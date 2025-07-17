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

@RestController
@RequestMapping("/auth") 
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    /**
     * Register customer dengan validasi ketat
     * - NIK harus ada di database KTP Dukcapil via HTTP call
     * - Nama harus sesuai dengan KTP
     * - Email tidak boleh duplikat
     * - Nomor HP tidak boleh duplikat
     */
    @PostMapping("/register")  // ← Sekarang jadi POST /auth/register
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody RegistrationRequest request, HttpServletResponse response) {
        try {
            Customer customer = registrationService.registerCustomer(request);
            String token = registrationService.authenticateCustomer(request.getEmail(), request.getPassword());
            
            // Set HTTP-only cookie
            Cookie authCookie = new Cookie("authToken", token);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false);
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60);
            authCookie.setDomain("localhost");
            response.addCookie(authCookie);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.");
            responseData.put("customer", buildCustomerResponse(customer));
            
            return ResponseEntity.ok(responseData);
            
        } catch (RuntimeException e) {
            // Return specific validation error
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "type", "validation_error"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Terjadi kesalahan sistem: " + e.getMessage(),
                "type", "system_error"
            ));
        }
    }
    
    /**
     * Check password strength
     */
    @PostMapping("/check-password")  // ← POST /auth/check-password
    public ResponseEntity<?> checkPasswordStrength(@RequestBody Map<String, String> request) {
        String password = request.get("password");
        String strength = registrationService.checkPasswordStrength(password);
        
        return ResponseEntity.ok(Map.of("strength", strength));
    }
    
    /**
     * Validate NIK format
     */
    @PostMapping("/validate-nik")  // ← POST /auth/validate-nik
    public ResponseEntity<?> validateNik(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            boolean isValid = registrationService.validateNikFormat(nik);
            boolean isExists = registrationService.getCustomerByNik(nik).isPresent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("exists", isExists);
            response.put("message", isValid ? 
                (isExists ? "NIK sudah terdaftar" : "NIK valid") : 
                "Format NIK tidak valid");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Verify email customer
     */
    @PostMapping("/verify-email")  // ← POST /auth/verify-email
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            registrationService.verifyEmail(email);
            
            return ResponseEntity.ok(Map.of("message", "Email berhasil diverifikasi"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get registration statistics
     */
    @GetMapping("/stats")  // ← GET /auth/stats
    public ResponseEntity<?> getRegistrationStats() {
        return ResponseEntity.ok(registrationService.getRegistrationStats());
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")  // ← GET /auth/health
    public ResponseEntity<?> healthCheck() {
        var stats = registrationService.getRegistrationStats();
        
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Customer Registration Service dengan Dukcapil Integration",
            "timestamp", System.currentTimeMillis(),
            "dukcapilService", Map.of(
                "url", stats.getDukcapilServiceUrl(),
                "available", stats.isDukcapilServiceAvailable()
            ),
            "statistics", Map.of(
                "totalCustomers", stats.getTotalCustomers(),
                "verifiedCustomers", stats.getVerifiedCustomers(),
                "verificationRate", stats.getVerificationRate() + "%"
            ),
            "endpoints", Map.of(
                "register", "POST /auth/register",
                "checkPassword", "POST /auth/check-password",
                "validateNik", "POST /auth/validate-nik",
                "verifyEmail", "POST /auth/verify-email",
                "stats", "GET /auth/stats",
                "health", "GET /auth/health",
                "profile", "GET /auth/profile"
            )
        ));
    }
    
    /**
     * Get customer profile  
     */
    @GetMapping("/profile")  // ← GET /auth/profile
    public ResponseEntity<?> getCustomerProfile(@CookieValue(value = "authToken", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak ditemukan"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak valid"));
            }
            
            var customerOpt = registrationService.getCustomerByEmail(email);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                return ResponseEntity.ok(Map.of(
                    "profile", buildCustomerResponse(customer)
                ));
            }
            
            return ResponseEntity.status(404).body(Map.of("error", "Customer tidak ditemukan"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Gagal mengambil profil"));
        }
    }
    
    /**
     * Helper method untuk build customer response
     */
    private Map<String, Object> buildCustomerResponse(Customer customer) {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("id", customer.getId());
        customerData.put("namaLengkap", customer.getNamaLengkap());
        customerData.put("nik", customer.getNik());
        customerData.put("email", customer.getEmail());
        customerData.put("nomorTelepon", customer.getNomorTelepon());
        customerData.put("tipeAkun", customer.getTipeAkun());
        customerData.put("tempatLahir", customer.getTempatLahir());
        customerData.put("tanggalLahir", customer.getTanggalLahir());
        customerData.put("jenisKelamin", customer.getJenisKelamin());
        customerData.put("agama", customer.getAgama());
        customerData.put("statusPernikahan", customer.getStatusPernikahan());
        customerData.put("pekerjaan", customer.getPekerjaan());
        customerData.put("emailVerified", customer.getEmailVerified());
        
        // Alamat info
        if (customer.getAlamat() != null) {
            Map<String, Object> alamatData = new HashMap<>();
            alamatData.put("namaAlamat", customer.getAlamat().getNamaAlamat());
            alamatData.put("provinsi", customer.getAlamat().getProvinsi());
            alamatData.put("kota", customer.getAlamat().getKota());
            alamatData.put("kecamatan", customer.getAlamat().getKecamatan());
            alamatData.put("kelurahan", customer.getAlamat().getKelurahan());
            alamatData.put("kodePos", customer.getAlamat().getKodePos());
            customerData.put("alamat", alamatData);
        }
        
        // Wali info
        if (customer.getWali() != null) {
            Map<String, Object> waliData = new HashMap<>();
            waliData.put("jenisWali", customer.getWali().getJenisWali());
            waliData.put("namaLengkapWali", customer.getWali().getNamaLengkapWali());
            waliData.put("pekerjaanWali", customer.getWali().getPekerjaanWali());
            waliData.put("nomorTeleponWali", customer.getWali().getNomorTeleponWali());
            customerData.put("wali", waliData);
        }
        
        return customerData;
    }
}