package com.reg.regis.service;

import com.reg.regis.dto.RegistrationRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.model.Wali;
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
    
    @Transactional
    public Customer registerCustomer(RegistrationRequest request) {
        // Validasi duplikasi
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar");
        }
        
        if (customerRepository.existsByNomorTelepon(request.getNomorTelepon())) {
            throw new RuntimeException("Nomor telepon sudah terdaftar");
        }
        
        // Buat customer baru
        Customer customer = new Customer();
        customer.setNamaLengkap(request.getNamaLengkap());
        customer.setNamaIbuKandung(request.getNamaIbuKandung());
        customer.setNomorTelepon(request.getNomorTelepon());
        customer.setEmail(request.getEmail().toLowerCase());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setTipeAkun(request.getTipeAkun());
        customer.setTempatLahir(request.getTempatLahir());
        customer.setTanggalLahir(request.getTanggalLahir());
        customer.setJenisKelamin(request.getJenisKelamin());
        customer.setAgama(request.getAgama());
        customer.setStatusPernikahan(request.getStatusPernikahan());
        customer.setPekerjaan(request.getPekerjaan());
        customer.setSumberPenghasilan(request.getSumberPenghasilan());
        customer.setRentangGaji(request.getRentangGaji());
        customer.setTujuanPembuatanRekening(request.getTujuanPembuatanRekening());
        customer.setKodeRekening(request.getKodeRekening());
        customer.setEmailVerified(false);
        
        // Buat alamat dari nested object
        Alamat alamat = new Alamat();
        alamat.setNamaAlamat(request.getAlamat().getNamaAlamat());
        alamat.setProvinsi(request.getAlamat().getProvinsi());
        alamat.setKota(request.getAlamat().getKota());
        alamat.setKecamatan(request.getAlamat().getKecamatan());
        alamat.setKelurahan(request.getAlamat().getKelurahan());
        alamat.setKodePos(request.getAlamat().getKodePos());
        
        // Buat wali dari nested object
        Wali wali = new Wali();
        wali.setJenisWali(request.getWali().getJenisWali());
        wali.setNamaLengkapWali(request.getWali().getNamaLengkapWali());
        wali.setPekerjaanWali(request.getWali().getPekerjaanWali());
        wali.setAlamatWali(request.getWali().getAlamatWali());
        wali.setNomorTeleponWali(request.getWali().getNomorTeleponWali());
        
        // Set relasi
        customer.setAlamat(alamat);
        customer.setWali(wali);
        
        return customerRepository.save(customer);
    }
    
    public String authenticateCustomer(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Email atau password salah");
        }
        
        Customer customer = customerOpt.get();
        
        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new RuntimeException("Email atau password salah");
        }
        
        return jwtUtil.generateToken(customer.getEmail());
    }
    
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmailIgnoreCase(email);
    }
    
    @Transactional
    public void verifyEmail(String email) {
        Optional<Customer> customerOpt = customerRepository.findByEmailIgnoreCase(email);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setEmailVerified(true);
            customerRepository.save(customer);
        }
    }
    
    public String checkPasswordStrength(String password) {
        int score = 0;
        
        if (password.length() >= 8) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[@$!%*?&].*")) score++;
        
        return switch (score) {
            case 0, 1, 2 -> "lemah";
            case 3, 4 -> "sedang";
            case 5 -> "kuat";
            default -> "lemah";
        };
    }
    
    public String getEmailFromToken(String token) {
        try {
            return jwtUtil.getEmailFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String generateTokenForEmail(String email) {
        return jwtUtil.generateToken(email);
    }
    
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
    
    public RegistrationStats getRegistrationStats() {
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = customerRepository.countVerifiedCustomers();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
            
        return new RegistrationStats(totalCustomers, verifiedCustomers, verificationRate);
    }
}