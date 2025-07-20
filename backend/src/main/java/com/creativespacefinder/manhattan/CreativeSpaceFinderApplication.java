package com.creativespacefinder.manhattan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
<<<<<<< Updated upstream


@SpringBootApplication
@EnableCaching
=======
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
>>>>>>> Stashed changes
public class CreativeSpaceFinderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreativeSpaceFinderApplication.class, args);
    }
<<<<<<< Updated upstream
}

=======
}
>>>>>>> Stashed changes
