package com.reg.regis.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilTest {

    @Test
    public void testValidations() {
        assertTrue(SecurityUtil.isValidEmail("test@example.com"));
        assertFalse(SecurityUtil.isValidEmail("invalid-email"));

        assertTrue(SecurityUtil.isValidPhoneNumber("081234567890"));
        assertFalse(SecurityUtil.isValidPhoneNumber("not-a-number"));

        assertTrue(SecurityUtil.isValidNik("1234567890123456"));
        assertFalse(SecurityUtil.isValidNik("short"));

        assertTrue(SecurityUtil.isStrongPassword("Password@123"));
        assertFalse(SecurityUtil.isStrongPassword("weak"));
    }

    @Test
    public void testSanitizeInput() {
        String raw = "<script>alert('x')</script>";
        String sanitized = SecurityUtil.sanitizeInput(raw);
        assertFalse(sanitized.contains("<"));
        assertFalse(sanitized.contains(">"));
    }
}
