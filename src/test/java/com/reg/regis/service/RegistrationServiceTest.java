package com.reg.regis.service;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private DukcapilClientService dukcapilClientService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private RegistrationService registrationService;

    private RegistrationRequest registrationRequest;
    private Customer customer;

    @BeforeEach
    void setUp() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setNik("1234567890123456");
        registrationRequest.setNamaLengkap("John Doe");
        registrationRequest.setEmail("john@example.com");
        registrationRequest.setPassword("password123");
        registrationRequest.setNomorTelepon("081234567890");
        registrationRequest.setTanggalLahir(LocalDate.of(1990, 1, 1));
        registrationRequest.setJenisKartu("Silver");

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
        customer.setKodeRekening(12345678);
        customer.setNomorKartuDebitVirtual("4101 2345 6789 0123");
    }

    @Test
    void testRegisterCustomer_Success() {
        // Mock Dukcapil service
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        Map<String, Object> ktpData = new HashMap<>();
        ktpData.put("namaLengkap", "John Doe");
        ktpData.put("tempatLahir", "Jakarta");
        ktpData.put("tanggalLahir", "1990-01-01");
        ktpData.put("jenisKelamin", "L");
        ktpData.put("agama", "Islam");
        dukcapilResponse.setData(ktpData);

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        RegistrationResponse response = registrationService.registerCustomer(registrationRequest);

        assertNotNull(response);
        assertEquals("Silver", response.getJenisKartu());
        assertEquals("John Doe", response.getNamaLengkap());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testRegisterCustomer_DukcapilServiceUnavailable() {
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registrationService.registerCustomer(registrationRequest));

        assertEquals("Dukcapil service tidak tersedia. Silakan coba lagi nanti.", exception.getMessage());
    }

    @Test
    void testRegisterCustomer_EmailExists() {
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registrationService.registerCustomer(registrationRequest));

        assertTrue(exception.getMessage().contains("Email") && exception.getMessage().contains("sudah terdaftar"));
    }

    @Test
    void testAuthenticateCustomer_Success() {
        when(customerRepository.findByEmailIgnoreCase("john@example.com"))
            .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("password123", customer.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");

        String token = registrationService.authenticateCustomer("john@example.com", "password123");

        assertEquals("jwt-token", token);
        verify(customerRepository).save(customer); // Reset failed attempts
    }

    @Test
    void testAuthenticateCustomer_WrongPassword() {
        when(customerRepository.findByEmailIgnoreCase("john@example.com"))
            .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpassword", customer.getPassword())).thenReturn(false);
        when(loginAttemptService.recordFailedLoginAttempt("john@example.com")).thenReturn(customer);
        when(loginAttemptService.getMaxLoginAttempts()).thenReturn(5);

        assertThrows(BadCredentialsException.class, 
            () -> registrationService.authenticateCustomer("john@example.com", "wrongpassword"));

        verify(loginAttemptService).recordFailedLoginAttempt("john@example.com");
    }

    @Test
    void testAuthenticateCustomer_UserNotFound() {
        when(customerRepository.findByEmailIgnoreCase("notfound@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, 
            () -> registrationService.authenticateCustomer("notfound@example.com", "password"));
    }

    @Test
    void testValidateNikFormat_Valid() {
        assertTrue(registrationService.validateNikFormat("3201234567890123"));
    }

    @Test
    void testValidateNikFormat_Invalid() {
        assertFalse(registrationService.validateNikFormat("123"));
        assertFalse(registrationService.validateNikFormat("abcd567890123456"));
        assertFalse(registrationService.validateNikFormat(null));
    }

    @Test
    void testCheckPasswordStrength() {
        assertEquals("lemah", registrationService.checkPasswordStrength("123"));
        assertEquals("sedang", registrationService.checkPasswordStrength("Password123"));
        assertEquals("kuat", registrationService.checkPasswordStrength("Password123@"));
    }

    @Test
    void testGetCustomerByEmail() {
        when(customerRepository.findByEmailIgnoreCase("john@example.com"))
            .thenReturn(Optional.of(customer));

        Optional<Customer> result = registrationService.getCustomerByEmail("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    void testValidateToken() {
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

        assertTrue(registrationService.validateToken("valid-token"));
        assertFalse(registrationService.validateToken("invalid-token"));
    }
}
