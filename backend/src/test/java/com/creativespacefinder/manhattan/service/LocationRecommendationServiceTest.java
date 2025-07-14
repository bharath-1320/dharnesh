package com.creativespacefinder.manhattan.service;

import com.creativespacefinder.manhattan.dto.RecommendationRequest;
import com.creativespacefinder.manhattan.dto.RecommendationResponse;
import com.creativespacefinder.manhattan.dto.WeatherData; // Import WeatherData
import com.creativespacefinder.manhattan.repository.ActivityRepository;
import com.creativespacefinder.manhattan.repository.LocationActivityScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable; // Import Pageable

import java.time.LocalDate; // Added import for LocalDate
import java.time.LocalDateTime;
import java.time.LocalTime; // Added import for LocalTime
import java.util.Collections;
import java.math.BigDecimal; // Import BigDecimal

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify; // This is the corrected import

@ExtendWith(MockitoExtension.class)
public class LocationRecommendationServiceTest {

    @Mock
    private LocationActivityScoreRepository locationActivityScoreRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private WeatherForecastService weatherForecastService;

    @InjectMocks
    private LocationRecommendationService locationRecommendationService;

    private RecommendationRequest testRequest;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2025, 7, 10, 15, 0);
        testRequest = new RecommendationRequest("Hiking", testDateTime);
    }

    @Test
    void testGetLocationRecommendations_NoMLDataFound() {
        // Mock the repository to return an empty list, simulating no ML data
        when(locationActivityScoreRepository.findTop10ByActivityNameAndEventDateAndEventTimeOrderByMuseScoreDesc( // CHANGED findTop5 to findTop10
                anyString(), any(LocalDate.class), any(LocalTime.class))) // CHANGED any() to any(LocalDate.class) and any(LocalTime.class)
                .thenReturn(Collections.emptyList());

        // Mock the historical fallback to also return empty, explicitly using any(Pageable.class)
        when(locationActivityScoreRepository.findTopByActivityNameIgnoreDateTime(
                anyString(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        // Mock weather service call
        when(weatherForecastService.getWeatherForDateTime(testDateTime))
                .thenReturn(new WeatherData( // Using fully qualified name for clarity
                        testDateTime, BigDecimal.valueOf(70.0), "Sunny", "clear sky", "2025-07-10 15:00"));

        // Call the service method
        RecommendationResponse response = locationRecommendationService.getLocationRecommendations(testRequest);

        // Assertions
        assertNotNull(response);
        assertTrue(response.getLocations().isEmpty());

        // Verify that the mocked methods were called as expected, explicitly using any(Pageable.class)
        verify(locationActivityScoreRepository).findTop10ByActivityNameAndEventDateAndEventTimeOrderByMuseScoreDesc( // CHANGED findTop5 to findTop10
                anyString(), any(LocalDate.class), any(LocalTime.class)); // CHANGED any() to any(LocalDate.class) and any(LocalTime.class)
        verify(locationActivityScoreRepository).findTopByActivityNameIgnoreDateTime(anyString(), any(Pageable.class));
        verify(weatherForecastService).getWeatherForDateTime(testDateTime);
    }

    // Add more @Test methods here for other scenarios (e.g., data found, errors, etc.)
}