package com.reg.regis.service;

import com.reg.regis.dto.RegistrationRequest;
import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.model.Wali;
import com.reg.regis.model.KtpDukcapil;
import com.reg.regis.repository.CustomerRepository;
import com.reg.regis.repository.KtpDukcapilRepository;
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
    private KtpDukcapilRepository ktpDukcapilRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional
    public Customer registerCustomer(RegistrationRequest request) {
        // 1. VALIDASI NIK HARUS ADA DI KTP DUKCAPIL
        Optional<KtpDukcapil> ktpOpt = ktpDukcapilRepository.findByNik(request.getNik());
        if (ktpOpt.isEmpty()) {
            throw new RuntimeException("NIK tidak terdaftar di database Dukcapil. Silakan gunakan NIK yang valid.");
        }
        
        KtpDukcapil ktpData = ktpOpt.get();
        
        // 2. VALIDASI NAMA HARUS SESUAI DENGAN KTP
        if (!ktpData.getNamaLengkap().equalsIgnoreCase(request.getNamaLengkap())) {
            throw new RuntimeException("Nama lengkap tidak sesuai dengan data KTP. " +
                "Data KTP: " + ktpData.getNamaLengkap() + 
                ", Input: " + request.getNamaLengkap());
        }
        
        // 3. VALIDASI EMAIL TIDAK BOLEH DUPLIKAT
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email " + request.getEmail() + " sudah terdaftar. Gunakan email lain.");
        }
        
        // 4. VALIDASI NOMOR TELEPON TIDAK BOLEH DUPLIKAT
        if (customerRepository.existsByNomorTelepon(request.getNomorTelepon())) {
            throw new RuntimeException("Nomor telepon " + request.getNomorTelepon() + " sudah terdaftar. Gunakan nomor lain.");
        }
        
        // 5. VALIDASI NIK BELUM PERNAH DIGUNAKAN UNTUK REGISTRASI
        if (customerRepository.existsByNik(request.getNik())) {
            throw new RuntimeException("NIK " + request.getNik() + " sudah pernah digunakan untuk registrasi.");
        }
        
        // 6. VALIDASI DATA TAMBAHAN SESUAI KTP (OPSIONAL TAPI DIREKOMENDASIKAN)
        validateAdditionalKtpData(request, ktpData);
        
        // 7. BUAT CUSTOMER BARU
        Customer customer = new Customer();
        customer.setNamaLengkap(ktpData.getNamaLengkap()); // Gunakan nama dari KTP
        customer.setNik(request.getNik());
        customer.setNamaIbuKandung(request.getNamaIbuKandung());
        customer.setNomorTelepon(request.getNomorTelepon());
        customer.setEmail(request.getEmail().toLowerCase());
        customer.setPassword(passwordEncoder.encode(request.getPassword()));
        customer.setTipeAkun(request.getTipeAkun());
        
        // Auto-fill dari data KTP
        customer.setTempatLahir(ktpData.getTempatLahir());
        customer.setTanggalLahir(ktpData.getTanggalLahir());
        customer.setJenisKelamin(ktpData.getJenisKelamin().getValue());
        customer.setAgama(ktpData.getAgama().getValue());
        
        customer.setStatusPernikahan(request.getStatusPernikahan());
        customer.setPekerjaan(request.getPekerjaan());
        customer.setSumberPenghasilan(request.getSumberPenghasilan());
        customer.setRentangGaji(request.getRentangGaji());
        customer.setTujuanPembuatanRekening(request.getTujuanPembuatanRekening());
        customer.setKodeRekening(request.getKodeRekening());
        customer.setEmailVerified(false);
        
        // Buat alamat
        Alamat alamat = new Alamat();
        alamat.setNamaAlamat(request.getAlamat().getNamaAlamat());
        alamat.setProvinsi(request.getAlamat().getProvinsi());
        alamat.setKota(request.getAlamat().getKota());
        alamat.setKecamatan(request.getAlamat().getKecamatan());
        alamat.setKelurahan(request.getAlamat().getKelurahan());
        alamat.setKodePos(request.getAlamat().getKodePos());
        
        // Buat wali
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
    
    /**
     * Validasi data tambahan sesuai KTP (warning saja, tidak error)
     */
    private void validateAdditionalKtpData(RegistrationRequest request, KtpDukcapil ktpData) {
        // Cek tanggal lahir
        if (!ktpData.getTanggalLahir().equals(request.getTanggalLahir())) {
            System.out.println("⚠️ Warning: Tanggal lahir tidak sesuai KTP. " +
                "KTP: " + ktpData.getTanggalLahir() + 
                ", Input: " + request.getTanggalLahir());
        }
        
        // Cek tempat lahir
        if (!ktpData.getTempatLahir().equalsIgnoreCase(request.getTempatLahir())) {
            System.out.println("⚠️ Warning: Tempat lahir tidak sesuai KTP. " +
                "KTP: " + ktpData.getTempatLahir() + 
                ", Input: " + request.getTempatLahir());
        }
        
        // Cek jenis kelamin
        if (!ktpData.getJenisKelamin().getValue().equalsIgnoreCase(request.getJenisKelamin())) {
            System.out.println("⚠️ Warning: Jenis kelamin tidak sesuai KTP. " +
                "KTP: " + ktpData.getJenisKelamin().getValue() + 
                ", Input: " + request.getJenisKelamin());
        }
    }
    
    /**
     * Get data KTP untuk preview sebelum registrasi
     */
    public Optional<KtpDukcapil> getKtpData(String nik) {
        return ktpDukcapilRepository.findByNik(nik);
    }
    
    /**
     * Validasi NIK dan nama sebelum registrasi
     */
    public boolean validateNikAndName(String nik, String namaLengkap) {
        Optional<KtpDukcapil> ktpOpt = ktpDukcapilRepository.findByNik(nik);
        if (ktpOpt.isEmpty()) {
            return false;
        }
        
        KtpDukcapil ktpData = ktpOpt.get();
        return ktpData.getNamaLengkap().equalsIgnoreCase(namaLengkap);
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
    
    public Optional<Customer> getCustomerByNik(String nik) {
        return customerRepository.findByNik(nik);
    }
    
    public boolean validateNikFormat(String nik) {
        if (nik == null || nik.length() != 16) {
            return false;
        }
        
        try {
            Long.parseLong(nik);
            String provinsi = nik.substring(0, 2);
            String kabupaten = nik.substring(2, 4);
            String kecamatan = nik.substring(4, 6);
            
            return !provinsi.equals("00") && !kabupaten.equals("00") && !kecamatan.equals("00");
        } catch (NumberFormatException e) {
            return false;
        }
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
        private final long totalKtpRecords;
        
        public RegistrationStats(long totalCustomers, long verifiedCustomers, double verificationRate, long totalKtpRecords) {
            this.totalCustomers = totalCustomers;
            this.verifiedCustomers = verifiedCustomers;
            this.verificationRate = verificationRate;
            this.totalKtpRecords = totalKtpRecords;
        }
        
        public long getTotalCustomers() { return totalCustomers; }
        public long getVerifiedCustomers() { return verifiedCustomers; }
        public double getVerificationRate() { return verificationRate; }
        public long getTotalKtpRecords() { return totalKtpRecords; }
    }    
    
    public RegistrationStats getRegistrationStats() {
        long totalCustomers = customerRepository.countTotalCustomers();
        long verifiedCustomers = customerRepository.countVerifiedCustomers();
        long totalKtpRecords = ktpDukcapilRepository.count();
        double verificationRate = totalCustomers > 0 ? 
            (double) verifiedCustomers / totalCustomers * 100 : 0;
            
        return new RegistrationStats(totalCustomers, verifiedCustomers, verificationRate, totalKtpRecords);
    }
}