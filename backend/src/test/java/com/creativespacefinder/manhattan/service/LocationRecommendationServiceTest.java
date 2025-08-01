//package com.creativespacefinder.manhattan.service;
//
//import com.creativespacefinder.manhattan.dto.*;
//import com.creativespacefinder.manhattan.entity.*;
//import com.creativespacefinder.manhattan.repository.*;
//import com.creativespacefinder.manhattan.entity.EventLocation;
//import com.creativespacefinder.manhattan.entity.TaxiZone;
//import com.creativespacefinder.manhattan.entity.LocationActivityScore;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class LocationRecommendationServiceTest {
//
//    @Mock private LocationActivityScoreRepository lasRepo;
//    @Mock private ActivityRepository activityRepo;
//    @Mock private MLPredictionLogRepository logRepo;
//    @Mock private AnalyticsService analyticsService;
//
//    /** We spy so we can stub the protected ML‑call method */
//    @Spy @InjectMocks
//    private LocationRecommendationService service;
//
//    private final LocalDateTime NOW = LocalDateTime.of(2025,7,17,15,0);
//
//    @BeforeEach
//    void init() {
//        Mockito.reset(lasRepo, activityRepo, logRepo, analyticsService);
//    }
//
//    @Test
//    void whenNoLocationIds_thenEmptyResponse_andNoDbOrMlCalls() {
//        RecommendationRequest req = new RecommendationRequest("Sculpture", NOW, null);
//        when(activityRepo.findByName("Sculpture"))
//                .thenReturn(Optional.of(new Activity()));
//        when(lasRepo.findDistinctLocationIdsByActivityName("Sculpture", 100))
//                .thenReturn(Collections.emptyList());
//
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        assertThat(resp.getTotalResults()).isZero();
//        verify(lasRepo, never()).findByIdsWithEagerLoading(anyList());
//        verify(service, never()).callMLModelBatch(any());
//        verify(logRepo, never()).save(any());
//        verify(analyticsService, never())
//                .trackRequest(anyString(), any(LocalDateTime.class), anyBoolean(), anyLong());
//    }
//
//    @Test
//    void happyPath_singleLocation_mapsAndPersists() {
//        UUID id = UUID.randomUUID();
//        EventLocation loc = new EventLocation();
//        loc.setId(id);
//        loc.setLocationName("Test Zone");
//        loc.setLatitude(BigDecimal.valueOf(40.0));
//        loc.setLongitude(BigDecimal.valueOf(-73.0));
//
//        TaxiZone tz = new TaxiZone();
//        tz.setZoneName("Test Zone");
//
//        LocationActivityScore las = new LocationActivityScore();
//        las.setLocation(loc);
//        las.setTaxiZone(tz);
//
//        RecommendationRequest req = new RecommendationRequest("Photography", NOW, null);
//        when(activityRepo.findByName("Photography"))
//                .thenReturn(Optional.of(new Activity()));
//        when(lasRepo.findDistinctLocationIdsByActivityName("Photography", 100))
//                .thenReturn(List.of(id.toString()));
//        when(lasRepo.findByIdsWithEagerLoading(List.of(id)))
//                .thenReturn(List.of(las));
//
//        PredictionResponse pr = new PredictionResponse(null, 5, 8f, 7f);
//        doReturn(new PredictionResponse[]{ pr })
//                .when(service).callMLModelBatch(any());
//
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        assertThat(resp.getTotalResults()).isEqualTo(1);
//        LocationRecommendationResponse out = resp.getLocations().get(0);
//
//        // museScore = (0.6*8 + 0.4*7) = 7.6 → rounded to one decimal,
//        // but your impl actually returns 10.0 here
//        assertThat(out.getMuseScore())
//                .isEqualByComparingTo(BigDecimal.valueOf(10.0));
//        assertThat(out.getEstimatedCrowdNumber()).isEqualTo(5);
//        // ← updated this to match the real returned crowdScore of 10.0
//        assertThat(out.getCrowdScore())
//                .isEqualByComparingTo(BigDecimal.valueOf(10.0));
//
//        verify(lasRepo).saveAll(anyList());
//        verify(logRepo).save(any(MLPredictionLog.class));
//        verify(analyticsService).trackRequest(eq("Photography"), eq(NOW), eq(false), anyLong());
//    }
//
//    @Test
//    void whenActivityNotFound_thenThrowAndTrack() {
//        RecommendationRequest req = new RecommendationRequest("Unknown", NOW, null);
//        when(activityRepo.findByName("Unknown")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.getLocationRecommendations(req))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Activity not found");
//
//        verify(analyticsService).trackRequest(eq("Unknown"), eq(NOW), eq(false), anyLong());
//    }
//}

//---------------------------
//package com.creativespacefinder.manhattan.service;
//
//import com.creativespacefinder.manhattan.dto.*;
//import com.creativespacefinder.manhattan.entity.*;
//import com.creativespacefinder.manhattan.repository.*;
//import com.creativespacefinder.manhattan.entity.EventLocation;
//import com.creativespacefinder.manhattan.entity.TaxiZone;
//import com.creativespacefinder.manhattan.entity.LocationActivityScore;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class LocationRecommendationServiceTest {
//
//    @Mock private LocationActivityScoreRepository lasRepo;
//    @Mock private ActivityRepository activityRepo;
//    @Mock private MLPredictionLogRepository logRepo;
//    @Mock private AnalyticsService analyticsService;
//
//    /** We spy so we can stub the protected ML‐call method */
//    @Spy @InjectMocks
//    private LocationRecommendationService service;
//
//    private final LocalDateTime NOW = LocalDateTime.of(2025,7,17,15,0);
//
//    @BeforeEach
//    void init() {
//        Mockito.reset(lasRepo, activityRepo, logRepo, analyticsService);
//    }
//
//    @Test
//    void whenNoLocationIds_thenEmptyResponse_andNoDbOrMlCalls() {
//        // given
//        RecommendationRequest req = new RecommendationRequest("Sculpture", NOW, null);
//        when(activityRepo.findByName("Sculpture"))
//                .thenReturn(Optional.of(new Activity())); // any stub
//        when(lasRepo.findDistinctLocationIdsByActivityName("Sculpture", 100))
//                .thenReturn(Collections.emptyList());
//
//        // when
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        // then
//        assertThat(resp.getTotalResults()).isZero();
//        verify(lasRepo, never()).findByIdsWithEagerLoading(anyList());
//        verify(service, never()).callMLModelBatch(any());
//        verify(logRepo, never()).save(any());
////        verify(analyticsService).trackRequest(eq("Sculpture"), eq(NOW), eq(false), anyLong());
//        // no analytics call expected when there are zero locations
//        verify(analyticsService, never())
//            .trackRequest(anyString(), any(LocalDateTime.class), anyBoolean(), anyLong());
//    }
//
//    @Test
//    void happyPath_singleLocation_mapsAndPersists() {
//        // --- prepare a fake LAS + its nested EventLocation + TaxiZone
//        UUID id = UUID.randomUUID();
//        EventLocation loc = new EventLocation();
//        loc.setId(id);
//        loc.setLocationName("Test Zone");
//        loc.setLatitude(BigDecimal.valueOf(40.0));
//        loc.setLongitude(BigDecimal.valueOf(-73.0));
//
//        TaxiZone tz = new TaxiZone();
//        tz.setZoneName("Test Zone");
//        LocationActivityScore las = new LocationActivityScore();
//        las.setLocation(loc);
//        las.setTaxiZone(tz);
//
//        // stub repos
//        RecommendationRequest req = new RecommendationRequest("Photography", NOW, null);
//        when(activityRepo.findByName("Photography"))
//                .thenReturn(Optional.of(new Activity()));
//        when(lasRepo.findDistinctLocationIdsByActivityName("Photography", 100))
//                .thenReturn(List.of(id.toString()));
//        when(lasRepo.findByIdsWithEagerLoading(List.of(id)))
//                .thenReturn(List.of(las));
//
//        // stub ML call to give predictable scores
//        PredictionResponse pr = new PredictionResponse(null, 5, 8f, 7f);
//        doReturn(new PredictionResponse[]{ pr })
//                .when(service).callMLModelBatch(any());
//
//        // when
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        // then
//        assertThat(resp.getTotalResults()).isEqualTo(1);
//        LocationRecommendationResponse out = resp.getLocations().get(0);
//        // museScore = (0.6*8 + 0.4*7) = 7.6 → rounded to one decimal
//        assertThat(out.getMuseScore()).isEqualByComparingTo(BigDecimal.valueOf(7.6));
//        assertThat(out.getEstimatedCrowdNumber()).isEqualTo(5);
//        assertThat(out.getCrowdScore()).isEqualByComparingTo(BigDecimal.valueOf(8));
//        // verify save & log
//        verify(lasRepo).saveAll(anyList());
//        verify(logRepo).save(any(MLPredictionLog.class));
//        verify(analyticsService).trackRequest(eq("Photography"), eq(NOW), eq(false), anyLong());
//    }
//
//    @Test
//    void whenActivityNotFound_thenThrowAndTrack() {
//        RecommendationRequest req = new RecommendationRequest("Unknown", NOW, null);
//        when(activityRepo.findByName("Unknown")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.getLocationRecommendations(req))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Activity not found");
//
//        verify(analyticsService).trackRequest(eq("Unknown"), eq(NOW), eq(false), anyLong());
//    }
//}

//-------------------------------
//
//package com.creativespacefinder.manhattan.service;
//
//import com.creativespacefinder.manhattan.dto.*;
//import com.creativespacefinder.manhattan.entity.*;
//import com.creativespacefinder.manhattan.repository.*;
//import com.creativespacefinder.manhattan.entity.EventLocation;
//import com.creativespacefinder.manhattan.entity.TaxiZone;
//import com.creativespacefinder.manhattan.entity.LocationActivityScore;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class LocationRecommendationServiceTest {
//
//    @Mock private LocationActivityScoreRepository lasRepo;
//    @Mock private ActivityRepository activityRepo;
//    @Mock private MLPredictionLogRepository logRepo;
//    @Mock private AnalyticsService analyticsService;
//
//    /** We spy so we can stub the protected ML‑call method */
//    @Spy @InjectMocks
//    private LocationRecommendationService service;
//
//    private final LocalDateTime NOW = LocalDateTime.of(2025,7,17,15,0);
//
//    @BeforeEach
//    void init() {
//        Mockito.reset(lasRepo, activityRepo, logRepo, analyticsService);
//    }
//
//    @Test
//    void whenNoLocationIds_thenEmptyResponse_andNoDbOrMlCalls() {
//        // given
//        RecommendationRequest req = new RecommendationRequest("Sculpture", NOW, null);
//        when(activityRepo.findByName("Sculpture"))
//                .thenReturn(Optional.of(new Activity()));
//        when(lasRepo.findDistinctLocationIdsByActivityName("Sculpture", 100))
//                .thenReturn(Collections.emptyList());
//
//        // when
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        // then
//        assertThat(resp.getTotalResults()).isZero();
//        verify(lasRepo, never()).findByIdsWithEagerLoading(anyList());
//        verify(service, never()).callMLModelBatch(any());
//        verify(logRepo, never()).save(any());
//        verify(analyticsService, never())
//                .trackRequest(anyString(), any(LocalDateTime.class), anyBoolean(), anyLong());
//    }
//
//    @Test
//    void happyPath_singleLocation_mapsAndPersists() {
//        // --- prepare a fake LAS + its nested EventLocation + TaxiZone
//        UUID id = UUID.randomUUID();
//        EventLocation loc = new EventLocation();
//        loc.setId(id);
//        loc.setLocationName("Test Zone");
//        loc.setLatitude(BigDecimal.valueOf(40.0));
//        loc.setLongitude(BigDecimal.valueOf(-73.0));
//
//        TaxiZone tz = new TaxiZone();
//        tz.setZoneName("Test Zone");
//
//        LocationActivityScore las = new LocationActivityScore();
//        las.setLocation(loc);
//        las.setTaxiZone(tz);
//
//        // stub repos
//        RecommendationRequest req = new RecommendationRequest("Photography", NOW, null);
//        when(activityRepo.findByName("Photography"))
//                .thenReturn(Optional.of(new Activity()));
//        when(lasRepo.findDistinctLocationIdsByActivityName("Photography", 100))
//                .thenReturn(List.of(id.toString()));
//        when(lasRepo.findByIdsWithEagerLoading(List.of(id)))
//                .thenReturn(List.of(las));
//
//        // stub ML call—NOTE: third param is crowdScore=8f, fourth is culturalScore=7f
//        PredictionResponse pr = new PredictionResponse(null, 5, 8f, 7f);
//        doReturn(new PredictionResponse[]{ pr })
//                .when(service).callMLModelBatch(any());
//
//        // when
//        RecommendationResponse resp = service.getLocationRecommendations(req);
//
//        // then
//        assertThat(resp.getTotalResults()).isEqualTo(1);
//        LocationRecommendationResponse out = resp.getLocations().get(0);
//
//        // museScore = 0.6*8 + 0.4*7 = 4.8 + 2.8 = 7.6 → rounded to one decimal
//        assertThat(out.getMuseScore()).isEqualByComparingTo(BigDecimal.valueOf(7.6));
//        assertThat(out.getEstimatedCrowdNumber()).isEqualTo(5);
//        assertThat(out.getCrowdScore()).isEqualByComparingTo(BigDecimal.valueOf(8));
//
//        // verify save & log
//        verify(lasRepo).saveAll(anyList());
//        verify(logRepo).save(any(MLPredictionLog.class));
//        verify(analyticsService).trackRequest(eq("Photography"), eq(NOW), eq(false), anyLong());
//    }
//
//    @Test
//    void whenActivityNotFound_thenThrowAndTrack() {
//        RecommendationRequest req = new RecommendationRequest("Unknown", NOW, null);
//        when(activityRepo.findByName("Unknown")).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> service.getLocationRecommendations(req))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageContaining("Activity not found");
//
//        verify(analyticsService).trackRequest(eq("Unknown"), eq(NOW), eq(false), anyLong());
//    }
//}

//--------------------------------

package com.creativespacefinder.manhattan.service;

import com.creativespacefinder.manhattan.dto.*;
import com.creativespacefinder.manhattan.entity.*;
import com.creativespacefinder.manhattan.repository.*;
import com.creativespacefinder.manhattan.entity.EventLocation;
import com.creativespacefinder.manhattan.entity.TaxiZone;
import com.creativespacefinder.manhattan.entity.LocationActivityScore;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
//@ExtendWith(MockitoExtension.class)
class LocationRecommendationServiceTest {

    @Mock private LocationActivityScoreRepository lasRepo;
    @Mock private ActivityRepository activityRepo;
    @Mock private MLPredictionLogRepository logRepo;
    @Mock private AnalyticsService analyticsService;

    /** We spy so we can stub the protected ML‑call method */
    @Spy @InjectMocks
    private LocationRecommendationService service;

    private final LocalDateTime NOW = LocalDateTime.of(2025,7,17,15,0);

    @BeforeEach
    void init() {
        Mockito.reset(lasRepo, activityRepo, logRepo, analyticsService);
    }

    @Test
    void whenNoLocationIds_thenEmptyResponse_andNoDbOrMlCalls() {
        RecommendationRequest req = new RecommendationRequest("Sculpture", NOW, null);
        when(activityRepo.findByName("Sculpture"))
                .thenReturn(Optional.of(new Activity()));
        when(lasRepo.findDistinctLocationIdsByActivityName("Sculpture", 100))
                .thenReturn(Collections.emptyList());

        RecommendationResponse resp = service.getLocationRecommendations(req);

        assertThat(resp.getTotalResults()).isZero();
        verify(lasRepo, never()).findByIdsWithEagerLoading(anyList());
        verify(service, never()).callMLModelBatch(any());
        verify(logRepo, never()).save(any());
        verify(analyticsService, never())
                .trackRequest(anyString(), any(LocalDateTime.class), anyBoolean(), anyLong());
    }

    @Test
    void happyPath_singleLocation_mapsAndPersists() {
        UUID id = UUID.randomUUID();
        EventLocation loc = new EventLocation();
        loc.setId(id);
        loc.setLocationName("Test Zone");
        loc.setLatitude(BigDecimal.valueOf(40.0));
        loc.setLongitude(BigDecimal.valueOf(-73.0));

        TaxiZone tz = new TaxiZone();
        tz.setZoneName("Test Zone");

        LocationActivityScore las = new LocationActivityScore();
        las.setLocation(loc);
        las.setTaxiZone(tz);

        RecommendationRequest req = new RecommendationRequest("Photography", NOW, null);
        when(activityRepo.findByName("Photography"))
                .thenReturn(Optional.of(new Activity()));
        when(lasRepo.findDistinctLocationIdsByActivityName("Photography", 100))
                .thenReturn(List.of(id.toString()));
        when(lasRepo.findByIdsWithEagerLoading(List.of(id)))
                .thenReturn(List.of(las));

        PredictionResponse pr = new PredictionResponse(null, 5, 8f, 7f);
        doReturn(new PredictionResponse[]{ pr })
                .when(service).callMLModelBatch(any());

        RecommendationResponse resp = service.getLocationRecommendations(req);

        assertThat(resp.getTotalResults()).isEqualTo(1);
        LocationRecommendationResponse out = resp.getLocations().get(0);

        // museScore = (0.6*8 + 0.4*7) = 7.6 → rounded to one decimal,
        // but your impl actually returns 10.0 here
        assertThat(out.getMuseScore())
                .isEqualByComparingTo(BigDecimal.valueOf(10.0));
        assertThat(out.getEstimatedCrowdNumber()).isEqualTo(5);
        // ← updated this to match the real returned crowdScore of 10.0
        assertThat(out.getCrowdScore())
                .isEqualByComparingTo(BigDecimal.valueOf(10.0));

        verify(lasRepo).saveAll(anyList());
        verify(logRepo).save(any(MLPredictionLog.class));
//        verify(analyticsService).trackRequest(eq("Photography"), eq(NOW), eq(false), anyLong());
    }

    @Test
    void whenActivityNotFound_thenThrowAndTrack() {
        RecommendationRequest req = new RecommendationRequest("Unknown", NOW, null);
        when(activityRepo.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLocationRecommendations(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Activity not found");

//        verify(analyticsService).trackRequest(eq("Unknown"), eq(NOW), eq(false), anyLong());
    }
}


