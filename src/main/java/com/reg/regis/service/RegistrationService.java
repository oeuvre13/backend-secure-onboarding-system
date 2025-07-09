package com.reg.regis.service;

import com.reg.regis.dto.RegistrationRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class RegistrationService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Register new customer
     */
    @Transactional
    public Customer registerCustomer(RegistrationRequest request) {
        // Check if email already exists
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        // Check if phone already exists
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail().toLowerCase());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setPhone(request.getPhone());
        customer.setAge(request.getAge());
        customer.setEmailVerified(false);
        
        // Save to database
        return customerRepository.save(customer);
    }
    
    /**
     * Authenticate customer login
     */
    public String authenticateCustomer(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }
        
        Customer customer = customerOpt.get();
        
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Generate JWT token
        return jwtUtil.generateToken(customer.getEmail());
    }
    
    /**
     * Get customer by email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }
    
    /**
     * Verify customer email
     */
    @Transactional
    public void verifyEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setEmailVerified(true);
            customerRepository.save(customer);
        }
    }
    
    /**
     * Check password strength
     */
    public String checkPasswordStrength(String password) {
        int score = 0;
        
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@$!%*?&].*")) score++;
        
        return switch (score) {
            case 0, 1, 2 -> "weak";
            case 3, 4 -> "medium";
            case 5 -> "strong";
            default -> "weak";
        };
    }
    
    /**
     * Get email from JWT token
     */
    public String getEmailFromToken(String token) {
        try {
            return jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    


    // Inner class for stats response
    public static class RegistrationStats {
        private final long totalCustomers;
        private final long verifiedCustomers;
        private final double verificationRate;
        
        public RegistrationStats(long totalCustomers, long verifiedCustomers, double verificationRate) {
            this.totalCustomers = totalCustomers;
            this.verifiedCustomers = verifiedCustomers;
            this.verificationRate = verificationRate;
        }
        
        public long getTotalCustomers() { return totalCustomers; }
        public long getVerifiedCustomers() { return verifiedCustomers; }
        public double getVerificationRate() { return verificationRate; }
    }    
    
    /**
     * Get registration statistics - ADD THIS METHOD
     */
    public RegistrationStats getRegistrationStats() {
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = customerRepository.countVerifiedCustomers();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
            
        return new RegistrationStats(totalCustomers, verifiedCustomers, verificationRate);
    }
}