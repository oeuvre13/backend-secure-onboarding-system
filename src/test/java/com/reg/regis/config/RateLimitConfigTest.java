package com.reg.regis.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitConfigTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() throws Exception {
        rateLimitConfig = new RateLimitConfig();
        
        // Set up rate limiting properties
        ReflectionTestUtils.setField(rateLimitConfig, "rateLimitEnabled", true);
        ReflectionTestUtils.setField(rateLimitConfig, "capacity", 2L); // Low capacity for testing
        ReflectionTestUtils.setField(rateLimitConfig, "refillRate", 1L);
        
        // Mock response writer
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void shouldAllowFirstRequest() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldAllowRequestsWithinLimit() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean firstRequest = rateLimitConfig.preHandle(request, response, handler);
        boolean secondRequest = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(firstRequest).isTrue();
        assertThat(secondRequest).isTrue();
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldBlockRequestsExceedingLimit() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When - Consume all tokens
        rateLimitConfig.preHandle(request, response, handler);
        rateLimitConfig.preHandle(request, response, handler);
        boolean thirdRequest = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(thirdRequest).isFalse();
        verify(response).setStatus(429);
        verify(response).setContentType("application/json");
    }

    @Test
    void shouldAllowDifferentIPAddresses() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        
        // Consume all tokens for first IP
        rateLimitConfig.preHandle(request, response, handler);
        rateLimitConfig.preHandle(request, response, handler);
        rateLimitConfig.preHandle(request, response, handler); // This should fail

        // Change to different IP
        when(request.getRemoteAddr()).thenReturn("192.168.1.2");

        // When
        boolean newIPRequest = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(newIPRequest).isTrue();
    }

    @Test
    void shouldUseXForwardedForHeader() throws Exception {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 192.168.1.1");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
        // Should use the first IP from X-Forwarded-For (203.0.113.1)
    }

    @Test
    void shouldUseXRealIPHeader() throws Exception {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("203.0.113.1");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldFallbackToRemoteAddr() throws Exception {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldBypassWhenRateLimitDisabled() throws Exception {
        // Given
        ReflectionTestUtils.setField(rateLimitConfig, "rateLimitEnabled", false);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When - Make many requests
        for (int i = 0; i < 10; i++) {
            boolean result = rateLimitConfig.preHandle(request, response, handler);
            assertThat(result).isTrue();
        }

        // Then
        verify(response, never()).setStatus(429);
    }

    @Test
    void shouldHandleEmptyXForwardedFor() throws Exception {
        // Given
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldHandleWriterException() throws Exception {
        // Given
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(response.getWriter()).thenThrow(new RuntimeException("Writer error"));

        // Consume all tokens
        rateLimitConfig.preHandle(request, response, handler);
        rateLimitConfig.preHandle(request, response, handler);

        // When
        boolean result = rateLimitConfig.preHandle(request, response, handler);

        // Then
        assertThat(result).isFalse();
        verify(response).setStatus(429);
        // Should not throw exception despite writer error
    }
}