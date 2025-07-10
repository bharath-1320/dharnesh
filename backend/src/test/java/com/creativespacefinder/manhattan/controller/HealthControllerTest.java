package com.creativespacefinder.manhattan.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest is used for testing Spring MVC controllers.
// It will auto-configure MockMvc and only scan controller-related beans.
@WebMvcTest(HealthController.class)
public class HealthControllerTest {

    // MockMvc is used to perform requests and assert responses
    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthCheckReturnsStatusUp() throws Exception {
        mockMvc.perform(get("/api/health")) // Perform a GET request to /api/health
                .andExpect(status().isOk()) // Expect HTTP 200 OK status
                .andExpect(jsonPath("$.status").value("UP")) // Expect JSON field 'status' to be 'UP'
                .andExpect(jsonPath("$.service").value("manhattan-muse")); // Expect JSON field 'service' to be 'manhattan-muse'
    }
}