package com.creativespacefinder.manhattan.controller;

import com.creativespacefinder.manhattan.dto.RecommendationRequest;
import com.creativespacefinder.manhattan.dto.RecommendationResponse;
import com.creativespacefinder.manhattan.dto.LocationRecommendationResponse; // Import LocationRecommendationResponse
import com.creativespacefinder.manhattan.entity.Activity;
import com.creativespacefinder.manhattan.service.LocationRecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to make HTTP requests to the controller

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean
    private LocationRecommendationService locationRecommendationService; // Mock the service layer

    @Test
    void getRecommendations_Success() throws Exception {
        // Prepare mock data for the service response
        LocalDateTime testDateTime = LocalDateTime.of(2025, 7, 10, 15, 0);

        // Create a mock LocationRecommendationResponse
        LocationRecommendationResponse location1 = new LocationRecommendationResponse(
                UUID.randomUUID(),
                "Central Park",
                BigDecimal.valueOf(40.785091),
                BigDecimal.valueOf(-73.968285),
                BigDecimal.valueOf(0.85), // culturalActivityScore
                BigDecimal.valueOf(0.92), // museScore
                BigDecimal.valueOf(0.70), // crowdScore
                500 // estimatedCrowdNumber
        );

        List<LocationRecommendationResponse> mockLocations = Collections.singletonList(location1);
        RecommendationResponse mockResponse = new RecommendationResponse(
                mockLocations,
                "Hiking",
                testDateTime.toString()
        );

        // Define the behavior of the mocked service
        // When getLocationRecommendations is called with any RecommendationRequest,
        // return our mockResponse.
        when(locationRecommendationService.getLocationRecommendations(any(RecommendationRequest.class)))
                .thenReturn(mockResponse);

        // Prepare the request body
        RecommendationRequest request = new RecommendationRequest("Hiking", testDateTime);
        String requestBodyJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and assert the results
        mockMvc.perform(post("/api/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print()) // Print request/response details for debugging
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.locations[0].locationName").value("Central Park"))
                .andExpect(jsonPath("$.locations[0].museScore").value(0.92))
                .andExpect(jsonPath("$.activity").value("Hiking"))
                .andExpect(jsonPath("$.dateTime").value(testDateTime.toString()));
    }

    @Test
    void getRecommendations_ServiceThrowsException() throws Exception {
        // Define the behavior of the mocked service to throw an exception
        when(locationRecommendationService.getLocationRecommendations(any(RecommendationRequest.class)))
                .thenThrow(new RuntimeException("Service error during recommendation"));

        LocalDateTime testDateTime = LocalDateTime.of(2025, 7, 10, 15, 0);
        RecommendationRequest request = new RecommendationRequest("Hiking", testDateTime);
        String requestBodyJson = objectMapper.writeValueAsString(request);

        // Perform the POST request and expect an Internal Server Error
        mockMvc.perform(post("/api/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllActivities_Success() throws Exception {
        // Prepare mock data for activities
        Activity activity1 = new Activity();
        activity1.setId(UUID.randomUUID());
        activity1.setName("Hiking");

        Activity activity2 = new Activity();
        activity2.setId(UUID.randomUUID());
        activity2.setName("Cycling");

        List<Activity> mockActivities = Arrays.asList(activity1, activity2);

        // Define the behavior of the mocked service
        when(locationRecommendationService.getAllActivities()).thenReturn(mockActivities);

        // Perform the GET request and assert the results
        mockMvc.perform(get("/api/recommendations/activities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hiking"))
                .andExpect(jsonPath("$[1].name").value("Cycling"));
    }

    @Test
    void healthCheck_Success() throws Exception {
        // No mocking needed as healthCheck method in controller is self-contained
        mockMvc.perform(get("/api/recommendations/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Creative Space Finder API is running."));
    }
}