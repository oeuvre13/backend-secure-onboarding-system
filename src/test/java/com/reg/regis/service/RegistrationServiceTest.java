package com.reg.regis.service;

import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import com.reg.regis.model.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

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
    
    @InjectMocks
    private RegistrationService registrationService;

    @BeforeEach
    void setUp() {
        // Setup default behavior
    }

    @Test
    void testValidateNikFormat_ValidNik() {
        String validNik = "1234567890123456";
        
        boolean result = registrationService.validateNikFormat(validNik);
        
        assertTrue(result);
    }

    @Test
    void testValidateNikFormat_InvalidNik() {
        String invalidNik = "123abc";
        
        boolean result = registrationService.validateNikFormat(invalidNik);
        
        assertFalse(result);
    }

    @Test
    void testCheckPasswordStrength_WeakPassword() {
        String weakPassword = "123";
        
        String result = registrationService.checkPasswordStrength(weakPassword);
        
        assertEquals("lemah", result);
    }

    @Test
    void testCheckPasswordStrength_StrongPassword() {
        String strongPassword = "Test123@Strong";
        
        String result = registrationService.checkPasswordStrength(strongPassword);
        
        assertEquals("kuat", result);
    }

    @Test
    void testGetCustomerByEmail() {
        String email = "test@example.com";
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        
        when(customerRepository.findByEmailIgnoreCase(email))
            .thenReturn(Optional.of(mockCustomer));
        
        Optional<Customer> result = registrationService.getCustomerByEmail(email);
        
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void testVerifyEmail() {
        String email = "test@example.com";
        Customer mockCustomer = new Customer();
        mockCustomer.setEmail(email);
        mockCustomer.setEmailVerified(false);
        
        when(customerRepository.findByEmailIgnoreCase(email))
            .thenReturn(Optional.of(mockCustomer));
        
        registrationService.verifyEmail(email);
        
        assertTrue(mockCustomer.getEmailVerified());
        verify(customerRepository).save(mockCustomer);
    }

    @Test
    void testValidateToken() {
        String token = "valid.jwt.token";
        
        when(jwtUtil.validateToken(token)).thenReturn(true);
        
        boolean result = registrationService.validateToken(token);
        
        assertTrue(result);
        verify(jwtUtil).validateToken(token);
    }
}