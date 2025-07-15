package com.creativespacefinder.manhattan.service;

import com.creativespacefinder.manhattan.dto.LocationRecommendationResponse;
import com.creativespacefinder.manhattan.dto.PredictionResponse;
import com.creativespacefinder.manhattan.dto.RecommendationRequest;
import com.creativespacefinder.manhattan.dto.RecommendationResponse;
import com.creativespacefinder.manhattan.entity.Activity;
import com.creativespacefinder.manhattan.entity.LocationActivityScore;
import com.creativespacefinder.manhattan.entity.MLPredictionLog;
import com.creativespacefinder.manhattan.repository.ActivityRepository;
import com.creativespacefinder.manhattan.repository.LocationActivityScoreRepository;
import com.creativespacefinder.manhattan.repository.MLPredictionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationRecommendationService {

    @Autowired
    private LocationActivityScoreRepository locationActivityScoreRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private MLPredictionLogRepository mlPredictionLogRepository;

    @Transactional
    public RecommendationResponse getLocationRecommendations(RecommendationRequest request) {
        LocalDateTime requestDateTime = request.getDateTime();
        String activityName = request.getActivity();

        Activity activity = activityRepository.findByName(activityName)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityName));

        // Fetch top candidates (250) to increase pool size
        // Consider increasing this number if you still get too few unique locations
        List<LocationActivityScore> candidates =
                locationActivityScoreRepository.findTopByActivityNameIgnoreDateTime(
                        activityName, PageRequest.of(0, 200)); // You can increase this to 500 or 1000 if needed

        if (candidates.isEmpty()) {
            return new RecommendationResponse(Collections.emptyList(), activityName, requestDateTime.toString());
        }

        // --- Batch ML Model Prediction ---
        List<Map<String, Object>> mlRequestBodies = new ArrayList<>();
        for (LocationActivityScore score : candidates) {
            Map<String, Object> body = new HashMap<>();
            body.put("latitude", score.getLocation().getLatitude().doubleValue());
            body.put("longitude", score.getLocation().getLongitude().doubleValue());
            body.put("hour", requestDateTime.getHour());
            body.put("month", requestDateTime.getMonthValue());
            body.put("day", requestDateTime.getDayOfMonth());
            body.put("cultural_activity_prefered", activityName);
            mlRequestBodies.add(body);
        }

        // Call ML model once for all candidates
        PredictionResponse[] predictions = callMLModelBatch(mlRequestBodies);

        // --- Process Predictions and Update Scores ---
        // changed for unit testing
        int limit = Math.min(predictions.length, candidates.size());

        Map<UUID, BigDecimal> mlCulturalScores = new HashMap<>();
        for (int i = 0; i < limit; i++) {                                      // bound‑checked loop
            LocationActivityScore score = candidates.get(i);
            PredictionResponse   p     = predictions[i];

            BigDecimal culturalScore = BigDecimal.valueOf(p.getCreativeActivityScore());
            BigDecimal crowdScore    = BigDecimal.valueOf(p.getCrowdScore());
            BigDecimal museScore     = culturalScore.multiply(BigDecimal.valueOf(0.6))
                    .add(crowdScore.multiply(BigDecimal.valueOf(0.4)));

            score.setCulturalActivityScore(culturalScore);
            score.setCrowdScore(crowdScore);
            score.setEstimatedCrowdNumber(p.getEstimatedCrowdNumber());
            score.setMuseScore(museScore);

            mlCulturalScores.put(score.getLocation().getId(), culturalScore);
        }
        candidates = candidates.subList(0, limit);

        // --- Batch Database Update ---
        locationActivityScoreRepository.saveAll(candidates); // Save all updated scores in one go

        // Log batch
        MLPredictionLog log = new MLPredictionLog();
        log.setId(UUID.randomUUID());
        log.setModelVersion("1.0");
        log.setPredictionType("location_recommendation");
        log.setRecordsProcessed(candidates.size());
        log.setRecordsUpdated(candidates.size()); // All candidates processed and updated
        log.setPredictionDate(OffsetDateTime.now());
        mlPredictionLogRepository.save(log);

        // Sort all candidates by museScore
        List<LocationActivityScore> sorted = candidates.stream()
                .sorted(Comparator.comparing(LocationActivityScore::getMuseScore).reversed())
                .collect(Collectors.toList());

        // Map to DTOs
        List<LocationRecommendationResponse> mapped = sorted.stream()
                .map(score -> new LocationRecommendationResponse(
                        score.getLocation().getId(),
                        score.getLocation().getLocationName(),
                        score.getLocation().getLatitude(),
                        score.getLocation().getLongitude(),
                        mlCulturalScores.get(score.getLocation().getId()),
                        score.getMuseScore(),
                        score.getCrowdScore(),
                        score.getEstimatedCrowdNumber()
                ))
                .collect(Collectors.toList());

        // Deduplicate by lat/lon keeping the highest museScore for each coordinate pair
        Map<String, LocationRecommendationResponse> bestPerCoord = new HashMap<>();
        for (LocationRecommendationResponse loc : mapped) {
            String key = loc.getLatitude().toPlainString() + ":" + loc.getLongitude().toPlainString(); // Use toPlainString for consistent keys
            bestPerCoord.merge(key, loc, (existing, incoming) ->
                    incoming.getMuseScore().compareTo(existing.getMuseScore()) > 0 ? incoming : existing
            );
        }

        // Take top 10 of the deduplicated values
        List<LocationRecommendationResponse> top10 = bestPerCoord.values().stream()
                .sorted(Comparator.comparing(LocationRecommendationResponse::getMuseScore).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return new RecommendationResponse(top10, activityName, requestDateTime.toString());
    }

    // Modified to send a list of requests and receive a list of predictions
    // changed from private to protected by dharnesh for unit testing
    protected PredictionResponse[] callMLModelBatch(List<Map<String, Object>> requestBodies) {
        RestTemplate rest = new RestTemplate();
        String url = "http://localhost:8000/predict_batch"; // New endpoint for batch prediction
        return rest.postForObject(url, requestBodies, PredictionResponse[].class );
    }

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public List<LocalDate> getAvailableDates(String activityName) {
        return locationActivityScoreRepository.findAvailableDatesByActivity(activityName);
    }

    public List<LocalTime> getAvailableTimes(String activityName, LocalDate date) {
        return locationActivityScoreRepository.findAvailableTimesByActivityAndDate(activityName, date);
    }
}
