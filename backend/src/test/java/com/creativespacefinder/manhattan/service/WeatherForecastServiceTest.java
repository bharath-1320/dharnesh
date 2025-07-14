package com.creativespacefinder.manhattan.service;

import com.creativespacefinder.manhattan.dto.ForecastResponse;
import com.creativespacefinder.manhattan.dto.WeatherData;
import com.creativespacefinder.manhattan.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant; // Ensure this import is present
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherForecastServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherForecastService weatherForecastService;

    private ObjectMapper objectMapper;
    private static final String MOCK_API_KEY = "test_api_key";
    private static final double LAT = 40.7831;
    private static final double LON = -73.9662;
    private static final String BASE_URL = "https://pro.openweathermap.org/data/2.5/forecast/hourly";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        ReflectionTestUtils.setField(weatherForecastService, "apiKey", MOCK_API_KEY);
    }

    // --- Test get96HourForecast() ---
    @Test
    void get96HourForecast_Success() throws Exception {
        // Prepare a mock JSON string that can be deserialized into ForecastResponse
        // This is the key to avoid directly setting private fields.
        long currentEpochSecond = Instant.now().getEpochSecond();
        String mockForecastJson = String.format("""
            {
                "list": [
                    {
                        "dt": %d,
                        "main": {"temp": 25.0},
                        "weather": [{"main": "Clouds", "description": "some clouds"}]
                    }
                ]
            }
            """, currentEpochSecond);

        ForecastResponse mockResponse = objectMapper.readValue(mockForecastJson, ForecastResponse.class);

        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(ForecastResponse.class)))
                .thenReturn(mockResponse);

        ForecastResponse result = weatherForecastService.get96HourForecast();

        assertNotNull(result);
        assertEquals(1, result.getHourly().size());
        assertEquals("Clouds", result.getHourly().get(0).getCondition());
        // Verify temp using the public getter
        assertEquals(25.0, result.getHourly().get(0).getTemp());
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(ForecastResponse.class));
    }

    @Test
    void get96HourForecast_HttpClientErrorException() {
        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(ForecastResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found", "Error Body".getBytes(), null));

        ApiException exception = assertThrows(ApiException.class, () -> {
            weatherForecastService.get96HourForecast();
        });

        assertTrue(exception.getMessage().contains("OpenWeather API call failed: 404 NOT_FOUND"));
        assertTrue(exception.getMessage().contains("- Error Body"));
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(ForecastResponse.class));
    }

    @Test
    void get96HourForecast_GenericException() {
        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(ForecastResponse.class)))
                .thenThrow(new RuntimeException("Network timeout"));

        ApiException exception = assertThrows(ApiException.class, () -> {
            weatherForecastService.get96HourForecast();
        });

        assertTrue(exception.getMessage().contains("Unexpected error while calling OpenWeather API: Network timeout"));
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(ForecastResponse.class));
    }

    // --- Test getAvailableForecastDateTimes() ---
    @Test
    void getAvailableForecastDateTimes_Success() throws Exception {
        String mockRawJson = """
            {
                "list": [
                    {"dt": 1720818000},
                    {"dt": 1720821600}
                ]
            }
            """;
        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn(mockRawJson);

        List<LocalDateTime> result = weatherForecastService.getAvailableForecastDateTimes();

        assertNotNull(result);
        assertEquals(2, result.size());

        LocalDateTime expectedDt1 = Instant.ofEpochSecond(1720818000L)
                                          .atZone(ZoneId.of("America/New_York"))
                                          .toLocalDateTime();
        LocalDateTime expectedDt2 = Instant.ofEpochSecond(1720821600L)
                                          .atZone(ZoneId.of("America/New_York"))
                                          .toLocalDateTime();

        assertEquals(expectedDt1, result.get(0));
        assertEquals(expectedDt2, result.get(1));
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(String.class));
    }

    @Test
    void getAvailableForecastDateTimes_InvalidJsonResponse() {
        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenReturn("{ \"data\": \"invalid\" }");

        ApiException exception = assertThrows(ApiException.class, () -> {
            weatherForecastService.getAvailableForecastDateTimes();
        });

        assertTrue(exception.getMessage().contains("Invalid response format from OpenWeather API"));
    }

    @Test
    void getAvailableForecastDateTimes_RestTemplateThrowsException() {
        String expectedUrl = String.format("%s?lat=%f&lon=%f&appid=%s&units=imperial", BASE_URL, LAT, LON, MOCK_API_KEY);

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        ApiException exception = assertThrows(ApiException.class, () -> {
            weatherForecastService.getAvailableForecastDateTimes();
        });

        assertTrue(exception.getMessage().contains("Failed to extract forecast datetimes: Connection refused"));
    }


    // --- Test getWeatherForDateTime() and findWeatherForDateTime() ---
    @Test
    void getWeatherForDateTime_Success() throws Exception {
        LocalDateTime targetDateTime = LocalDateTime.of(2025, 7, 15, 10, 0); // Example target time
        long targetEpochSecond = targetDateTime.atZone(ZoneId.of("America/New_York")).toEpochSecond();

        // Prepare a mock JSON string for ForecastResponse with target time
        String mockForecastJson = String.format("""
            {
                "list": [
                    {
                        "dt": %d,
                        "main": {"temp": 20.0},
                        "weather": [{"main": "Rain", "description": "light rain"}]
                    },
                    {
                        "dt": %d,
                        "main": {"temp": 28.5},
                        "weather": [{"main": "Clear", "description": "clear sky"}]
                    },
                    {
                        "dt": %d,
                        "main": {"temp": 30.0},
                        "weather": [{"main": "Clouds", "description": "overcast clouds"}]
                    }
                ]
            }
            """, targetEpochSecond - 3600, targetEpochSecond, targetEpochSecond + 3600);

        ForecastResponse mockForecast = objectMapper.readValue(mockForecastJson, ForecastResponse.class);

        doReturn(mockForecast).when(restTemplate).getForObject(anyString(), eq(ForecastResponse.class));

        WeatherData result = weatherForecastService.getWeatherForDateTime(targetDateTime);

        assertNotNull(result);
        assertEquals(targetDateTime, result.getDateTime());
        assertEquals(new BigDecimal("28.5"), result.getTemperature());
        assertEquals("Clear", result.getCondition());
        assertEquals("clear sky", result.getDescription());
        assertEquals(targetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), result.getFormattedDateTime());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ForecastResponse.class));
    }

    @Test
    void getWeatherForDateTime_TargetNotFound_ReturnsDefault() throws Exception {
        LocalDateTime targetDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
        
        // Prepare a mock JSON string for ForecastResponse that DOES NOT contain the target time
        String mockForecastJson = """
            {
                "list": [
                    {
                        "dt": 1720900000,
                        "main": {"temp": 20.0},
                        "weather": [{"main": "Rain", "description": "light rain"}]
                    }
                ]
            }
            """;
        ForecastResponse mockForecast = objectMapper.readValue(mockForecastJson, ForecastResponse.class);

        doReturn(mockForecast).when(restTemplate).getForObject(anyString(), eq(ForecastResponse.class));

        WeatherData result = weatherForecastService.getWeatherForDateTime(targetDateTime);

        assertNotNull(result);
        assertEquals(targetDateTime, result.getDateTime());
        assertEquals(new BigDecimal("70.0"), result.getTemperature());
        assertEquals("Clear", result.getCondition());
        assertEquals("clear sky", result.getDescription());
        assertEquals(targetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), result.getFormattedDateTime());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ForecastResponse.class));
    }

    @Test
    void getWeatherForDateTime_Get96HourForecastThrowsException_ReturnsDefault() {
        LocalDateTime targetDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);

        when(restTemplate.getForObject(anyString(), eq(ForecastResponse.class)))
                .thenThrow(new RuntimeException("API is down"));

        WeatherData result = weatherForecastService.getWeatherForDateTime(targetDateTime);

        assertNotNull(result);
        assertEquals(targetDateTime, result.getDateTime());
        assertEquals(new BigDecimal("70.0"), result.getTemperature());
        assertEquals("Clear", result.getCondition());
        assertEquals("clear sky", result.getDescription());
        assertEquals(targetDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), result.getFormattedDateTime());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(ForecastResponse.class));
    }
}