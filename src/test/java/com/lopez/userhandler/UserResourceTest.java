package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class UserResourceTest {
    @Test
    void testJaxrs() {
        RestAssured.when().get("/api/v1/users").then()
                .contentType("text/plain")
                .body(equalTo("Users list"));
    }

}
