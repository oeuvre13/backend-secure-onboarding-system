package com.reg.regis.controller;

import com.reg.regis.dto.RegistrationRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    /**
     * Register new customer
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegistrationRequest request) {
        try {
            Customer customer = registrationService.registerCustomer(request);
            String token = registrationService.authenticateCustomer(request.getEmail(), request.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("token", token);
            response.put("customer", Map.of(
                "id", customer.getId(),
                "name", customer.getName(),
                "email", customer.getEmail(),
                "phone", customer.getPhone(),
                "age", customer.getAge(),
                "emailVerified", customer.getEmailVerified()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Customer login
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Map<String, String> loginRequest) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            String token = registrationService.authenticateCustomer(email, password);
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("customer", Map.of(
                    "id", customer.getId(),
                    "name", customer.getName(),
                    "email", customer.getEmail(),
                    "phone", customer.getPhone(),
                    "age", customer.getAge(),
                    "emailVerified", customer.getEmailVerified()
                ));
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Customer not found"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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