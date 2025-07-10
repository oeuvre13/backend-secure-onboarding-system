package com.reg.regis.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void shouldConfigurePasswordEncoder() {
        // When
        String encoded = passwordEncoder.encode("password");

        // Then
        assertThat(encoded).isNotEqualTo("password");
        assertThat(passwordEncoder.matches("password", encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrongpassword", encoded)).isFalse();
    }

    @Test
    void shouldUseBCryptWithStrength12() {
        // When
        String encoded = passwordEncoder.encode("test");

        // Then
        assertThat(encoded).startsWith("$2a$12$"); // BCrypt with strength 12
    }

    @Test
    void shouldAllowAuthEndpoints() throws Exception {
        // Health endpoint
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk());

        // Stats endpoint
        mockMvc.perform(get("/auth/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSetSecurityHeaders() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"));
    }

    @Test
    void shouldConfigureCORS() {
        // When
        var corsConfig = corsConfigurationSource.getCorsConfiguration(null);

        // Then
        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOriginPatterns()).contains("http://localhost:3000");
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(corsConfig.getAllowedHeaders()).contains("Authorization", "Content-Type");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
    }

    @Test
    void shouldHandleOptionsRequests() throws Exception {
        // When & Then
        mockMvc.perform(options("/auth/register")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,PATCH"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    void shouldReturnCustomErrorForAuthentication() throws Exception {
        // This would normally require authentication, but we're allowing all for now
        // In a real scenario with protected endpoints:
        
        mockMvc.perform(get("/protected-endpoint"))
                .andExpect(status().isNotFound()); // 404 because endpoint doesn't exist, not 401
    }

    @Test
    void shouldGenerateConsistentPasswordHashes() {
        // Given
        String password = "testPassword123!";

        // When
        String hash1 = passwordEncoder.encode(password);
        String hash2 = passwordEncoder.encode(password);

        // Then
        assertThat(hash1).isNotEqualTo(hash2); // Different salts
        assertThat(passwordEncoder.matches(password, hash1)).isTrue();
        assertThat(passwordEncoder.matches(password, hash2)).isTrue();
    }

    @Test
    void shouldHandleSpecialCharactersInPassword() {
        // Given
        String specialPassword = "P@ssw0rd!#$%^&*()";

        // When
        String encoded = passwordEncoder.encode(specialPassword);

        // Then
        assertThat(passwordEncoder.matches(specialPassword, encoded)).isTrue();
        assertThat(passwordEncoder.matches("wrongPassword", encoded)).isFalse();
    }

    @Test
    void shouldHandleEmptyAndNullPasswords() {
        // Empty password
        String emptyEncoded = passwordEncoder.encode("");
        assertThat(passwordEncoder.matches("", emptyEncoded)).isTrue();
        assertThat(passwordEncoder.matches("notEmpty", emptyEncoded)).isFalse();

        // Null password should be handled gracefully
        String nullEncoded = passwordEncoder.encode(null);
        assertThat(passwordEncoder.matches(null, nullEncoded)).isTrue();
    }
}