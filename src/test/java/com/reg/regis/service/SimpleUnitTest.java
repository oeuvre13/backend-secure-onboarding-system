package com.reg.regis.service;

// import com.reg.regis.service.RegistrationService;
import com.reg.regis.test.factory.TestDataFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public
class SimpleUnitTest {

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
    void testPasswordStrength_Weak() {
        String result = registrationService.checkPasswordStrength("123");
        assertEquals("lemah", result);
    }
    
    @Test
    void testPasswordStrength_Medium() {
        String result = registrationService.checkPasswordStrength("Password123");
        assertEquals("sedang", result);
    }
    
    @Test
    void testPasswordStrength_Strong() {
        String result = registrationService.checkPasswordStrength("Password123!");
        assertEquals("kuat", result);
    }
    
    @Test
    void testNikValidation_Valid() {
        boolean result = registrationService.validateNikFormat("3175031234567890");
        assertTrue(result);
    }
    
    @Test
    void testNikValidation_Invalid_TooShort() {
        boolean result = registrationService.validateNikFormat("123");
        assertFalse(result);
    }
    
    @Test
    void testNikValidation_Invalid_Null() {
        boolean result = registrationService.validateNikFormat(null);
        assertFalse(result);
    }

    @Test
    void testTestDataFactory() {
        Customer john = TestDataFactory.createJohnDoe();
        assertEquals("John Doe", john.getNamaLengkap());
        assertEquals("3175031234567890", john.getNik());
    }
}