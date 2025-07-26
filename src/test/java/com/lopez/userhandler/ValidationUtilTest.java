package com.lopez.userhandler;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.junit.jupiter.api.Test;

import com.lopez.userhandler.util.ValidationUtil;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(DevTestProfile.class)
class ValidationUtilTest {

    @Test
    void testValidEmail() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.uk"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));
        assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    void testValidIdNumber() {
        assertTrue(ValidationUtil.isValidIdNumber("12345678"));
        assertTrue(ValidationUtil.isValidIdNumber("123456789012"));
        assertFalse(ValidationUtil.isValidIdNumber("1234567"));
        assertFalse(ValidationUtil.isValidIdNumber("1234567890123"));
        assertFalse(ValidationUtil.isValidIdNumber("abc12345"));
        assertFalse(ValidationUtil.isValidIdNumber(null));
    }

    @Test
    void testValidUUID() {
        assertTrue(ValidationUtil.isValidUUID("550e8400-e29b-41d4-a716-446655440000"));
        assertFalse(ValidationUtil.isValidUUID("invalid-uuid"));
        assertFalse(ValidationUtil.isValidUUID("550e8400-e29b-41d4-a716"));
        assertFalse(ValidationUtil.isValidUUID(null));
    }

    @Test
    void testValidString() {
        assertTrue(ValidationUtil.isValidString("test", 10));
        assertTrue(ValidationUtil.isValidString("exactly10c", 10));
        assertFalse(ValidationUtil.isValidString("toolongstring", 10));
        assertFalse(ValidationUtil.isValidString("", 10));
        assertFalse(ValidationUtil.isValidString("   ", 10));
        assertFalse(ValidationUtil.isValidString(null, 10));
    }

    @Test
    void testValidRiskLevel() {
        assertTrue(ValidationUtil.isValidRiskLevel("1"));
        assertTrue(ValidationUtil.isValidRiskLevel("5"));
        assertTrue(ValidationUtil.isValidRiskLevel("10"));
        assertFalse(ValidationUtil.isValidRiskLevel("0"));
        assertFalse(ValidationUtil.isValidRiskLevel("11"));
        assertFalse(ValidationUtil.isValidRiskLevel("abc"));
        assertFalse(ValidationUtil.isValidRiskLevel(null));
    }
}