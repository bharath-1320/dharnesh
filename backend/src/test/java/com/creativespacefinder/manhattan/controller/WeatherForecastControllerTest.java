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
        ForecastResponse mockForecastResponse = new ForecastResponse();
        ForecastResponse.HourlyForecast hourlyForecast = new ForecastResponse.HourlyForecast();

        // Corrected section
        ForecastResponse.HourlyForecast.TempInfo tempInfo = new ForecastResponse.HourlyForecast.TempInfo();
        tempInfo.setTemp(25.0);
        
        hourlyForecast.setTempInfo(tempInfo);

        ForecastResponse.Weather weather = new ForecastResponse.Weather();
        weather.setMain("Clouds");
        hourlyForecast.setWeather(Collections.singletonList(weather));

        mockForecastResponse.setHourly(Collections.singletonList(hourlyForecast));

        when(weatherForecastService.get96HourForecast()).thenReturn(mockForecastResponse);

        mockMvc.perform(get("/api/forecast"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].main.temp").value(25.0))
                .andExpect(jsonPath("$.list[0].weather[0].main").value("Clouds"));
    }

    @Test
    void getForecast_ServiceThrowsException() throws Exception {
        when(weatherForecastService.get96HourForecast()).thenThrow(new RuntimeException("API Down"));

        mockMvc.perform(get("/api/forecast"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}