package com.reg.regis.test.factory;

import com.reg.regis.model.Customer;
import com.reg.regis.model.Alamat;
import com.reg.regis.model.Wali;
import com.reg.regis.dto.request.RegistrationRequest;
import com.reg.regis.dto.request.NikVerificationRequest;
import com.reg.regis.dto.request.EmailVerificationRequest;
import com.reg.regis.dto.request.PhoneVerificationRequest;
import java.time.LocalDate;

public class TestDataFactory {
    
    /**
     * John Doe - Valid test case sesuai cURL
     */
    public static Customer createJohnDoe() {
        Customer customer = new Customer();
        customer.setNamaLengkap("John Doe");
        customer.setNik("3175031234567890");
        customer.setNamaIbuKandung("Mary Doe");
        customer.setNomorTelepon("081234567890");
        customer.setEmail("john.doe@example.com");
        customer.setPassword("JohnDoe123!");
        customer.setTipeAkun("BNI Taplus");
        customer.setJenisKartu("Gold");
        customer.setTempatLahir("Jakarta");
        customer.setTanggalLahir(LocalDate.of(1990, 5, 15));
        customer.setJenisKelamin("Laki-laki");
        customer.setAgama("Islam");
        customer.setStatusPernikahan("Belum Kawin");
        customer.setPekerjaan("Software Engineer");
        customer.setSumberPenghasilan("Gaji");
        customer.setRentangGaji("5-10 juta");
        customer.setTujuanPembuatanRekening("Tabungan");
        
        // Alamat
        Alamat alamat = new Alamat();
        alamat.setNamaAlamat("Jl. Sudirman No. 123, RT 001/RW 002");
        alamat.setProvinsi("DKI Jakarta");
        alamat.setKota("Jakarta Pusat");
        alamat.setKecamatan("Tanah Abang");
        alamat.setKelurahan("Bendungan Hilir");
        alamat.setKodePos("10210");
        customer.setAlamat(alamat);
        
        // Wali
        Wali wali = new Wali();
        wali.setJenisWali("Ayah");
        wali.setNamaLengkapWali("Robert Doe");
        wali.setPekerjaanWali("Pensiunan");
        wali.setAlamatWali("Jl. Sudirman No. 123, RT 001/RW 002");
        wali.setNomorTeleponWali("081298765432");
        customer.setWali(wali);
        
        return customer;
    }
    
    /**
     * Jane Smith - Valid test case sesuai cURL
     */
    public static Customer createJaneSmith() {
        Customer customer = new Customer();
        customer.setNamaLengkap("Jane Smith");
        customer.setNik("3175032345678901");
        customer.setNamaIbuKandung("Anna Smith");
        customer.setNomorTelepon("081234567891");
        customer.setEmail("jane.smith@example.com");
        customer.setPassword("JaneSmith123!");
        customer.setTipeAkun("BNI Taplus");
        customer.setJenisKartu("Gold");
        customer.setTempatLahir("Jakarta");
        customer.setTanggalLahir(LocalDate.of(1995, 8, 22));
        customer.setJenisKelamin("Perempuan");
        customer.setAgama("Kristen");
        customer.setStatusPernikahan("Kawin");
        customer.setPekerjaan("Marketing Manager");
        customer.setSumberPenghasilan("Gaji");
        customer.setRentangGaji("10-15 juta");
        customer.setTujuanPembuatanRekening("Investasi");
        return customer;
    }
    
    /**
     * NIK Verification Requests sesuai data dari cURL
     */
    public static NikVerificationRequest createJohnDoeNikRequest() {
        return new NikVerificationRequest("3175031234567890", "John Doe", LocalDate.of(1990, 5, 15));
    }
    
    public static NikVerificationRequest createJaneSmithNikRequest() {
        return new NikVerificationRequest("3175032345678901", "Jane Smith", LocalDate.of(1995, 8, 22));
    }
    
    public static NikVerificationRequest createInvalidNikRequest() {
        return new NikVerificationRequest("3175031234567890", "Wrong Name", LocalDate.of(1990, 5, 15));
    }
    
    /**
     * Email & Phone Verification Requests
     */
    public static EmailVerificationRequest createNewEmailRequest() {
        return new EmailVerificationRequest("newuser@example.com");
    }
    
    public static EmailVerificationRequest createExistingEmailRequest() {
        return new EmailVerificationRequest("existing@example.com");
    }
    
    public static PhoneVerificationRequest createNewPhoneRequest() {
        return new PhoneVerificationRequest("081999888777");
    }
    
    public static PhoneVerificationRequest createExistingPhoneRequest() {
        return new PhoneVerificationRequest("081234567890");
    }
}