// KtpDukcapil.java - Model untuk table ktp_dukcapil
package com.reg.regis.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ktp_dukcapil")
public class KtpDukcapil {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nik", nullable = false, unique = true, length = 20)
    private String nik;
    
    @Column(name = "nama_lengkap", nullable = false, length = 100)
    private String namaLengkap;
    
    @Column(name = "tempat_lahir", nullable = false, length = 50)
    private String tempatLahir;
    
    @Column(name = "tanggal_lahir", nullable = false)
    private LocalDate tanggalLahir;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_kelamin", nullable = false)
    private JenisKelamin jenisKelamin;
    
    @Column(name = "nama_alamat", nullable = false, columnDefinition = "TEXT")
    private String namaAlamat;
    
    @Column(name = "kecamatan", nullable = false, length = 50)
    private String kecamatan;
    
    @Column(name = "kelurahan", nullable = false, length = 50)
    private String kelurahan;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "agama", nullable = false)
    private Agama agama;
    
    @Column(name = "status_perkawinan", nullable = false, length = 20)
    private String statusPerkawinan;
    
    @Column(name = "kewarganegaraan", nullable = false, length = 20)
    private String kewarganegaraan = "WNI";
    
    @Column(name = "berlaku_hingga", nullable = false, length = 20)
    private String berlakuHingga = "SEUMUR HIDUP";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum JenisKelamin {
        LAKI_LAKI("Laki-laki"),
        PEREMPUAN("Perempuan");
        
        private String value;
        
        JenisKelamin(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    public enum Agama {
        ISLAM("Islam"),
        KRISTEN("Kristen"),
        BUDDHA("Buddha"),
        HINDU("Hindu"),
        KONGHUCU("Konghucu"),
        LAINNYA("Lainnya");
        
        private String value;
        
        Agama(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public KtpDukcapil() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    public String getTempatLahir() { return tempatLahir; }
    public void setTempatLahir(String tempatLahir) { this.tempatLahir = tempatLahir; }
    
    public LocalDate getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(LocalDate tanggalLahir) { this.tanggalLahir = tanggalLahir; }
    
    public JenisKelamin getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(JenisKelamin jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    
    public String getNamaAlamat() { return namaAlamat; }
    public void setNamaAlamat(String namaAlamat) { this.namaAlamat = namaAlamat; }
    
    public String getKecamatan() { return kecamatan; }
    public void setKecamatan(String kecamatan) { this.kecamatan = kecamatan; }
    
    public String getKelurahan() { return kelurahan; }
    public void setKelurahan(String kelurahan) { this.kelurahan = kelurahan; }
    
    public Agama getAgama() { return agama; }
    public void setAgama(Agama agama) { this.agama = agama; }
    
    public String getStatusPerkawinan() { return statusPerkawinan; }
    public void setStatusPerkawinan(String statusPerkawinan) { this.statusPerkawinan = statusPerkawinan; }
    
    public String getKewarganegaraan() { return kewarganegaraan; }
    public void setKewarganegaraan(String kewarganegaraan) { this.kewarganegaraan = kewarganegaraan; }
    
    public String getBerlakuHingga() { return berlakuHingga; }
    public void setBerlakuHingga(String berlakuHingga) { this.berlakuHingga = berlakuHingga; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}