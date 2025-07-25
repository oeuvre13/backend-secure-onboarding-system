package com.reg.regis.service;

import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DukcapilClientService dukcapilClientService;

    @InjectMocks
    private VerificationService verificationService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setNamaLengkap("Test User");
        customer.setNomorTelepon("081234567890");
        customer.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testVerifyNik_Success() {
        NikVerificationRequest request = new NikVerificationRequest();
        request.setNik("1234567890123456");
        request.setNamaLengkap("Test User");
        request.setTanggalLahir(LocalDate.of(1990, 1, 1));

        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid NIK");
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(dukcapilResponse);

        VerificationResponse response = verificationService.verifyNik(request);

        assertTrue(response.isValid());
        assertEquals("Valid NIK", response.getMessage());
    }

    @Test
    void testVerifyNik_Failed() {
        NikVerificationRequest request = new NikVerificationRequest();
        request.setNik("1234567890123456");
        request.setNamaLengkap("Test User");
        request.setTanggalLahir(LocalDate.of(1990, 1, 1));

        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(false, "Invalid NIK");
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(dukcapilResponse);

        VerificationResponse response = verificationService.verifyNik(request);

        assertFalse(response.isValid());
        assertEquals("Invalid NIK", response.getMessage());
    }

    @Test
    void testVerifyEmail_EmailExists() {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("test@example.com");

        when(customerRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);
        when(customerRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(customer));

        VerificationResponse response = verificationService.verifyEmail(request);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Email sudah terdaftar"));
        assertNotNull(response.getData());
    }

    @Test
    void testVerifyEmail_EmailAvailable() {
        EmailVerificationRequest request = new EmailVerificationRequest();
        request.setEmail("new@example.com");

        when(customerRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);

        VerificationResponse response = verificationService.verifyEmail(request);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().contains("Email belum terdaftar"));
    }

    @Test
    void testVerifyPhone_PhoneExists() {
        PhoneVerificationRequest request = new PhoneVerificationRequest();
        request.setNomorTelepon("081234567890");

        when(customerRepository.existsByNomorTelepon("081234567890")).thenReturn(true);
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        VerificationResponse response = verificationService.verifyPhone(request);

        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Nomor telepon sudah terdaftar"));
    }

    @Test
    void testVerifyPhone_PhoneAvailable() {
        PhoneVerificationRequest request = new PhoneVerificationRequest();
        request.setNomorTelepon("089876543210");

        when(customerRepository.existsByNomorTelepon("089876543210")).thenReturn(false);

        VerificationResponse response = verificationService.verifyPhone(request);

        assertTrue(response.isValid());
        assertTrue(response.getMessage().contains("Nomor telepon belum terdaftar"));
    }

    @Test
    void testIsNikRegistered() {
        when(dukcapilClientService.isNikExists("1234567890123456")).thenReturn(true);
        when(dukcapilClientService.isNikExists("9876543210987654")).thenReturn(false);

        assertTrue(verificationService.isNikRegistered("1234567890123456"));
        assertFalse(verificationService.isNikRegistered("9876543210987654"));
    }

    @Test
    void testGetVerificationStats() {
        when(customerRepository.countTotalCustomers()).thenReturn(100L);
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.getDukcapilBaseUrl()).thenReturn("http://localhost:8081");

        Map<String, Object> stats = verificationService.getVerificationStats();

        assertEquals(100L, stats.get("totalCustomers"));
        assertEquals(0L, stats.get("verifiedCustomers"));
        assertEquals(true, stats.get("dukcapilServiceHealthy"));
        assertEquals("http://localhost:8081", stats.get("dukcapilServiceUrl"));
        assertNotNull(stats.get("timestamp"));
    }
}
