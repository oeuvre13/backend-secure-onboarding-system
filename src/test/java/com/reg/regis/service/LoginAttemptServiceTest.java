package com.reg.regis.service;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private LoginAttemptService loginAttemptService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setFailedLoginAttempts(0);
    }

    @Test
    void testRecordFailedLoginAttempt_CustomerExists() {
        when(customerRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = loginAttemptService.recordFailedLoginAttempt("test@example.com");

        assertEquals(1, result.getFailedLoginAttempts());
        verify(customerRepository).save(customer);
    }

    @Test
    void testRecordFailedLoginAttempt_CustomerNotFound() {
        when(customerRepository.findByEmailIgnoreCase("notfound@example.com"))
            .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> 
            loginAttemptService.recordFailedLoginAttempt("notfound@example.com"));
    }

    @Test
    void testRecordFailedLoginAttempt_AccountLocked() {
        customer.setFailedLoginAttempts(4); // One more will reach max
        when(customerRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = loginAttemptService.recordFailedLoginAttempt("test@example.com");

        assertNotNull(result.getAccountLockedUntil());
        assertEquals(0, result.getFailedLoginAttempts()); // Reset after lock
        assertTrue(result.getAccountLockedUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void testRecordFailedLoginAttempt_MultipleAttempts() {
        when(customerRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // First attempt
        Customer result1 = loginAttemptService.recordFailedLoginAttempt("test@example.com");
        assertEquals(1, result1.getFailedLoginAttempts());

        // Second attempt
        customer.setFailedLoginAttempts(1);
        Customer result2 = loginAttemptService.recordFailedLoginAttempt("test@example.com");
        assertEquals(2, result2.getFailedLoginAttempts());
    }

    @Test
    void testGetMaxLoginAttempts() {
        assertEquals(5, loginAttemptService.getMaxLoginAttempts());
    }

    @Test
    void testGetLockoutDurationMinutes() {
        assertEquals(1, loginAttemptService.getLockoutDurationMinutes());
    }
}
