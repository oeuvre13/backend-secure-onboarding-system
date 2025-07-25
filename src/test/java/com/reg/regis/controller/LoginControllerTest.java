package com.reg.regis.controller;

import com.reg.regis.controller.LoginController.LoginRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private LoginController loginController;

    private LoginRequest loginRequest;
    private Customer customer;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        // Set default values for @Value fields
        ReflectionTestUtils.setField(loginController, "cookieSecure", false);
        ReflectionTestUtils.setField(loginController, "cookieDomain", "");
        
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john@example.com");
        customer.setNamaLengkap("John Doe");
        customer.setJenisKartu("Silver");
    }

    @Test
    void testLoginCustomer_Success() {
        when(registrationService.authenticateCustomer("john@example.com", "password123"))
            .thenReturn("jwt-token");
        when(registrationService.getCustomerByEmail("john@example.com"))
            .thenReturn(Optional.of(customer));

        ResponseEntity<?> result = loginController.loginCustomer(loginRequest, request, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("Login successful", body.get("message"));
        assertEquals("jwt-token", body.get("token"));
    }

    @Test
    void testLoginCustomer_BadCredentials() {
        when(registrationService.authenticateCustomer("john@example.com", "wrongpassword"))
            .thenThrow(new BadCredentialsException("Email atau password salah"));

        loginRequest.setPassword("wrongpassword");
        ResponseEntity<?> result = loginController.loginCustomer(loginRequest, request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("Email atau password salah", body.get("error"));
    }

    @Test
    void testLoginCustomer_AccountLocked() {
        when(registrationService.authenticateCustomer("john@example.com", "password123"))
            .thenThrow(new BadCredentialsException("Akun Anda terkunci"));

        ResponseEntity<?> result = loginController.loginCustomer(loginRequest, request, response);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    void testGetCurrentUser_ValidToken() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = loginController.getCurrentUser("valid-token", null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("authenticated"));
    }

    @Test
    void testGetCurrentUser_InvalidToken() {
        when(registrationService.getEmailFromToken("invalid-token")).thenReturn(null);

        ResponseEntity<?> result = loginController.getCurrentUser("invalid-token", null);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Invalid token", body.get("error"));
    }

    @Test
    void testGetCurrentUser_NoToken() {
        ResponseEntity<?> result = loginController.getCurrentUser(null, null);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Authentication required", body.get("error"));
    }

    @Test
    void testLogout() {
        ResponseEntity<?> result = loginController.logout(response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("Logout successful", body.get("message"));
    }

    @Test
    void testRefreshToken_Success() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.generateTokenForEmail("john@example.com")).thenReturn("new-jwt-token");

        ResponseEntity<?> result = loginController.refreshToken("valid-token", null, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("new-jwt-token", body.get("token"));
    }

    @Test
    void testRefreshToken_InvalidToken() {
        when(registrationService.getEmailFromToken("invalid-token")).thenReturn(null);

        ResponseEntity<?> result = loginController.refreshToken("invalid-token", null, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertEquals("Invalid token", body.get("error"));
    }

    @Test
    void testCheckAuthentication_Authenticated() {
        when(registrationService.getEmailFromToken("valid-token")).thenReturn("john@example.com");
        when(registrationService.getCustomerByEmail("john@example.com")).thenReturn(Optional.of(customer));

        ResponseEntity<?> result = loginController.checkAuthentication("valid-token", null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertTrue((Boolean) body.get("authenticated"));
        assertEquals("john@example.com", body.get("email"));
    }

    @Test
    void testCheckAuthentication_NotAuthenticated() {
        ResponseEntity<?> result = loginController.checkAuthentication(null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) result.getBody();
        assertFalse((Boolean) body.get("authenticated"));
    }
}
