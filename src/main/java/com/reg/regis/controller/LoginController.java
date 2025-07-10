package com.reg.regis.controller;

import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class LoginController {
    
    @Autowired
    private RegistrationService registrationService;
    
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
     * Refresh JWT token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "authToken", required = false) String token, HttpServletResponse response) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "No token provided"));
            }
            
            // Validate current token
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
            }
            
            // Generate new token
            String newToken = registrationService.authenticateCustomer(email, null); // Assuming we can refresh without password
            
            // Set new cookie
            Cookie authCookie = new Cookie("authToken", newToken);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false);
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60); // 24 hours
            authCookie.setDomain("localhost");
            response.addCookie(authCookie);
            
            return ResponseEntity.ok(Map.of(
                "message", "Token refreshed successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token refresh failed"));
        }
    }
    
    /**
     * Check if user is authenticated
     */
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthentication(@CookieValue(value = "authToken", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.ok(Map.of("authenticated", false));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email != null) {
                Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
                if (customerOpt.isPresent()) {
                    return ResponseEntity.ok(Map.of(
                        "authenticated", true,
                        "email", email
                    ));
                }
            }
            
            return ResponseEntity.ok(Map.of("authenticated", false));
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
    }
}