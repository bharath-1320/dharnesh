package com.creativespacefinder.manhattan;

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

    @MockBean
    private WeatherForecastService weatherForecastService;

    @Test
    void contextLoads() {
    }
}
