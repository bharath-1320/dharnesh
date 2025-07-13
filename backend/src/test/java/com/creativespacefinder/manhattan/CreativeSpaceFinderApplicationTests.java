// // package com.creativespacefinder.manhattan;

// // import com.creativespacefinder.manhattan.repository.WeatherCacheRepository;
// // import com.creativespacefinder.manhattan.service.WeatherForecastService;
// // import org.junit.jupiter.api.Test;
// // import org.springframework.boot.test.context.SpringBootTest;
// // import org.springframework.boot.test.mock.mockito.MockBean;
// // import org.springframework.test.context.ActiveProfiles;
// // import org.springframework.test.context.TestPropertySource;

// // @SpringBootTest
// // @ActiveProfiles("test")
// // @TestPropertySource(properties = {
// //     "openweather.api-key=test-api-key"
// // })
// // class CreativeSpaceFinderApplicationTests {

// //     // Mocking external services to ensure the context loads without network calls
// //     @MockBean
// //     private WeatherForecastService weatherForecastService;

// //     // Mocking the repository to prevent the test from connecting to a real DB
// //     @MockBean
// //     private WeatherCacheRepository weatherCacheRepository;

// //     @Test
// //     void contextLoads() {
// //         // This test will now succeed if the application context can load
// //     }
// // }


// package com.creativespacefinder.manhattan;

// import com.creativespacefinder.manhattan.repository.WeatherCacheRepository;
// import com.creativespacefinder.manhattan.service.WeatherForecastService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.env.Environment;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @ActiveProfiles("test") // ✅ Tells Spring to use the "test" profile and load application.yaml from test/resources
// class CreativeSpaceFinderApplicationTests {

//     @MockBean
//     private WeatherForecastService weatherForecastService;

//     @MockBean
//     private WeatherCacheRepository weatherCacheRepository;

//     @Autowired
//     private Environment env;

//     @Test
//     void contextLoads() {
//         System.out.println("✅ Active Profiles: " + String.join(", ", env.getActiveProfiles()));
//     }
// }

package com.creativespacefinder.manhattan;

import com.creativespacefinder.manhattan.repository.WeatherCacheRepository;
import com.creativespacefinder.manhattan.service.WeatherForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // ✅ This is what tells Spring to load application-test.yaml
@TestPropertySource("classpath:application-test.yaml") 
class CreativeSpaceFinderApplicationTests {

    @MockBean
    private WeatherForecastService weatherForecastService;

    @MockBean
    private WeatherCacheRepository weatherCacheRepository;

    @Autowired
    Environment env;

    @Test
    void contextLoads() {
        System.out.println("✅ Active Profiles: " + String.join(", ", env.getActiveProfiles()));
        System.out.println("🔑 openweather.api-key: " + env.getProperty("openweather.api-key"));
        System.out.println("🔗 spring.datasource.url: " + env.getProperty("spring.datasource.url"));
    }
}
