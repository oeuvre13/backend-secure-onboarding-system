package com.reg.regis.controller;

import com.reg.regis.dto.NikVerificationRequest;
import com.reg.regis.dto.EmailVerificationRequest;
import com.reg.regis.dto.PhoneVerificationRequest;
import com.reg.regis.dto.VerificationResponse;
import com.reg.regis.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/verification")
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class VerificationController {
    
    @Autowired
    private VerificationService verificationService;
    
    /**
     * Verifikasi NIK dengan nama lengkap - Support both DTO dan Map
     */
    @PostMapping("/nik")
    public ResponseEntity<?> verifyNik(@RequestBody Map<String, String> requestBody) {
        try {
            // Extract data dari request body (dari React)
            String nik = requestBody.get("nik");
            String namaLengkap = requestBody.get("namaLengkap");
            
            // Validasi input
            if (nik == null || nik.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "NIK wajib diisi"
                ));
            }
            
            if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Nama lengkap wajib diisi"
                ));
            }
            
            if (nik.length() != 16 || !nik.matches("^[0-9]{16}$")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "NIK harus 16 digit angka"
                ));
            }
            
            // Buat DTO untuk service
            NikVerificationRequest request = new NikVerificationRequest(nik, namaLengkap);
            VerificationResponse response = verificationService.verifyNik(request);
            
            if (response.isValid()) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", response.getMessage(),
                    "data", response.getData() != null ? response.getData() : Map.of()
                ));
            } else {
                return ResponseEntity.ok(Map.of( // Ganti ke 200 OK agar FE bisa handle
                    "valid", false,
                    "message", response.getMessage()
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error in verifyNik: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Terjadi kesalahan saat verifikasi NIK: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Alternative endpoint dengan @Valid DTO (untuk consistency)
     */
    @PostMapping("/nik-dto")
    public ResponseEntity<?> verifyNikWithDto(@Valid @RequestBody NikVerificationRequest request) {
        try {
            VerificationResponse response = verificationService.verifyNik(request);
            
            if (response.isValid()) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", response.getMessage(),
                    "data", response.getData()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", response.getMessage()
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Terjadi kesalahan saat verifikasi NIK: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Verifikasi email (check ketersediaan)
     */
    @PostMapping("/email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            VerificationResponse response = verificationService.verifyEmail(request);
            
            return ResponseEntity.ok(Map.of(
                "available", response.isValid(),
                "message", response.getMessage(),
                "data", response.getData() != null ? response.getData() : Map.of()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "available", false,
                "message", "Terjadi kesalahan saat verifikasi email: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Verifikasi nomor telepon (check ketersediaan)
     */
    @PostMapping("/phone")
    public ResponseEntity<?> verifyPhone(@Valid @RequestBody PhoneVerificationRequest request) {
        try {
            VerificationResponse response = verificationService.verifyPhone(request);
            
            return ResponseEntity.ok(Map.of(
                "available", response.isValid(),
                "message", response.getMessage(),
                "data", response.getData() != null ? response.getData() : Map.of()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "available", false,
                "message", "Terjadi kesalahan saat verifikasi nomor telepon: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Check NIK tanpa nama (simple check)
     */
    @PostMapping("/nik-check")
    public ResponseEntity<?> checkNik(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            
            if (nik == null || nik.length() != 16) {
                return ResponseEntity.badRequest().body(Map.of(
                    "registered", false,
                    "message", "NIK harus 16 digit"
                ));
            }
            
            boolean isRegistered = verificationService.isNikRegistered(nik);
            
            return ResponseEntity.ok(Map.of(
                "registered", isRegistered,
                "message", isRegistered ? 
                    "NIK terdaftar di database Dukcapil" : 
                    "NIK tidak terdaftar di database Dukcapil"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "registered", false,
                "message", "Terjadi kesalahan saat pengecekan NIK: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get data KTP berdasarkan NIK (untuk testing)
     */
    @GetMapping("/ktp-data/{nik}")
    public ResponseEntity<?> getKtpData(@PathVariable String nik) {
        try {
            if (nik.length() != 16) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "NIK harus 16 digit"
                ));
            }
            
            var ktpOpt = verificationService.getKtpDataByNik(nik);
            
            if (ktpOpt.isPresent()) {
                var ktpData = ktpOpt.get();
                return ResponseEntity.ok(Map.of(
                    "found", true,
                    "data", Map.of(
                        "nik", ktpData.getNik(),
                        "namaLengkap", ktpData.getNamaLengkap(),
                        "tempatLahir", ktpData.getTempatLahir(),
                        "tanggalLahir", ktpData.getTanggalLahir(),
                        "jenisKelamin", ktpData.getJenisKelamin().getValue(),
                        "alamat", ktpData.getNamaAlamat(),
                        "kecamatan", ktpData.getKecamatan(),
                        "kelurahan", ktpData.getKelurahan(),
                        "agama", ktpData.getAgama().getValue(),
                        "statusPerkawinan", ktpData.getStatusPerkawinan()
                    )
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "Data KTP tidak ditemukan"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Terjadi kesalahan saat mengambil data KTP: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get verification statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getVerificationStats() {
        try {
            return ResponseEntity.ok(verificationService.getVerificationStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Terjadi kesalahan saat mengambil statistik: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Health check untuk verification service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Verification Service (NIK, Email, Phone)",
            "timestamp", System.currentTimeMillis(),
            "supportedFormats", Map.of(
                "nikVerification", "{ \"nik\": \"string\", \"namaLengkap\": \"string\" }",
                "nikCheck", "{ \"nik\": \"string\" }",
                "emailVerification", "{ \"email\": \"string\" }",
                "phoneVerification", "{ \"nomorTelepon\": \"string\" }"
            ),
            "endpoints", Map.of(
                "nikVerification", "POST /verification/nik",
                "emailVerification", "POST /verification/email", 
                "phoneVerification", "POST /verification/phone",
                "nikCheck", "POST /verification/nik-check",
                "ktpData", "GET /verification/ktp-data/{nik}",
                "stats", "GET /verification/stats"
            )
        ));
    }
}