package com.creativespacefinder.manhattan;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // This is the missing import

@SpringBootTest
@ActiveProfiles("test")
class CreativeSpaceFinderApplicationTests {

    @Test
    void contextLoads() {
    }

}
