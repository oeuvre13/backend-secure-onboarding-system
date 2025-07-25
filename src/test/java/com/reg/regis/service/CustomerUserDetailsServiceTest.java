package com.reg.regis.service;

import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerUserDetailsService userDetailsService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("encodedPassword123");
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        when(customerRepository.findByEmailIgnoreCase("test@example.com"))
            .thenReturn(Optional.of(customer));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(customerRepository.findByEmailIgnoreCase("notfound@example.com"))
            .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername("notfound@example.com")
        );

        assertEquals("User not found with email: notfound@example.com", exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_CaseInsensitive() {
        when(customerRepository.findByEmailIgnoreCase("TEST@EXAMPLE.COM"))
            .thenReturn(Optional.of(customer));

        UserDetails userDetails = userDetailsService.loadUserByUsername("TEST@EXAMPLE.COM");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(customerRepository).findByEmailIgnoreCase("TEST@EXAMPLE.COM");
    }

    @Test
    void testLoadUserByUsername_EmptyEmail() {
        when(customerRepository.findByEmailIgnoreCase(""))
            .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(""));
    }

    @Test
    void testLoadUserByUsername_NullEmail() {
        when(customerRepository.findByEmailIgnoreCase(null))
            .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> userDetailsService.loadUserByUsername(null));
    }
}
