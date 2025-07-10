package com.creativespacefinder.manhattan;

import com.creativespacefinder.manhattan.repository.WeatherCacheRepository;
import com.creativespacefinder.manhattan.service.WeatherForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "openweather.api-key=test-api-key"
})
class CreativeSpaceFinderApplicationTests {

    // Mocking external services to ensure the context loads without network calls
    @MockBean
    private WeatherForecastService weatherForecastService;

    // Mocking the repository to prevent the test from connecting to a real DB
    @MockBean
    private WeatherCacheRepository weatherCacheRepository;

    @Test
    void contextLoads() {
        // This test will now succeed if the application context can load
    }
}
