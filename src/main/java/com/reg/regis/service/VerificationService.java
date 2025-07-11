package com.reg.regis.service;

import com.reg.regis.dto.*;
import com.reg.regis.model.Customer;
import com.reg.regis.model.KtpDukcapil;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.repository.KtpDukcapilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VerificationService {
    
    @Autowired
    private KtpDukcapilRepository ktpDukcapilRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Verifikasi NIK dengan nama lengkap
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyNik(NikVerificationRequest request) {
        try {
            // Cari data KTP berdasarkan NIK dan nama
            Optional<KtpDukcapil> ktpOpt = ktpDukcapilRepository.findByNikAndNama(
                request.getNik(), 
                request.getNamaLengkap()
            );
            
            if (ktpOpt.isPresent()) {
                KtpDukcapil ktpData = ktpOpt.get();
                KtpDataDto ktpDto = convertToDto(ktpData);
                
                return new VerificationResponse(
                    true, 
                    "Data NIK dan nama valid sesuai dengan data Dukcapil", 
                    ktpDto
                );
            } else {
                // Check apakah NIK ada tapi nama tidak cocok
                Optional<KtpDukcapil> nikExists = ktpDukcapilRepository.findByNik(request.getNik());
                if (nikExists.isPresent()) {
                    return new VerificationResponse(
                        false, 
                        "NIK terdaftar namun nama tidak sesuai dengan data Dukcapil"
                    );
                } else {
                    return new VerificationResponse(
                        false, 
                        "NIK tidak terdaftar di database Dukcapil"
                    );
                }
            }
            
        } catch (Exception e) {
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi NIK: " + e.getMessage()
            );
        }
    }
    
    /**
     * Verifikasi email (check apakah sudah terdaftar)
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyEmail(EmailVerificationRequest request) {
        try {
            boolean emailExists = customerRepository.existsByEmailIgnoreCase(request.getEmail());
            
            if (emailExists) {
                Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(request.getEmail());
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    Map<String, Object> emailData = new HashMap<>();
                    emailData.put("email", customer.getEmail());
                    emailData.put("namaLengkap", customer.getNamaLengkap());
                    emailData.put("emailVerified", customer.getEmailVerified());
                    emailData.put("registeredAt", customer.getCreatedAt());
                    
                    return new VerificationResponse(
                        false, 
                        "Email sudah terdaftar di sistem", 
                        emailData
                    );
                }
            }
            
            return new VerificationResponse(
                true, 
                "Email belum terdaftar dan dapat digunakan"
            );
            
        } catch (Exception e) {
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi email: " + e.getMessage()
            );
        }
    }
    
    /**
     * Verifikasi nomor telepon (check apakah sudah terdaftar)
     */
    @Transactional(readOnly = true)
    public VerificationResponse verifyPhone(PhoneVerificationRequest request) {
        try {
            boolean phoneExists = customerRepository.existsByNomorTelepon(request.getNomorTelepon());
            
            if (phoneExists) {
                // Cari customer berdasarkan nomor telepon
                Optional<Customer> customerOpt = customerRepository.findAll()
                    .stream()
                    .filter(c -> c.getNomorTelepon().equals(request.getNomorTelepon()))
                    .findFirst();
                
                if (customerOpt.isPresent()) {
                    Customer customer = customerOpt.get();
                    Map<String, Object> phoneData = new HashMap<>();
                    phoneData.put("nomorTelepon", customer.getNomorTelepon());
                    phoneData.put("namaLengkap", customer.getNamaLengkap());
                    phoneData.put("email", customer.getEmail());
                    phoneData.put("registeredAt", customer.getCreatedAt());
                    
                    return new VerificationResponse(
                        false, 
                        "Nomor telepon sudah terdaftar di sistem", 
                        phoneData
                    );
                }
            }
            
            return new VerificationResponse(
                true, 
                "Nomor telepon belum terdaftar dan dapat digunakan"
            );
            
        } catch (Exception e) {
            return new VerificationResponse(
                false, 
                "Terjadi kesalahan saat verifikasi nomor telepon: " + e.getMessage()
            );
        }
    }
    
    /**
     * Check apakah NIK terdaftar (tanpa nama)
     */
    @Transactional(readOnly = true)
    public boolean isNikRegistered(String nik) {
        return ktpDukcapilRepository.existsByNik(nik);
    }
    
    /**
     * Get data KTP berdasarkan NIK saja
     */
    @Transactional(readOnly = true)
    public Optional<KtpDukcapil> getKtpDataByNik(String nik) {
        return ktpDukcapilRepository.findByNik(nik);
    }
    
    /**
     * Convert KtpDukcapil entity ke DTO
     */
    private KtpDataDto convertToDto(KtpDukcapil ktpData) {
        KtpDataDto dto = new KtpDataDto();
        dto.setNik(ktpData.getNik());
        dto.setNamaLengkap(ktpData.getNamaLengkap());
        dto.setTempatLahir(ktpData.getTempatLahir());
        dto.setTanggalLahir(ktpData.getTanggalLahir().toString());
        dto.setJenisKelamin(ktpData.getJenisKelamin().getValue());
        dto.setAlamat(ktpData.getNamaAlamat());
        dto.setKecamatan(ktpData.getKecamatan());
        dto.setKelurahan(ktpData.getKelurahan());
        dto.setAgama(ktpData.getAgama().getValue());
        dto.setStatusPerkawinan(ktpData.getStatusPerkawinan());
        
        return dto;
    }
    
    /**
     * Get verification statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVerificationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Customer stats
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = customerRepository.countVerifiedCustomers();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
        
        // KTP stats
        long totalKtpRecords = ktpDukcapilRepository.count();
        
        stats.put("totalCustomers", totalCustomers);
        stats.put("verifiedCustomers", verifiedCustomers);
        stats.put("verificationRate", Math.round(verificationRate * 100.0) / 100.0);
        stats.put("totalKtpRecords", totalKtpRecords);
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
}