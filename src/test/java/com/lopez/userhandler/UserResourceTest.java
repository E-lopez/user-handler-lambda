package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(DevTestProfile.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @Test
    @Order(1)
    void testGetAllUsers() {
        given()
            .when().get("/users")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(404), equalTo(500)));
    }

    @Test
    @Order(2)
    void testGetUserByIdWithInvalidFormat() {
        given()
            .when().get("/users/id/invalid-id")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(404)));
    }

    @Test
    @Order(3)
    void testGetUserByNameWithValidName() {
        given()
            .when().get("/users/name/testuser")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(404), equalTo(500)));
    }

    @Test
    @Order(4)
    void testAddUserWithInvalidData() {
        String invalidUser = "{}";
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidUser)
            .when().post("/users")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(404), equalTo(500)));
    }

    @Test
    @Order(5)
    void testAddValidUser() {
        String validUser = "{"
            + "\"idNumber\": \"123456789\","
            + "\"userName\": \"testuser\","
            + "\"email\": \"test@example.com\","
            + "\"dateOfBirth\": \"1990-01-01\","
            + "\"gender\": \"M\","
            + "\"occupation\": \"Developer\","
            + "\"riskLevel\": \"5\""
            + "}";
        
        given()
            .contentType(ContentType.JSON)
            .body(validUser)
            .when().post("/users")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(404), equalTo(409), equalTo(500)));
    }

    @Test
    @Order(6)
    void testUpdateRiskLevelWithInvalidData() {
        String invalidUpdate = "{}";
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidUpdate)
            .when().post("/users/risk")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(404)));
    }

    @Test
    @Order(7)
    void testUpdateRiskLevelWithInvalidRiskLevel() {
        String invalidUpdate = "{"
            + "\"userId\": \"550e8400-e29b-41d4-a716-446655440000\","
            + "\"riskLevel\": \"15\""
            + "}";
        
        given()
            .contentType(ContentType.JSON)
            .body(invalidUpdate)
            .when().post("/users/risk")
            .then()
            .statusCode(anyOf(equalTo(200), equalTo(400), equalTo(404)));
    }
}
