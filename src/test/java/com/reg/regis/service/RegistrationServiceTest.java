package com.reg.regis.service;

import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.response.DukcapilResponseDto;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import com.reg.regis.test.factory.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public
class RegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private DukcapilClientService dukcapilClientService;
    
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegisterCustomer_DukcapilServiceDown() {
        // Given
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("test@example.com");
        
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registrationService.registerCustomer(request));
        
        assertEquals("Dukcapil service tidak tersedia. Silakan coba lagi nanti.", 
            exception.getMessage());
        
        verify(customerRepository, never()).save(any());
    }
    
    @Test
    void testRegisterCustomer_EmailAlreadyExists() {
        // Given
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("john.doe@example.com");
        request.setNik("3175031234567890");
        request.setNamaLengkap("John Doe");
        
        when(dukcapilClientService.isDukcapilServiceHealthy()).thenReturn(true);
        
        DukcapilResponseDto mockResponse = new DukcapilResponseDto();
        mockResponse.setValid(true);
        mockResponse.setMessage("Data valid");
        mockResponse.setData(new HashMap<>());
        
        when(dukcapilClientService.verifyNikNameAndBirthDate(anyString(), anyString(), any()))
            .thenReturn(mockResponse);
        
        when(customerRepository.existsByEmailIgnoreCase("john.doe@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registrationService.registerCustomer(request));
        
        assertTrue(exception.getMessage().contains("Email john.doe@example.com sudah terdaftar"));
        verify(customerRepository, never()).save(any());
    }
    
    @Test
    void testValidateNikFormat_ValidNik() {
        // Given
        String validNik = "3175031234567890";

        // When
        boolean result = registrationService.validateNikFormat(validNik);

        // Then
        assertTrue(result);
    }
    
    @Test
    void testValidateNikFormat_InvalidNik() {
        // When & Then
        assertFalse(registrationService.validateNikFormat("123")); // Too short
        assertFalse(registrationService.validateNikFormat("1234567890123456a")); // Contains letter
        assertFalse(registrationService.validateNikFormat(null)); // Null
    }
    
    @Test
    void testCheckPasswordStrength() {
        // When & Then
        assertEquals("lemah", registrationService.checkPasswordStrength("123"));
        assertEquals("sedang", registrationService.checkPasswordStrength("Password123"));
        assertEquals("kuat", registrationService.checkPasswordStrength("Password123!"));
    }
    
    @Test
    void testAuthenticateCustomer_Success() {
        // Given
        Customer customer = TestDataFactory.createJohnDoe();
        customer.setEmail("john.doe@example.com");
        customer.setPassword("encoded_password");
        
        when(customerRepository.findByEmailIgnoreCase("john.doe@example.com"))
            .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("JohnDoe123!", "encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("john.doe@example.com")).thenReturn("jwt_token");

        // When
        String token = registrationService.authenticateCustomer("john.doe@example.com", "JohnDoe123!");

        // Then
        assertEquals("jwt_token", token);
    }
    
    @Test
    void testAuthenticateCustomer_WrongPassword() {
        // Given
        Customer customer = TestDataFactory.createJohnDoe();
        customer.setEmail("john.doe@example.com");
        customer.setPassword("encoded_password");
        
        when(customerRepository.findByEmailIgnoreCase("john.doe@example.com"))
            .thenReturn(Optional.of(customer));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> registrationService.authenticateCustomer("john.doe@example.com", "wrongpassword"));
        
        assertEquals("Email atau password salah", exception.getMessage());
    }
    
    @Test
    void testGetCustomerByEmail() {
        // Given
        Customer customer = TestDataFactory.createJohnDoe();
        when(customerRepository.findByEmailIgnoreCase("john.doe@example.com"))
            .thenReturn(Optional.of(customer));

        // When
        Optional<Customer> result = registrationService.getCustomerByEmail("john.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getNamaLengkap());
    }
}