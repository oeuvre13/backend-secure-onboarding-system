-- ===== DATABASE SETUP SCRIPT =====
-- File: database_setup.sql
-- Jalankan dengan: psql -U postgres -f database_setup.sql

-- 1. Create Database untuk Dukcapil
CREATE DATABASE dukcapil_ktp;

-- 2. Create Database untuk Customer Registration
CREATE DATABASE customer_registration;

-- 3. Setup Dukcapil Database
\c dukcapil_ktp;

-- Drop existing table jika ada
DROP TABLE IF EXISTS ktp_dukcapil CASCADE;

-- Create KTP Dukcapil table
CREATE TABLE ktp_dukcapil (
    id BIGSERIAL PRIMARY KEY,
    nik VARCHAR(20) NOT NULL UNIQUE,
    nama_lengkap VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(50) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(255) NOT NULL CHECK (jenis_kelamin IN ('LAKI_LAKI', 'PEREMPUAN')),
    nama_alamat TEXT NOT NULL,
    kecamatan VARCHAR(50) NOT NULL,
    kelurahan VARCHAR(50) NOT NULL,
    agama VARCHAR(255) NOT NULL CHECK (agama IN ('ISLAM', 'KRISTEN', 'BUDDHA', 'HINDU', 'KONGHUCU', 'LAINNYA')),
    status_perkawinan VARCHAR(20) NOT NULL,
    kewarganegaraan VARCHAR(20) DEFAULT 'WNI' NOT NULL,
    berlaku_hingga VARCHAR(20) DEFAULT 'SEUMUR HIDUP' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_ktp_nik ON ktp_dukcapil(nik);
CREATE INDEX idx_ktp_nama ON ktp_dukcapil(LOWER(nama_lengkap));
CREATE INDEX idx_ktp_nik_nama ON ktp_dukcapil(nik, LOWER(nama_lengkap));

-- Create trigger untuk updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_ktp_updated_at
    BEFORE UPDATE ON ktp_dukcapil
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insert sample data
INSERT INTO ktp_dukcapil (
    nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_alamat, kecamatan, kelurahan, agama, status_perkawinan
) VALUES 
('3175031234567890', 'John Doe', 'Jakarta', '1990-05-15', 'LAKI_LAKI',
 'Jl. Sudirman No. 123, RT 001/RW 002', 'Tanah Abang', 'Bendungan Hilir', 'ISLAM', 'BELUM KAWIN'),
('3175032345678901', 'Jane Smith', 'Jakarta', '1995-08-22', 'PEREMPUAN', 
 'Jl. Gatot Subroto No. 456, RT 003/RW 004', 'Setiabudi', 'Kuningan Timur', 'KRISTEN', 'KAWIN'),
('3175033456789012', 'Ahmad Rahman', 'Bogor', '1985-12-10', 'LAKI_LAKI',
 'Jl. Thamrin No. 789, RT 005/RW 006', 'Menteng', 'Gondangdia', 'ISLAM', 'KAWIN'),
('3175034567890123', 'Siti Nurhaliza', 'Depok', '1992-03-18', 'PEREMPUAN',
 'Jl. HR Rasuna Said No. 321, RT 007/RW 008', 'Setiabudi', 'Setiabudi', 'ISLAM', 'BELUM KAWIN'),
('3175035678901234', 'Budi Santoso', 'Jakarta', '1988-11-25', 'LAKI_LAKI',
 'Jl. Kemang Raya No. 654, RT 009/RW 010', 'Mampang Prapatan', 'Kemang', 'BUDDHA', 'KAWIN'),
('1234567890123456', 'Test User One', 'Jakarta', '1995-01-01', 'LAKI_LAKI',
 'Jl. Test No. 123', 'Test Kecamatan', 'Test Kelurahan', 'ISLAM', 'BELUM KAWIN'),
('1234567890123457', 'Test User Two', 'Bandung', '1992-02-02', 'PEREMPUAN',
 'Jl. Test No. 456', 'Test Kecamatan', 'Test Kelurahan', 'KRISTEN', 'KAWIN');

-- 4. Setup Customer Registration Database
\c customer_registration;

-- Drop existing tables
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS alamat CASCADE;
DROP TABLE IF EXISTS wali CASCADE;

-- Create Alamat table
CREATE TABLE alamat (
    id BIGSERIAL PRIMARY KEY,
    nama_alamat VARCHAR(255) NOT NULL,
    provinsi VARCHAR(100) NOT NULL,
    kota VARCHAR(100) NOT NULL,
    kecamatan VARCHAR(100) NOT NULL,
    kelurahan VARCHAR(100) NOT NULL,
    kode_pos VARCHAR(10) NOT NULL
);

-- Create Wali table
CREATE TABLE wali (
    id BIGSERIAL PRIMARY KEY,
    jenis_wali VARCHAR(50) NOT NULL,
    nama_lengkap_wali VARCHAR(255) NOT NULL,
    pekerjaan_wali VARCHAR(100) NOT NULL,
    alamat_wali VARCHAR(500) NOT NULL,
    nomor_telepon_wali VARCHAR(15) NOT NULL
);

-- Create Customers table (TANPA NIK karena akan diverifikasi via API)
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    nama_lengkap VARCHAR(255) NOT NULL,
    nik VARCHAR(16) NOT NULL UNIQUE,
    nama_ibu_kandung VARCHAR(255) NOT NULL,
    nomor_telepon VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tipe_akun VARCHAR(100) NOT NULL,
    tempat_lahir VARCHAR(100) NOT NULL,
    tanggal_lahir DATE NOT NULL,
    jenis_kelamin VARCHAR(20) NOT NULL,
    agama VARCHAR(50) NOT NULL,
    status_pernikahan VARCHAR(50) NOT NULL,
    pekerjaan VARCHAR(100) NOT NULL,
    sumber_penghasilan VARCHAR(100) NOT NULL,
    rentang_gaji VARCHAR(50) NOT NULL,
    tujuan_pembuatan_rekening VARCHAR(255) NOT NULL,
    kode_rekening INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE NOT NULL,
    alamat_id BIGINT,
    wali_id BIGINT,
    
    -- Foreign Keys
    CONSTRAINT fk_customer_alamat FOREIGN KEY (alamat_id) REFERENCES alamat(id),
    CONSTRAINT fk_customer_wali FOREIGN KEY (wali_id) REFERENCES wali(id),
    
    -- Check constraints
    CONSTRAINT chk_jenis_kelamin CHECK (jenis_kelamin IN ('Laki-laki', 'Perempuan'))
);

-- Create indexes
CREATE INDEX idx_customers_email ON customers(LOWER(email));
CREATE INDEX idx_customers_phone ON customers(nomor_telepon);
CREATE INDEX idx_customers_nik ON customers(nik);
CREATE INDEX idx_customers_verified ON customers(email_verified);

-- Create trigger untuk updated_at customers
CREATE TRIGGER trigger_customers_updated_at
    BEFORE UPDATE ON customers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Sample data untuk testing
INSERT INTO alamat (nama_alamat, provinsi, kota, kecamatan, kelurahan, kode_pos) 
VALUES ('Jl. Melati No. 10', 'DKI Jakarta', 'Jakarta Selatan', 'Kebayoran Baru', 'Melawai', '12160');

INSERT INTO wali (jenis_wali, nama_lengkap_wali, pekerjaan_wali, alamat_wali, nomor_telepon_wali)
VALUES ('Ayah', 'Budi Hartono', 'Pensiunan', 'Jl. Melati No. 10', '081298765432');

-- Verify setup
SELECT 'Database setup completed successfully!' as status;
\c dukcapil_ktp;
SELECT 'Dukcapil KTP Records: ' || COUNT(*) as status FROM ktp_dukcapil;
\c customer_registration;
SELECT 'Customer Registration setup completed!' as status;