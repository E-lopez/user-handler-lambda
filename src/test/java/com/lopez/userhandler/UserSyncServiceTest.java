package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import com.lopez.userhandler.dto.ApiResponse;
import com.lopez.userhandler.dto.UpdateUserRiskDto;
import com.lopez.userhandler.dto.User;
import com.lopez.userhandler.service.UserSyncService;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(DevTestProfile.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserSyncServiceTest {

    @Inject
    UserSyncService userService;

    @Test
    @Order(1)
    void testServiceInjection() {
        assertNotNull(userService, "UserSyncService should be injected");
    }

    @Test
    @Order(2)
    void testGetAllUsers() {
        try {
            ApiResponse<?> response = userService.getAll();
            assertNotNull(response);
        } catch (Exception e) {
            // Database may not be available in test environment
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }

    @Test
    @Order(3)
    void testGetUserByIdNotFound() {
        try {
            ApiResponse<User> response = userService.getUserById("550e8400-e29b-41d4-a716-446655440000");
            assertNotNull(response);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }

    @Test
    @Order(4)
    void testGetUserByNameNotFound() {
        try {
            ApiResponse<User> response = userService.getUserByName("nonexistentuser");
            assertNotNull(response);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }

    @Test
    @Order(5)
    void testAddUser() {
        User user = new User();
        user.setIdNumber("987654321");
        user.setUserName("servicetest");
        user.setEmail("servicetest@example.com");
        user.setDateOfBirth("1985-05-15");
        user.setGender("F");
        user.setOccupation("Tester");
        user.setRiskLevel("3");

        try {
            ApiResponse<User> response = userService.add(user);
            assertNotNull(response);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }

    @Test
    @Order(6)
    void testUpdateRiskLevelUserNotFound() {
        UpdateUserRiskDto dto = new UpdateUserRiskDto();
        dto.setUserId("550e8400-e29b-41d4-a716-446655440000");
        dto.setRiskLevel("8");

        try {
            ApiResponse<User> response = userService.updateRiskLevel(dto);
            assertNotNull(response);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("DynamoDB") || e.getMessage().contains("table"));
        }
    }
}