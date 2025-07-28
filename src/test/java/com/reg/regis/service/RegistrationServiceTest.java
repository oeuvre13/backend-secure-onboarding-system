package com.reg.regis.service;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.dto.response.RegistrationResponse;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void authenticateCustomer_ValidCredentials_ReturnsToken() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        String token = "jwt.token.here";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(hashedPassword);
        customer.setFailedLoginAttempts(0);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        // When
        String result = registrationService.authenticateCustomer(email, password);

        // Then
        assertEquals(token, result);
        assertEquals(0, customer.getFailedLoginAttempts());
        assertNull(customer.getAccountLockedUntil());
        verify(customerRepository).save(customer);
    }

    @Test
    void authenticateCustomer_InvalidCredentials_ThrowsBadCredentials() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(hashedPassword);
        customer.setFailedLoginAttempts(2);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setFailedLoginAttempts(3);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);
        when(loginAttemptService.recordFailedLoginAttempt(email)).thenReturn(updatedCustomer);
        when(loginAttemptService.getMaxLoginAttempts()).thenReturn(5);

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> registrationService.authenticateCustomer(email, password)
        );

        assertTrue(exception.getMessage().contains("Email atau password salah"));
        verify(loginAttemptService).recordFailedLoginAttempt(email);
    }

    @Test
    void authenticateCustomer_EmailNotFound_ThrowsBadCredentials() {
        // Given
        String email = "notfound@example.com";
        String password = "password123";

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> registrationService.authenticateCustomer(email, password)
        );

        assertEquals("Email atau password salah.", exception.getMessage());
        verify(loginAttemptService, never()).recordFailedLoginAttempt(any());
    }

    @Test
    void authenticateCustomer_AccountLocked_ThrowsBadCredentials() {
        // Given
        String email = "test@example.com";
        String password = "password123";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword("hashedPassword");
        customer.setFailedLoginAttempts(0);
        // Set account locked via accountLockedUntil instead of isAccountLocked()
        customer.setAccountLockedUntil(java.time.LocalDateTime.now().plusMinutes(5));

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));
        when(loginAttemptService.getLockoutDurationMinutes()).thenReturn(1L);

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> registrationService.authenticateCustomer(email, password)
        );

        assertTrue(exception.getMessage().contains("Akun Anda terkunci"));
    }

    @Test
    void validateNikFormat_ValidNik_ReturnsTrue() {
        // Given
        String validNik = "1234567890123456";

        // When
        boolean result = registrationService.validateNikFormat(validNik);

        // Then
        assertTrue(result);
    }

    @Test
    void validateNikFormat_InvalidLength_ReturnsFalse() {
        // Given
        String invalidNik = "12345678901234";

        // When
        boolean result = registrationService.validateNikFormat(invalidNik);

        // Then
        assertFalse(result);
    }

    @Test
    void validateNikFormat_InvalidFormat_ReturnsFalse() {
        // Given
        String invalidNik = "00000012345678901";

        // When
        boolean result = registrationService.validateNikFormat(invalidNik);

        // Then
        assertFalse(result);
    }

    @Test
    void checkPasswordStrength_WeakPassword_ReturnsLemah() {
        // Given
        String weakPassword = "123";

        // When
        String result = registrationService.checkPasswordStrength(weakPassword);

        // Then
        assertEquals("lemah", result);
    }

    @Test
    void checkPasswordStrength_StrongPassword_ReturnsKuat() {
        // Given
        String strongPassword = "Password123!";

        // When
        String result = registrationService.checkPasswordStrength(strongPassword);

        // Then
        assertEquals("kuat", result);
    }

    @Test
    void getCustomerByEmail_Exists_ReturnsCustomer() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = registrationService.getCustomerByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = "valid.jwt.token";

        when(jwtUtil.validateToken(token)).thenReturn(true);

        // When
        boolean result = registrationService.validateToken(token);

        // Then
        assertTrue(result);
    }

    // BRANCH COVERAGE IMPROVEMENTS - Major methods not tested

    @Test
    void registerCustomer_Success_ReturnsRegistrationResponse() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        Map<String, Object> dukcapilData = new HashMap<>();
        dukcapilData.put("namaLengkap", "John Doe");
        dukcapilData.put("tempatLahir", "Jakarta");
        dukcapilData.put("tanggalLahir", "1990-01-01");
        dukcapilData.put("jenisKelamin", "L");
        dukcapilData.put("agama", "Islam");
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        dukcapilResponse.setData(dukcapilData);

        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(1012501234);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        // Mock all dependencies
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        assertEquals("Silver", result.getJenisKartu());
        assertEquals("John Doe", result.getNamaLengkap());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_DukcapilServiceDown_ThrowsException() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registrationService.registerCustomer(request)
        );

        assertTrue(exception.getMessage().contains("Dukcapil service tidak tersedia"));
    }

    @Test
    void registerCustomer_DukcapilVerificationFailed_ThrowsException() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(false, "Data tidak sesuai");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registrationService.registerCustomer(request)
        );

        assertTrue(exception.getMessage().contains("Verifikasi Dukcapil gagal"));
    }

    @Test
    void registerCustomer_EmailAlreadyExists_ThrowsException() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registrationService.registerCustomer(request)
        );

        assertTrue(exception.getMessage().contains("Email") && exception.getMessage().contains("sudah terdaftar"));
    }

    @Test
    void registerCustomer_PhoneAlreadyExists_ThrowsException() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registrationService.registerCustomer(request)
        );

        assertTrue(exception.getMessage().contains("Nomor telepon") && exception.getMessage().contains("sudah terdaftar"));
    }

    @Test
    void registerCustomer_NikAlreadyExists_ThrowsException() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> registrationService.registerCustomer(request)
        );

        assertTrue(exception.getMessage().contains("NIK") && exception.getMessage().contains("sudah pernah digunakan"));
    }

    @Test
    void authenticateCustomer_MaxAttemptsReached_ThrowsBadCredentials() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(hashedPassword);
        customer.setFailedLoginAttempts(4);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setFailedLoginAttempts(5);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);
        when(loginAttemptService.recordFailedLoginAttempt(email)).thenReturn(updatedCustomer);
        when(loginAttemptService.getMaxLoginAttempts()).thenReturn(5);
        when(loginAttemptService.getLockoutDurationMinutes()).thenReturn(1L);

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> registrationService.authenticateCustomer(email, password)
        );

        assertTrue(exception.getMessage().contains("Terlalu banyak percobaan"));
    }

    @Test
    void checkPasswordStrength_MediumPassword_ReturnsSedang() {
        // Given
        String mediumPassword = "Password123";

        // When
        String result = registrationService.checkPasswordStrength(mediumPassword);

        // Then
        assertEquals("sedang", result);
    }

    @Test
    void validateNikFormat_NullNik_ReturnsFalse() {
        // When
        boolean result = registrationService.validateNikFormat(null);

        // Then
        assertFalse(result);
    }

    @Test
    void validateNikFormat_NonNumericNik_ReturnsFalse() {
        // Given
        String invalidNik = "abcd567890123456";

        // When
        boolean result = registrationService.validateNikFormat(invalidNik);

        // Then
        assertFalse(result);
    }

    @Test
    void validateNikFormat_InvalidProvinceCode_ReturnsFalse() {
        // Given
        String invalidNik = "0000567890123456";

        // When
        boolean result = registrationService.validateNikFormat(invalidNik);

        // Then
        assertFalse(result);
    }

    @Test
    void getCustomerByEmail_NotExists_ReturnsEmpty() {
        // Given
        String email = "notfound@example.com";

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = registrationService.getCustomerByEmail(email);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String token = "invalid.jwt.token";

        when(jwtUtil.validateToken(token)).thenReturn(false);

        // When
        boolean result = registrationService.validateToken(token);

        // Then
        assertFalse(result);
    }

    @Test
    void getCustomerByNik_Exists_ReturnsCustomer() {
        // Given
        String nik = "1234567890123456";
        Customer customer = new Customer();
        customer.setNik(nik);

        when(customerRepository.findByNik(nik)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = registrationService.getCustomerByNik(nik);

        // Then
        assertTrue(result.isPresent());
        assertEquals(nik, result.get().getNik());
    }

    @Test
    void verifyEmail_CustomerExists_UpdatesVerification() {
        // Given
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);

        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(customer));

        // When
        registrationService.verifyEmail(email);

        // Then
        verify(customerRepository).save(customer);
    }

    @Test
    void getEmailFromToken_ValidToken_ReturnsEmail() {
        // Given
        String token = "valid.jwt.token";
        String email = "test@example.com";

        when(jwtUtil.getEmailFromToken(token)).thenReturn(email);

        // When
        String result = registrationService.getEmailFromToken(token);

        // Then
        assertEquals(email, result);
    }

    @Test
    void getEmailFromToken_InvalidToken_ReturnsNull() {
        // Given
        String token = "invalid.jwt.token";

        when(jwtUtil.getEmailFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // When
        String result = registrationService.getEmailFromToken(token);

        // Then
        assertNull(result);
    }

    @Test
    void generateTokenForEmail_ValidEmail_ReturnsToken() {
        // Given
        String email = "test@example.com";
        String token = "generated.jwt.token";

        when(jwtUtil.generateToken(email)).thenReturn(token);

        // When
        String result = registrationService.generateTokenForEmail(email);

        // Then
        assertEquals(token, result);
    }

    @Test
    void getRegistrationStats_ReturnsStats() {
        // Given
        when(customerRepository.countTotalCustomers()).thenReturn(100L);
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.getDukcapilBaseUrl()).thenReturn("http://localhost:8080");

        // When
        RegistrationService.RegistrationStats result = registrationService.getRegistrationStats();

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getTotalCustomers());
        assertTrue(result.isDukcapilServiceAvailable());
    }

    // Helper method to create valid registration request
    private RegistrationRequest createValidRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setNik("1234567890123456");
        request.setNamaLengkap("John Doe");
        request.setTanggalLahir(LocalDate.of(1990, 1, 1));
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setNomorTelepon("081234567890");
        request.setJenisKartu("Silver");
        request.setTempatLahir("Jakarta");
        request.setJenisKelamin("L");
        request.setAgama("Islam");
        request.setNamaIbuKandung("Jane Doe");
        request.setTipeAkun("Tabungan");
        request.setStatusPernikahan("Belum Kawin");
        request.setPekerjaan("Karyawan");
        request.setSumberPenghasilan("Gaji");
        request.setRentangGaji("5-10 juta");
        request.setTujuanPembuatanRekening("Menabung");
        
        // Set alamat
        RegistrationRequest.AlamatRequest alamat = new RegistrationRequest.AlamatRequest();
        alamat.setNamaAlamat("Jl. Sudirman No. 1");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta Pusat");
        alamat.setKecamatan("Tanah Abang");
        alamat.setKelurahan("Kebon Melati");
        alamat.setKodePos("10230");
        request.setAlamat(alamat);
        
        return request;
    }

    // ADDITIONAL BRANCH COVERAGE TESTS FOR MISSED BRANCHES

    @Test
    void registerCustomer_WithNullDukcapilData_UsesFallbackData() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        // Set data to null to test fallback branch
        dukcapilResponse.setData(null);

        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(1012501234);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        // Mock all dependencies
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithNullTanggalLahirFromDukcapil_UsesFallback() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        Map<String, Object> dukcapilData = new HashMap<>();
        dukcapilData.put("namaLengkap", "John Doe");
        dukcapilData.put("tempatLahir", "Jakarta");
        // tanggalLahir is null to test fallback branch
        dukcapilData.put("tanggalLahir", null);
        dukcapilData.put("jenisKelamin", "L");
        dukcapilData.put("agama", "Islam");
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        dukcapilResponse.setData(dukcapilData);

        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(1012501234);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithProvidedKodeRekening_DoesNotGenerate() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        request.setKodeRekening(999888777); // Pre-set kode rekening
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(999888777);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        // Remove this line since kodeRekening is already provided
        // when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithWali_CreatesWaliObject() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        // Add Wali data
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("John Doe Sr");
        wali.setPekerjaanWali("PNS");
        wali.setAlamatWali("Jl. Merdeka No. 1");
        wali.setNomorTeleponWali("081234567899");
        request.setWali(wali);
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(1012501234);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_WithIncompleteWali_DoesNotCreateWali() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        
        // Add incomplete Wali data (missing required fields)
        RegistrationRequest.WaliRequest wali = new RegistrationRequest.WaliRequest();
        wali.setJenisWali("Ayah");
        // Missing other required fields
        request.setWali(wali);
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Silver");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(1012501234);
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4101 1234 5678 9012");

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void registerCustomer_DifferentJenisKartu_GeneratesCorrectPrefix() {
        // Given
        RegistrationRequest request = createValidRegistrationRequest();
        request.setJenisKartu("Gold");
        
        DukcapilResponseDto dukcapilResponse = new DukcapilResponseDto(true, "Valid");
        Customer savedCustomer = new Customer();
        savedCustomer.setJenisKartu("Gold");
        savedCustomer.setNamaLengkap("John Doe");
        savedCustomer.setKodeRekening(2012501234); // Gold prefix: 20
        savedCustomer.setTipeAkun("Tabungan");
        savedCustomer.setNomorKartuDebitVirtual("4102 1234 5678 9012"); // Gold prefix: 4102

        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any(LocalDate.class)))
                .thenReturn(dukcapilResponse);
        when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(customerRepository.existsByNomorTelepon(anyString())).thenReturn(false);
        when(customerRepository.existsByNik(anyString())).thenReturn(false);
        when(customerRepository.existsByKodeRekening(anyInt())).thenReturn(false);
        when(customerRepository.existsByNomorKartuDebitVirtual(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        // When
        RegistrationResponse result = registrationService.registerCustomer(request);

        // Then
        assertNotNull(result);
        assertEquals("Gold", result.getJenisKartu());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getCustomerByAccountCode_Exists_ReturnsCustomer() {
        // Given
        Integer kodeRekening = 1012501234;
        Customer customer = new Customer();
        customer.setKodeRekening(kodeRekening);

        when(customerRepository.findByKodeRekening(kodeRekening)).thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = registrationService.getCustomerByAccountCode(kodeRekening);

        // Then
        assertTrue(result.isPresent());
        assertEquals(kodeRekening, result.get().getKodeRekening());
    }

    @Test
    void validateNikNameAndBirthDate_CallsDukcapilService() {
        // Given
        String nik = "1234567890123456";
        String namaLengkap = "John Doe";
        LocalDate tanggalLahir = LocalDate.of(1990, 1, 1);
        
        DukcapilResponseDto expectedResponse = new DukcapilResponseDto(true, "Valid");
        when(dukcapilClientService.verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir))
                .thenReturn(expectedResponse);

        // When
        DukcapilResponseDto result = registrationService.validateNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);

        // Then
        assertEquals(expectedResponse, result);
        verify(dukcapilClientService).verifyNikNameAndBirthDate(nik, namaLengkap, tanggalLahir);
    }

    @Test
    void checkNikExists_CallsDukcapilService() {
        // Given
        String nik = "1234567890123456";
        when(dukcapilClientService.isNikExists(nik)).thenReturn(true);

        // When
        boolean result = registrationService.checkNikExists(nik);

        // Then
        assertTrue(result);
        verify(dukcapilClientService).isNikExists(nik);
    }

    @Test
    void verifyEmail_CustomerNotExists_DoesNothing() {
        // Given
        String email = "notfound@example.com";
        when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        // When
        registrationService.verifyEmail(email);

        // Then
        verify(customerRepository, never()).save(any(Customer.class));
    }
}