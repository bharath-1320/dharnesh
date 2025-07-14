package com.creativespacefinder.manhattan.repository;

import com.creativespacefinder.manhattan.entity.LocationActivityScore;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LocationActivityScoreRepository extends JpaRepository<LocationActivityScore, UUID> {

    // Top 5 results by activity, date, and time (ordered by MuseScore)
    @Query("""
        SELECT l FROM LocationActivityScore l
        WHERE l.activity.name = :activityName
          AND l.eventDate = :eventDate
          AND l.eventTime = :eventTime
        ORDER BY l.museScore DESC NULLS LAST
        """)
    List<LocationActivityScore> findTop10ByActivityNameAndEventDateAndEventTimeOrderByMuseScoreDesc(
            @Param("activityName") String activityName,
            @Param("eventDate") LocalDate eventDate,
            @Param("eventTime") LocalTime eventTime
    );

    // Query to fetch unique locations by coordinates, ordered by historicalActivityScore
    // This returns a projection of LocationActivityScore entities, ensuring uniqueness by location_id
    @Query("""
        SELECT las FROM LocationActivityScore las
        WHERE las.activity.name = :activityName
        GROUP BY las.location.id, las.id, las.activity.id, las.taxiZone.id, las.eventDate, las.eventTime, las.historicalTaxiZoneCrowdScore, las.historicalActivityScore, las.culturalActivityScore, las.crowdScore, las.museScore, las.estimatedCrowdNumber, las.mlPredictionDate, las.createdAt, las.updatedAt, las.eventId
        ORDER BY las.historicalActivityScore DESC NULLS LAST
        """)
    List<LocationActivityScore> findTopByActivityNameIgnoreDateTime(
            @Param("activityName") String activityName,
            Pageable pageable
    );

    // Available dates for the activity
    @Query("SELECT DISTINCT l.eventDate FROM LocationActivityScore l WHERE l.activity.name = :activityName")
    List<LocalDate> findAvailableDatesByActivity(@Param("activityName") String activityName);

    // Available times for the activity on a given date
    @Query("SELECT DISTINCT l.eventTime FROM LocationActivityScore l WHERE l.activity.name = :activityName AND l.eventDate = :eventDate")
    List<LocalTime> findAvailableTimesByActivityAndDate(
            @Param("activityName") String activityName,
            @Param("eventDate") LocalDate eventDate
    );

    // Count records with ML predictions
    @Query("SELECT COUNT(l) FROM LocationActivityScore l WHERE l.museScore IS NOT NULL")
    Long countRecordsWithMLPredictions();

    // Count records with historical data
    @Query("SELECT COUNT(l) FROM LocationActivityScore l WHERE l.historicalActivityScore IS NOT NULL")
    Long countRecordsWithHistoricalData();

    // used by LocationRecommendationService to load all relevant records
    List<LocationActivityScore> findByActivityIdAndEventDateAndEventTime(
            UUID activityId,
            LocalDate eventDate,
            LocalTime eventTime
    );
}
