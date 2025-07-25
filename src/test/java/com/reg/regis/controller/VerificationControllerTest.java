package com.reg.regis.controller;

import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class VerificationControllerTest {

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private VerificationController verificationController;

    private NikVerificationRequest nikRequest;
    private EmailVerificationRequest emailRequest;
    private PhoneVerificationRequest phoneRequest;

    @BeforeEach
    void setUp() {
        nikRequest = new NikVerificationRequest();
        nikRequest.setNik("1234567890123456");
        nikRequest.setNamaLengkap("John Doe");
        nikRequest.setTanggalLahir(LocalDate.of(1990, 1, 1));

        emailRequest = new EmailVerificationRequest();
        emailRequest.setEmail("test@example.com");

        phoneRequest = new PhoneVerificationRequest();
        phoneRequest.setNomorTelepon("081234567890");
    }

    @Test
    void testVerifyNik_Success() {
        VerificationResponse response = new VerificationResponse(true, "NIK valid");
        when(verificationService.verifyNik(any(NikVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertEquals("NIK valid", body.get("message"));
    }

    @Test
    void testVerifyNik_Failed() {
        VerificationResponse response = new VerificationResponse(false, "NIK tidak valid");
        when(verificationService.verifyNik(any(NikVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyNik(nikRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
        assertEquals("NIK tidak valid", body.get("message"));
    }

    @Test
    void testVerifyEmail_Available() {
        VerificationResponse response = new VerificationResponse(true, "Email tersedia");
        when(verificationService.verifyEmail(any(EmailVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyEmail(emailRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Email tersedia", body.get("message"));
    }

    @Test
    void testVerifyPhone_Available() {
        VerificationResponse response = new VerificationResponse(true, "Nomor telepon tersedia");
        when(verificationService.verifyPhone(any(PhoneVerificationRequest.class))).thenReturn(response);

        ResponseEntity<?> result = verificationController.verifyPhone(phoneRequest);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("available"));
        assertEquals("Nomor telepon tersedia", body.get("message"));
    }

    @Test
    void testCheckNik_Valid() {
        when(verificationService.isNikRegistered("1234567890123456")).thenReturn(true);

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("registered"));
    }

    @Test
    void testCheckNik_Invalid() {
        Map<String, String> request = Map.of("nik", "123");
        ResponseEntity<?> result = verificationController.checkNik(request);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("registered"));
        assertEquals("NIK harus 16 digit", body.get("message"));
    }

    @Test
    void testGetVerificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCustomers", 100L);
        stats.put("verifiedCustomers", 80L);
        stats.put("dukcapilServiceHealthy", true);

        when(verificationService.getVerificationStats()).thenReturn(stats);

        ResponseEntity<?> result = verificationController.getVerificationStats();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<?> result = verificationController.healthCheck();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("OK", body.get("status"));
        assertNotNull(body.get("service"));
        assertNotNull(body.get("endpoints"));
    }
}
