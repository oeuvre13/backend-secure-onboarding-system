package com.reg.regis.dto;

public class KtpDataDto {
    private String nik;
    private String namaLengkap;
    private String tempatLahir;
    private String tanggalLahir;
    private String jenisKelamin;
    private String alamat;
    private String kecamatan;
    private String kelurahan;
    private String agama;
    private String statusPerkawinan;
    
    // Constructors
    public KtpDataDto() {}
    
    // Getters and Setters
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getTempatLahir() { return tempatLahir; }
    public void setTempatLahir(String tempatLahir) { this.tempatLahir = tempatLahir; }
    
    public String getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(String tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    
    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    
    public String getKecamatan() { return kecamatan; }
    public void setKecamatan(String kecamatan) { this.kecamatan = kecamatan; }
    
    public String getKelurahan() { return kelurahan; }
    public void setKelurahan(String kelurahan) { this.kelurahan = kelurahan; }
    
    public String getAgama() { return agama; }
    public void setAgama(String agama) { this.agama = agama; }
    
    public String getStatusPerkawinan() { return statusPerkawinan; }
    public void setStatusPerkawinan(String statusPerkawinan) { this.statusPerkawinan = statusPerkawinan; }
}