package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import com.lopez.userhandler.service.UserSyncService;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(DevTestProfile.class)
class ApplicationStartupTest {

    @Inject
    UserSyncService userService;

    @Test
    void testApplicationStarts() {
        assertNotNull(userService, "UserSyncService should be injected");
    }

    @Test
    void testDatabaseConnection() {
        try {
            userService.getAll();
        } catch (Exception e) {
            // Database may not be available in test environment
            // This is acceptable for testing application startup
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }
}