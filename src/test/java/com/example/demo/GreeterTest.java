package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test verifying the application context loads using the H2 test profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class GreeterTest {

    @Test
    void contextLoads() {
    }
}
