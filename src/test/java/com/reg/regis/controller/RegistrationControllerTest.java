package com.reg.regis.controller;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private RegistrationRequest registrationRequest;
    private Customer customer;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        response = new MockHttpServletResponse();
        
        registrationRequest = new RegistrationRequest();
        registrationRequest.setNik("1234567890123456");
        registrationRequest.setNamaLengkap("John Doe");
        registrationRequest.setEmail("john@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setNomorTelepon("081234567890");
        registrationRequest.setTanggalLahir(LocalDate.of(1990, 1, 1));

        RegistrationRequest.AlamatRequest alamat = new RegistrationRequest.AlamatRequest();
        alamat.setNamaAlamat("Jl. Test");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta");
        alamat.setKecamatan("Test");
        alamat.setKelurahan("Test");
        alamat.setKodePos("12345");
        registrationRequest.setAlamat(alamat);

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john@example.com");
        customer.setNamaLengkap("John Doe");
        customer.setJenisKartu("Silver");
    }

    @Test
    void testRegisterCustomer_Success() {
        RegistrationResponse registrationResponse = new RegistrationResponse(
            "Silver", "John Doe", "12345678", "PERSONAL", "4101 2345 6789 0123"
        );

        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenReturn(registrationResponse);
        when(registrationService.authenticateCustomer(anyString(), anyString()))
            .thenReturn("jwt-token");

        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("success"));
        
        verify(registrationService).registerCustomer(any(RegistrationRequest.class));
        verify(registrationService).authenticateCustomer("john@example.com", "password123");
    }

    @Test
    void testRegisterCustomer_ValidationError() {
        when(registrationService.registerCustomer(any(RegistrationRequest.class)))
            .thenThrow(new RuntimeException("Email sudah terdaftar"));

        ResponseEntity<?> result = registrationController.registerCustomer(registrationRequest, response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("Email sudah terdaftar", body.get("error"));
    }

    @Test
    void testCheckPasswordStrength() {
        when(registrationService.checkPasswordStrength("password123"))
            .thenReturn("sedang");

        Map<String, String> request = Map.of("password", "password123");
        ResponseEntity<?> result = registrationController.checkPasswordStrength(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("sedang", body.get("strength"));
    }

    @Test
    void testValidateNik_Valid() {
        when(registrationService.validateNikFormat("1234567890123456")).thenReturn(true);
        when(registrationService.getCustomerByNik("1234567890123456")).thenReturn(Optional.empty());

        Map<String, String> request = Map.of("nik", "1234567890123456");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("valid"));
        assertFalse((Boolean) body.get("exists"));
    }

    @Test
    void testValidateNik_Invalid() {
        when(registrationService.validateNikFormat("123")).thenReturn(false);

        Map<String, String> request = Map.of("nik", "123");
        ResponseEntity<?> result = registrationController.validateNik(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("valid"));
        assertEquals("Format NIK tidak valid", body.get("message"));
    }

    @Test
    void testVerifyEmail() {
        doNothing().when(registrationService).verifyEmail("test@example.com");

        Map<String, String> request = Map.of("email", "test@example.com");
        ResponseEntity<?> result = registrationController.verifyEmail(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Email berhasil diverifikasi", body.get("message"));
    }

    @Test
    void testGetRegistrationStats() {
        RegistrationService.RegistrationStats stats = new RegistrationService.RegistrationStats(
            100L, 80L, 80.0, true, "http://localhost:8081"
        );
        when(registrationService.getRegistrationStats()).thenReturn(stats);

        ResponseEntity<?> result = registrationController.getRegistrationStats();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(stats, result.getBody());
    }

    @Test
    void testHealthCheck() {
        RegistrationService.RegistrationStats stats = new RegistrationService.RegistrationStats(
            100L, 80L, 80.0, true, "http://localhost:8081"
        );
        when(registrationService.getRegistrationStats()).thenReturn(stats);

        ResponseEntity<?> result = registrationController.healthCheck();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("OK", body.get("status"));
    }

    @Test
    void testGetCustomerProfile_ValidToken() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = registrationController.getCustomerProfile("valid-token");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertNotNull(body.get("profile"));
    }

    @Test
    void testGetCustomerProfile_InvalidToken() {
        when(registrationService.getEmailFromToken("invalid-token")).thenReturn(null);

        ResponseEntity<?> result = registrationController.getCustomerProfile("invalid-token");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Token tidak valid", body.get("error"));
    }

    @Test
    void testGetCustomerProfile_NoToken() {
        ResponseEntity<?> result = registrationController.getCustomerProfile(null);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Token tidak ditemukan", body.get("error"));
    }
}
