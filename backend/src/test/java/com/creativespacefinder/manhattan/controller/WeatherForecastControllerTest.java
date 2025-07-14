package com.creativespacefinder.manhattan.controller;

import com.creativespacefinder.manhattan.dto.ForecastResponse;
import com.creativespacefinder.manhattan.service.WeatherForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherForecastController.class)
@TestPropertySource(properties = "openweather.api-key=test-api-key-from-test-annotation")
class WeatherForecastControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherForecastService weatherForecastService;

    @Test
    void getForecast_Success() throws Exception {
        // We will create concrete instances for ForecastResponse and Weather,
        // but use Mockito for HourlyForecast to handle the 'main' (TempInfo) issue.

        // 1. Create Weather instance using its no-arg constructor and set 'main' using its setter
        ForecastResponse.Weather weather = new ForecastResponse.Weather();
        weather.setMain("Clouds");

        // 2. Mock the HourlyForecast object, and specify behavior for its getters
        // We need to mock getTemp() and getWeather() directly on HourlyForecast.
        ForecastResponse.HourlyForecast mockHourlyForecast = org.mockito.Mockito.mock(ForecastResponse.HourlyForecast.class);

        // Configure mockHourlyForecast to return the desired temp directly via its getTemp() method
        when(mockHourlyForecast.getTemp()).thenReturn(25.0);

        // Configure mockHourlyForecast to return the list of mock weather objects
        when(mockHourlyForecast.getWeather()).thenReturn(Collections.singletonList(weather));

        // You can optionally set 'dt' on the mock if needed, but it's not asserted in your test.
        // when(mockHourlyForecast.getDt()).thenReturn(System.currentTimeMillis() / 1000L);


        // 3. Create ForecastResponse instance and set its hourly list with the mocked HourlyForecast
        ForecastResponse mockForecastResponse = new ForecastResponse();
        mockForecastResponse.setHourly(Collections.singletonList(mockHourlyForecast));

        // Mock the service to return our prepared ForecastResponse
        when(weatherForecastService.get96HourForecast()).thenReturn(mockForecastResponse);

        mockMvc.perform(get("/api/forecast"))
                .andDo(print())
                .andExpect(status().isOk())
                // These jsonPath assertions now directly map to the mocked getters on mockHourlyForecast
                .andExpect(jsonPath("$.list[0].main.temp").value(25.0)) // This checks HourlyForecast.getTemp() indirectly
                .andExpect(jsonPath("$.list[0].weather[0].main").value("Clouds")); // This checks HourlyForecast.getWeather().get(0).getMain()
    }

    @Test
    void getForecast_ServiceThrowsException() throws Exception {
        when(weatherForecastService.get96HourForecast()).thenThrow(new RuntimeException("API Down"));

        mockMvc.perform(get("/api/forecast"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}