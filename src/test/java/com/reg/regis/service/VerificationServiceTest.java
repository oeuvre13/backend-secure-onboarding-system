package com.reg.regis.service;

import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.VerificationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.test.factory.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public
class VerificationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private DukcapilClientService dukcapilClientService;
    
    @InjectMocks
    private VerificationService verificationService;

    @Test
    void testVerifyNik_JohnDoe_Success() {
        // Given
        NikVerificationRequest request = TestDataFactory.createJohnDoeNikRequest();
        
        Map<String, Object> ktpData = new HashMap<>();
        ktpData.put("namaLengkap", "John Doe");
        ktpData.put("nik", "3175031234567890");
        
        DukcapilResponseDto mockResponse = new DukcapilResponseDto();
        mockResponse.setValid(true);
        mockResponse.setMessage("Data valid");
        mockResponse.setData(ktpData);
        
        when(dukcapilClientService.verifyNikNameAndBirthDate(
            "3175031234567890", "John Doe", request.getTanggalLahir()))
            .thenReturn(mockResponse);

        // When
        VerificationResponse response = verificationService.verifyNik(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("Data valid", response.getMessage());
        assertNotNull(response.getData());
        
        verify(dukcapilClientService).verifyNikNameAndBirthDate(
            "3175031234567890", "John Doe", request.getTanggalLahir());
    }
    
    @Test
    void testVerifyNik_JaneSmith_Success() {
        // Given
        NikVerificationRequest request = TestDataFactory.createJaneSmithNikRequest();
        
        Map<String, Object> ktpData = new HashMap<>();
        ktpData.put("namaLengkap", "Jane Smith");
        ktpData.put("nik", "3175032345678901");
        
        DukcapilResponseDto mockResponse = new DukcapilResponseDto();
        mockResponse.setValid(true);
        mockResponse.setMessage("Data valid");
        mockResponse.setData(ktpData);
        
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(mockResponse);

        // When
        VerificationResponse response = verificationService.verifyNik(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("Data valid", response.getMessage());
    }
    
    @Test
    void testVerifyNik_InvalidData_Failed() {
        // Given
        NikVerificationRequest request = TestDataFactory.createInvalidNikRequest();
        
        DukcapilResponseDto mockResponse = new DukcapilResponseDto();
        mockResponse.setValid(false);
        mockResponse.setMessage("Nama tidak sesuai dengan NIK");
        mockResponse.setData(null);
        
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(mockResponse);

        // When
        VerificationResponse response = verificationService.verifyNik(request);

        // Then
        assertFalse(response.isValid());
        assertEquals("Nama tidak sesuai dengan NIK", response.getMessage());
    }
    
    @Test
    void testVerifyNik_DukcapilException() {
        // Given
        NikVerificationRequest request = TestDataFactory.createJohnDoeNikRequest();
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenThrow(new RuntimeException("Dukcapil service error"));

        // When
        VerificationResponse response = verificationService.verifyNik(request);

        // Then
        assertFalse(response.isValid());
        assertTrue(response.getMessage().contains("Terjadi kesalahan saat verifikasi NIK"));
    }
    
    @Test
    void testVerifyEmail_NewEmail_Available() {
        // Given
        EmailVerificationRequest request = TestDataFactory.createNewEmailRequest();
        when(customerRepository.existsByEmailIgnoreCase("newuser@example.com")).thenReturn(false);

        // When
        VerificationResponse response = verificationService.verifyEmail(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("Email belum terdaftar dan dapat digunakan", response.getMessage());
    }
    
    @Test
    void testVerifyEmail_ExistingEmail_NotAvailable() {
        // Given
        EmailVerificationRequest request = TestDataFactory.createExistingEmailRequest();
        Customer existingCustomer = TestDataFactory.createJohnDoe();
        existingCustomer.setEmail("existing@example.com");
        
        when(customerRepository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);
        when(customerRepository.findByEmailIgnoreCase("existing@example.com"))
            .thenReturn(Optional.of(existingCustomer));

        // When
        VerificationResponse response = verificationService.verifyEmail(request);

        // Then
        assertFalse(response.isValid());
        assertEquals("Email sudah terdaftar di sistem", response.getMessage());
        assertNotNull(response.getData());
    }
    
    @Test
    void testVerifyPhone_NewPhone_Available() {
        // Given
        PhoneVerificationRequest request = TestDataFactory.createNewPhoneRequest();
        when(customerRepository.existsByNomorTelepon("081999888777")).thenReturn(false);

        // When
        VerificationResponse response = verificationService.verifyPhone(request);

        // Then
        assertTrue(response.isValid());
        assertEquals("Nomor telepon belum terdaftar dan dapat digunakan", response.getMessage());
    }
    
    @Test
    void testVerifyPhone_ExistingPhone_NotAvailable() {
        // Given
        PhoneVerificationRequest request = TestDataFactory.createExistingPhoneRequest();
        Customer existingCustomer = TestDataFactory.createJohnDoe();
        existingCustomer.setNomorTelepon("081234567890");
        
        when(customerRepository.existsByNomorTelepon("081234567890")).thenReturn(true);
        when(customerRepository.findAll()).thenReturn(List.of(existingCustomer));

        // When
        VerificationResponse response = verificationService.verifyPhone(request);

        // Then
        assertFalse(response.isValid());
        assertEquals("Nomor telepon sudah terdaftar di sistem", response.getMessage());
        assertNotNull(response.getData());
    }
    
    @Test
    void testIsNikRegistered() {
        // Given
        when(dukcapilClientService.isNikExists("3175031234567890")).thenReturn(true);

        // When
        boolean result = verificationService.isNikRegistered("3175031234567890");

        // Then
        assertTrue(result);
        verify(dukcapilClientService).isNikExists("3175031234567890");
    }
    
    @Test
    void testGetVerificationStats() {
        // Given
        when(customerRepository.countTotalCustomers()).thenReturn(100L);
        // when(customerRepository.countVerifiedCustomers()).thenReturn(80L);
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.getDukcapilBaseUrl()).thenReturn("http://localhost:8081/api/dukcapil");

        // When
        Map<String, Object> stats = verificationService.getVerificationStats();

        // Then
        assertEquals(100L, stats.get("totalCustomers"));
        assertEquals(80L, stats.get("verifiedCustomers"));
        assertEquals(80.0, stats.get("verificationRate"));
        assertTrue((Boolean) stats.get("dukcapilServiceHealthy"));
        assertEquals("http://localhost:8081/api/dukcapil", stats.get("dukcapilServiceUrl"));
        assertNotNull(stats.get("timestamp"));
    }
}