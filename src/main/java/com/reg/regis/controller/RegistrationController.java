package com.reg.regis.controller;

import com.reg.regis.dto.RegistrationRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    /**
     * Register new customer with cookie-based auth
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegistrationRequest request, HttpServletResponse response) {
        try {
            Customer customer = registrationService.registerCustomer(request);
            String token = registrationService.authenticateCustomer(request.getEmail(), request.getPassword());
            
            // Set HTTP-only cookie
            Cookie authCookie = new Cookie("authToken", token);
            authCookie.setHttpOnly(true); // Prevent XSS
            authCookie.setSecure(false); // Set to true in production with HTTPS
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(authCookie);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("message", "Registration successful");
            responseData.put("customer", Map.of(
                "id", customer.getId(),
                "name", customer.getName(),
                "email", customer.getEmail(),
                "phone", customer.getPhone(),
                "age", customer.getAge(),
                "emailVerified", customer.getEmailVerified()
            ));
            // Don't send token in response body for security
            
            return ResponseEntity.ok(responseData);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Customer login with cookie-based auth
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            String token = registrationService.authenticateCustomer(email, password);
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                // Set HTTP-only cookie
                Cookie authCookie = new Cookie("authToken", token);
                authCookie.setHttpOnly(true); // Prevent XSS attacks
                authCookie.setSecure(false); // Set to true in production with HTTPS
                authCookie.setPath("/");
                authCookie.setMaxAge(24 * 60 * 60); // 24 hours
                authCookie.setDomain("localhost"); // Set domain for cookie
                response.addCookie(authCookie);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "Login successful");
                responseData.put("customer", Map.of(
                    "id", customer.getId(),
                    "name", customer.getName(),
                    "email", customer.getEmail(),
                    "phone", customer.getPhone(),
                    "age", customer.getAge(),
                    "emailVerified", customer.getEmailVerified()
                ));
                // Don't send token in response body
                
                return ResponseEntity.ok(responseData);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Customer not found"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Logout - clear cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the auth cookie
        Cookie authCookie = new Cookie("authToken", "");
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false);
        authCookie.setPath("/");
        authCookie.setMaxAge(0); // Expire immediately
        authCookie.setDomain("localhost");
        response.addCookie(authCookie);
        
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    
    /**
     * Check auth status from cookie
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "authToken", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            
            // Validate token and get user
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }
            
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("authenticated", true);
                responseData.put("customer", Map.of(
                    "id", customer.getId(),
                    "name", customer.getName(),
                    "email", customer.getEmail(),
                    "phone", customer.getPhone(),
                    "age", customer.getAge(),
                    "emailVerified", customer.getEmailVerified()
                ));
                
                return ResponseEntity.ok(responseData);
            }
            
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
        }
    }
    
    /**
     * Check password strength
     */
    @PostMapping("/check-password")
    public ResponseEntity<?> checkPasswordStrength(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String strength = registrationService.checkPasswordStrength(password);
        
        return ResponseEntity.ok(Map.of("strength", strength));
    }
    
    /**
     * Verify email
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            registrationService.verifyEmail(email);
            
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get registration statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getRegistrationStats() {
        return ResponseEntity.ok(registrationService.getRegistrationStats());
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Secure Customer Registration",
            "timestamp", System.currentTimeMillis()
        ));
    }
}