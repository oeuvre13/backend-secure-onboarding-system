// src/main/java/com/reg/regis/controller/LoginController.java
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
     * Customer login dengan cookie-based auth dan mengembalikan Bearer Token
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Map<String, String> loginRequest, HttpServletResponse response) {
        try {
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            String token = registrationService.authenticateCustomer(email, password); // Ini akan memanggil JwtUtil dari RegistrationService
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                // Set HTTP-only cookie
                Cookie authCookie = new Cookie("authToken", token);
                authCookie.setHttpOnly(true); // Prevent XSS attacks
                authCookie.setSecure(false); // Set to true in production with HTTPS!
                authCookie.setPath("/");
                authCookie.setMaxAge(24 * 60 * 60); // 24 hours
                authCookie.setDomain("localhost"); // Set domain for cookie, sesuaikan untuk production
                response.addCookie(authCookie);
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("message", "Login berhasil");
                responseData.put("customer", buildCustomerResponse(customer));
                responseData.put("token", token); // Sertakan token dalam respons JSON
                
                return ResponseEntity.ok(responseData);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Customer tidak ditemukan"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Check auth status dari cookie atau header Authorization (Bearer Token)
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(value = "authToken", required = false) String cookieToken,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Ambil token setelah "Bearer "
        } else if (cookieToken != null && !cookieToken.isEmpty()) {
            token = cookieToken; // Fallback ke cookie jika header tidak ada
        }

        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Tidak terautentikasi: Token tidak ditemukan"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak valid atau kadaluarsa"));
            }
            
            Optional<Customer> customerOpt = registrationService.getCustomerByEmail(email);
            if (customerOpt.isPresent()) {
                Customer customer = customerOpt.get();
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("authenticated", true);
                responseData.put("customer", buildCustomerResponse(customer));
                
                return ResponseEntity.ok(responseData);
            }
            
            return ResponseEntity.status(401).body(Map.of("error", "User tidak ditemukan"));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Autentikasi gagal: " + e.getMessage()));
        }
    }
    
    /**
     * Logout - clear cookie
     * (Untuk Bearer Token, logout di sisi klien hanya perlu menghapus token dari storage mereka)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the auth cookie
        Cookie authCookie = new Cookie("authToken", "");
        authCookie.setHttpOnly(true);
        authCookie.setSecure(false); // Ganti ke true di production dengan HTTPS!
        authCookie.setPath("/");
        authCookie.setMaxAge(0); // Expire immediately
        authCookie.setDomain("localhost"); // Sesuaikan untuk production
        response.addCookie(authCookie);
        
        return ResponseEntity.ok(Map.of("message", "Logout berhasil"));
    }
    
    /**
     * Refresh JWT token dari cookie atau header Authorization
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "authToken", required = false) String cookieToken,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader,
                                          HttpServletResponse response) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (cookieToken != null && !cookieToken.isEmpty()) {
            token = cookieToken;
        }

        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak ada"));
            }
            
            String email = registrationService.getEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Token tidak valid atau kadaluarsa"));
            }
            
            // Generate new token (refresh tanpa password)
            String newToken = registrationService.generateTokenForEmail(email);
            
            // Set new cookie
            Cookie authCookie = new Cookie("authToken", newToken);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(false); // Ganti ke true di production dengan HTTPS!
            authCookie.setPath("/");
            authCookie.setMaxAge(24 * 60 * 60); // 24 hours
            authCookie.setDomain("localhost"); // Sesuaikan untuk production
            response.addCookie(authCookie);
            
            return ResponseEntity.ok(Map.of(
                "message", "Token berhasil diperbarui",
                "token", newToken // KEMBALIKAN TOKEN BARU DI RESPON JSON
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token gagal: " + e.getMessage()));
        }
    }
    
    /**
     * Check if user is authenticated (dari cookie atau header Authorization)
     */
    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthentication(@CookieValue(value = "authToken", required = false) String cookieToken,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (cookieToken != null && !cookieToken.isEmpty()) {
            token = cookieToken;
        }

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
    
    /**
     * Helper method untuk build customer response
     */
    private Map<String, Object> buildCustomerResponse(Customer customer) {
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("id", customer.getId());
        customerData.put("namaLengkap", customer.getNamaLengkap());
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
        
        // Alamat info (jika ada)
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
        
        // Wali info (jika ada)
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