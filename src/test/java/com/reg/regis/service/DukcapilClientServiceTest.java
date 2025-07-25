package com.reg.regis.service;

import com.reg.regis.dto.response.DukcapilResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class DukcapilClientServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DukcapilClientService dukcapilClientService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dukcapilClientService, "dukcapilBaseUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(dukcapilClientService, "verifyNikEndpoint", "/verify");
        ReflectionTestUtils.setField(dukcapilClientService, "checkNikEndpoint", "/check");
    }

    @Test
    void testVerifyNikNameAndBirthDate_Success() {
        DukcapilResponseDto mockResponse = new DukcapilResponseDto(true, "Valid");
        ResponseEntity<DukcapilResponseDto> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(DukcapilResponseDto.class)
        )).thenReturn(responseEntity);

        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(
            "1234567890123456", 
            "John Doe", 
            LocalDate.of(1990, 1, 1)
        );

        assertTrue(result.isValid());
        assertEquals("Valid", result.getMessage());
    }

    @Test
    void testVerifyNikNameAndBirthDate_ServiceUnavailable() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(DukcapilResponseDto.class)
        )).thenThrow(new ResourceAccessException("Connection refused"));

        DukcapilResponseDto result = dukcapilClientService.verifyNikNameAndBirthDate(
            "1234567890123456", 
            "John Doe", 
            LocalDate.of(1990, 1, 1)
        );

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("tidak dapat diakses"));
    }

    @Test
    void testIsNikExists_True() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("exists", true);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        boolean result = dukcapilClientService.isNikExists("1234567890123456");

        assertTrue(result);
    }

    @Test
    void testIsNikExists_False() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("exists", false);
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        boolean result = dukcapilClientService.isNikExists("1234567890123456");

        assertFalse(result);
    }

    @Test
    void testIsNikExists_Exception() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Service error"));

        boolean result = dukcapilClientService.isNikExists("1234567890123456");

        assertFalse(result);
    }

    @Test
    void testIsDukcapilServiceHealthy_Healthy() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("status", "OK");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        assertTrue(result);
    }

    @Test
    void testIsDukcapilServiceHealthy_Unhealthy() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("Health check failed"));

        boolean result = dukcapilClientService.isDukcapilServiceHealthy();

        assertFalse(result);
    }

    @Test
    void testGetDukcapilBaseUrl() {
        String baseUrl = dukcapilClientService.getDukcapilBaseUrl();
        assertEquals("http://localhost:8081", baseUrl);
    }
}
