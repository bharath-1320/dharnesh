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
    
    // Query to find top 5 ML-predicted scores
    List<LocationActivityScore> findTop5ByActivityNameAndEventDateAndEventTimeOrderByMuseScoreDesc(String activityName, LocalDate eventDate, LocalTime eventTime);

    // Corrected query to find historical data only (where museScore is null)
    @Query("SELECT las FROM LocationActivityScore las WHERE las.activity.name = :activityName AND las.museScore IS NULL ORDER BY las.historicalActivityScore DESC")
    List<LocationActivityScore> findTopByActivityNameIgnoreDateTime(@Param("activityName") String activityName, Pageable pageable);

    // Other existing queries
    @Query("SELECT DISTINCT las.eventDate FROM LocationActivityScore las WHERE las.activity.name = :activityName")
    List<LocalDate> findAvailableDatesByActivity(String activityName);

    @Query("SELECT DISTINCT las.eventTime FROM LocationActivityScore las WHERE las.activity.name = :activityName AND las.eventDate = :eventDate")
    List<LocalTime> findAvailableTimesByActivityAndDate(String activityName, LocalDate eventDate);

    @Query("SELECT COUNT(las) FROM LocationActivityScore las WHERE las.museScore IS NOT NULL")
    Long countRecordsWithMLPredictions();

    @Query("SELECT COUNT(las) FROM LocationActivityScore las WHERE las.historicalActivityScore IS NOT NULL")
    Long countRecordsWithHistoricalData();
}