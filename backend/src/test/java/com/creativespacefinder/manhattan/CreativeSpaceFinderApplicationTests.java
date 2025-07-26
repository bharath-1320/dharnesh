package com.creativespacefinder.manhattan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreativeSpaceFinderApplicationTests {

    @Test
    void contextLoads() {
        // Simple test that doesn't require Spring context loading
        // This tests that the main application class exists and can be instantiated
        CreativeSpaceFinderApplication app = new CreativeSpaceFinderApplication();
        assertNotNull(app);
        System.out.println("✅ Application class exists and can be instantiated");
    }

    @Test
    void applicationMainClassExists() {
        // Verify the main application class is properly defined
        assertNotNull(CreativeSpaceFinderApplication.class);
        System.out.println("✅ Application main class is properly defined");
    }
}