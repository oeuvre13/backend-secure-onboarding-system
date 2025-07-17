#!/bin/bash

# ===== CUSTOMER REGISTRATION SERVICE SETUP SCRIPT =====
# File: setup.sh
# Description: Simple setup untuk Customer Registration Service
# Usage: chmod +x setup.sh && ./setup.sh

set -e  # Exit on any error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_step() {
    echo -e "${BLUE}📋 Step $1: $2${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_step "1" "Checking Prerequisites"
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -ge "21" ]; then
            print_success "Java $JAVA_VERSION found"
        else
            print_error "Java 21+ required, found Java $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java not found. Please install Java 21+"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        print_success "Maven found"
    else
        print_error "Maven not found. Please install Maven"
        exit 1
    fi
    
    # Check PostgreSQL
    if command -v psql &> /dev/null; then
        print_success "PostgreSQL found"
    else
        print_error "PostgreSQL not found. Please install PostgreSQL"
        exit 1
    fi
    
    echo ""
}

# Check existing project structure
check_project_structure() {
    print_step "2" "Checking Project Structure"
    
    if [ -d "src/main/java/com/reg/regis" ]; then
        print_success "Project structure already exists"
    else
        print_warning "Creating missing project structure..."
        mkdir -p src/main/java/com/reg/regis/{config,controller,dto,model,repository,service,security,util}
        mkdir -p src/main/resources
        mkdir -p src/test/java/com/reg/regis
        print_success "Project structure created"
    fi
    
    # Create missing directories
    mkdir -p sql
    mkdir -p scripts
    
    echo ""
}

# Create or update .env file
create_env_file() {
    print_step "3" "Managing Environment Configuration"
    
    if [ -f ".env" ]; then
        print_warning ".env file already exists, skipping creation"
        print_warning "Please ensure .env has these variables:"
        echo "  - DB_URL"
        echo "  - DB_USERNAME"  
        echo "  - DB_PASSWORD"
        echo "  - JWT_SECRET"
        echo "  - JWT_EXPIRATION"
        echo "  - SERVER_PORT"
    else
        cat > .env << 'EOF'
# ===== CUSTOMER REGISTRATION SERVICE ENVIRONMENT =====
DB_URL=jdbc:postgresql://localhost:5432/customer_registration
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=mySecretKey123456789mySecretKey123456789
JWT_EXPIRATION=86400000
SERVER_PORT=8080
EOF
        print_success ".env file created"
        print_warning "Please update DB_USERNAME and DB_PASSWORD in .env file"
    fi
    echo ""
}

# Create or check application.properties
create_application_properties() {
    print_step "4" "Managing Application Properties"
    
    if [ -f "src/main/resources/application.properties" ]; then
        print_warning "application.properties already exists, skipping creation"
        print_warning "Please ensure it has the required configuration from your requirements"
    else
        cat > src/main/resources/application.properties << 'EOF'
# ===== CUSTOMER REGISTRATION SERVICE CONFIGURATION =====
spring.application.name=Customer-Registration-Service
server.port=${SERVER_PORT}
server.servlet.context-path=/api

# Database Configuration - Customer Registration Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Security
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=${JWT_EXPIRATION}

# CORS Configuration
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173

# Rate Limiting
app.rateLimit.enabled=true
app.rateLimit.capacity=10
app.rateLimit.refillRate=2

# External Services - DUKCAPIL SERVICE
app.dukcapil.base-url=http://localhost:8081/api/dukcapil
app.dukcapil.verify-nik-endpoint=/verify-nik
app.dukcapil.check-nik-endpoint=/check-nik
app.dukcapil.timeout=10000

# Service Configuration
app.service.name=Customer Registration Service
app.service.version=1.0.0

# Logging Configuration
logging.level.com.reg.regis=INFO
logging.level.org.springframework.web=INFO
logging.level.org.springframework.security=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [CUSTOMER] %logger{36} - %msg%n

# Response Configuration
spring.jackson.serialization.indent-output=true
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.time-zone=Asia/Jakarta
EOF
        print_success "application.properties created"
    fi
    echo ""
}

# Create or check SQL scripts
create_sql_scripts() {
    print_step "5" "Managing SQL Scripts"
    
    # Check if database_setup script exists
    if [ -f "sql/database_setup_FIXED.sql" ] || [ -f "sql/database_setup.sql" ]; then
        print_warning "Database setup script already exists, skipping creation"
        print_success "Found existing database setup script"
    else
        cat > sql/database_setup.sql << 'EOF'
-- ===== DATABASE SETUP SCRIPT =====
-- File: database_setup.sql
-- Jalankan dengan: psql -U postgres -f sql/database_setup.sql

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
RETURNS TRIGGER AS $
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$ language 'plpgsql';

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

-- Create Customers table
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

-- Verify setup
SELECT 'Database setup completed successfully!' as status;
\c dukcapil_ktp;
SELECT 'Dukcapil KTP Records: ' || COUNT(*) as status FROM ktp_dukcapil;
\c customer_registration;
SELECT 'Customer Registration setup completed!' as status;
EOF
        print_success "Database setup script created"
    fi
    
    # Check if clean script exists
    if [ -f "sql/clean_db.sql" ]; then
        print_warning "Database clean script already exists, skipping creation"
    else
        cat > sql/clean_db.sql << 'EOF'
-- ===== CLEAN CUSTOMER REGISTRATION DATABASE =====
-- File: clean_customer_registration.sql
-- Jalankan dengan: psql postgres -f sql/clean_db.sql

-- Connect ke database customer_registration
\c customer_registration;

-- Tampilkan data sebelum dihapus (untuk konfirmasi)
SELECT 'Data sebelum dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

-- Hapus data customers terlebih dahulu (karena ada foreign key)
DELETE FROM customers;

-- Hapus data alamat
DELETE FROM alamat;

-- Hapus data wali
DELETE FROM wali;

-- Reset sequence untuk auto-increment ID
ALTER SEQUENCE customers_id_seq RESTART WITH 1;
ALTER SEQUENCE alamat_id_seq RESTART WITH 1;
ALTER SEQUENCE wali_id_seq RESTART WITH 1;

-- Verifikasi data sudah terhapus
SELECT 'Data setelah dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

SELECT '✅ Customer registration database berhasil dibersihkan!' as status;
EOF
        print_success "Database clean script created"
    fi
    
    echo ""
}
-- ===== DATABASE SETUP SCRIPT =====
-- File: database_setup.sql
-- Jalankan dengan: psql -U postgres -f sql/database_setup.sql

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

-- Create Customers table
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

-- Verify setup
SELECT 'Database setup completed successfully!' as status;
\c dukcapil_ktp;
SELECT 'Dukcapil KTP Records: ' || COUNT(*) as status FROM ktp_dukcapil;
\c customer_registration;
SELECT 'Customer Registration setup completed!' as status;
EOF
    
    # Clean database script
    cat > sql/clean_db.sql << 'EOF'
-- ===== CLEAN CUSTOMER REGISTRATION DATABASE =====
-- File: clean_customer_registration.sql
-- Jalankan dengan: psql postgres -f sql/clean_db.sql

-- Connect ke database customer_registration
\c customer_registration;

-- Tampilkan data sebelum dihapus (untuk konfirmasi)
SELECT 'Data sebelum dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

-- Hapus data customers terlebih dahulu (karena ada foreign key)
DELETE FROM customers;

-- Hapus data alamat
DELETE FROM alamat;

-- Hapus data wali
DELETE FROM wali;

-- Reset sequence untuk auto-increment ID
ALTER SEQUENCE customers_id_seq RESTART WITH 1;
ALTER SEQUENCE alamat_id_seq RESTART WITH 1;
ALTER SEQUENCE wali_id_seq RESTART WITH 1;

-- Verifikasi data sudah terhapus
SELECT 'Data setelah dihapus:' as status;
SELECT COUNT(*) as total_customers FROM customers;
SELECT COUNT(*) as total_alamat FROM alamat;
SELECT COUNT(*) as total_wali FROM wali;

SELECT '✅ Customer registration database berhasil dibersihkan!' as status;
EOF
    
    print_success "SQL scripts created"
    echo ""
}

# Create or check pom.xml
create_pom_xml() {
    print_step "6" "Managing Maven POM Configuration"
    
    if [ -f "pom.xml" ]; then
        print_warning "pom.xml already exists, skipping creation"
        print_success "Found existing pom.xml"
    else
        print_warning "pom.xml not found, creating basic pom.xml"
        print_warning "Please replace with your actual pom.xml with correct dependencies"
        
        cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.reg</groupId>
	<artifactId>Registration-Absolute</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Registration-Absolute</name>
	<description>Develop a secure customer registration system</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
</project>
EOF
        print_success "Basic pom.xml created"
    fi
    echo ""
}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.3</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.reg</groupId>
	<artifactId>Registration-Absolute</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Registration-Absolute</name>
	<description>Develop a secure customer registration system</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>

		<!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
		<!-- WebFlux for WebClient (Modern HTTP Client) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
        </dependency>
        
        <!-- Rate Limiting -->
        <dependency>
            <groupId>com.github.vladimir-bukhtoyarov</groupId>
            <artifactId>bucket4j-core</artifactId>
            <version>7.6.0</version>
        </dependency>

		<!-- Test Dependencies -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>junit-jupiter</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>org.testcontainers</groupId>
			<artifactId>postgresql</artifactId>
			<scope>test</scope>
		</dependency>
		<!-- Dotenv Dependency -->
		<dependency>
			<groupId>io.github.cdimascio</groupId>
			<artifactId>dotenv-java</artifactId>
			<version>2.3.2</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</path>
					</annotationProcessorPaths>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
			<!-- JaCoCo Coverage Plugin -->
			<plugin>
				<groupId>org.jacoco</groupId>
				<artifactId>jacoco-maven-plugin</artifactId>
				<version>0.8.11</version>
				<executions>
					<execution>
						<goals>
							<goal>prepare-agent</goal>
						</goals>
					</execution>
					<execution>
						<id>report</id>
						<phase>test</phase>
						<goals>
							<goal>report</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			
			<!-- Surefire Plugin for Test Reports -->
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>3.0.0-M9</version>
				<configuration>
					<includes>
						<include>**/*Test.java</include>
						<include>**/*Tests.java</include>
					</includes>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
EOF
    
    print_success "pom.xml created"
    echo ""
}

# Create or check utility scripts
create_utility_scripts() {
    print_step "7" "Managing Basic Scripts"
    
    # Create run script if not exists
    if [ ! -f "scripts/run.sh" ]; then
        cat > scripts/run.sh << 'EOF'
#!/bin/bash
echo "🚀 Starting Customer Registration Service..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

mvn spring-boot:run
EOF
        chmod +x scripts/run.sh
        print_success "run.sh created"
    else
        print_warning "scripts/run.sh already exists"
    fi
    
    # Create setup database script if not exists
    if [ ! -f "scripts/setup-db.sh" ]; then
        cat > scripts/setup-db.sh << 'EOF'
#!/bin/bash
echo "🗄️  Setting up databases..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

DB_USER=$(echo $DB_USERNAME)
DB_PASS=$(echo $DB_PASSWORD)

if [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo "❌ Please set DB_USERNAME and DB_PASSWORD in .env file"
    exit 1
fi

# Use the existing database setup script
if [ -f "sql/database_setup_FIXED.sql" ]; then
    PGPASSWORD=$DB_PASS psql -h localhost -U $DB_USER postgres -f sql/database_setup_FIXED.sql
elif [ -f "sql/database_setup.sql" ]; then
    PGPASSWORD=$DB_PASS psql -h localhost -U $DB_USER postgres -f sql/database_setup.sql
else
    echo "❌ No database setup script found"
    exit 1
fi

echo "✅ Database setup completed"
EOF
        chmod +x scripts/setup-db.sh
        print_success "setup-db.sh created"
    else
        print_warning "scripts/setup-db.sh already exists"
    fi
    
    # Create clean database script if not exists
    if [ ! -f "scripts/clean-db.sh" ]; then
        cat > scripts/clean-db.sh << 'EOF'
#!/bin/bash
echo "🧹 Cleaning customer registration database..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

DB_USER=$(echo $DB_USERNAME)
DB_PASS=$(echo $DB_PASSWORD)

if [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo "❌ Please set DB_USERNAME and DB_PASSWORD in .env file"
    exit 1
fi

PGPASSWORD=$DB_PASS psql -h localhost -U $DB_USER postgres -f sql/clean_db.sql
echo "✅ Database cleaned"
EOF
        chmod +x scripts/clean-db.sh
        print_success "clean-db.sh created"
    else
        print_warning "scripts/clean-db.sh already exists"
    fi
    
    echo ""
}
#!/bin/bash
echo "🚀 Starting Customer Registration Service..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

mvn spring-boot:run
EOF
    
    # Create setup database script
    cat > scripts/setup-db.sh << 'EOF'
#!/bin/bash
echo "🗄️  Setting up databases..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

DB_USER=$(echo $DB_USERNAME)
DB_PASS=$(echo $DB_PASSWORD)

if [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo "❌ Please set DB_USERNAME and DB_PASSWORD in .env file"
    exit 1
fi

PGPASSWORD=$DB_PASS psql -h localhost -U $DB_USER postgres -f sql/database_setup.sql
echo "✅ Database setup completed"
EOF
    
    # Create clean database script
    cat > scripts/clean-db.sh << 'EOF'
#!/bin/bash
echo "🧹 Cleaning customer registration database..."

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '#' | xargs)
fi

DB_USER=$(echo $DB_USERNAME)
DB_PASS=$(echo $DB_PASSWORD)

if [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo "❌ Please set DB_USERNAME and DB_PASSWORD in .env file"
    exit 1
fi

PGPASSWORD=$DB_PASS psql -h localhost -U $DB_USER postgres -f sql/clean_db.sql
echo "✅ Database cleaned"
EOF
    
    # Make scripts executable
    chmod +x scripts/*.sh
    
    print_success "Basic scripts created"
    echo ""
}

# Create or check gitignore
create_gitignore() {
    print_step "8" "Managing Git Configuration"
    
    if [ -f ".gitignore" ]; then
        print_warning ".gitignore already exists, skipping creation"
    else
        cat > .gitignore << 'EOF'
# Compiled class file
*.class

# Log file
*.log

# Package Files
*.jar
*.war
*.nar
*.ear

# Maven
target/
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
.project
.classpath
.settings/

# Environment
.env
.env.local

# OS
.DS_Store
Thumbs.db

# Application specific
cookies*.txt
refresh_cookies.txt
EOF
        print_success "Git configuration created"
    fi
    echo ""
}
# Compiled class file
*.class

# Log file
*.log

# Package Files
*.jar
*.war
*.nar
*.ear

# Maven
target/
.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iml
.vscode/
.project
.classpath
.settings/

# Environment
.env
.env.local

# OS
.DS_Store
Thumbs.db

# Application specific
cookies*.txt
EOF
    
    print_success "Git configuration created"
    echo ""
}

# Create or update basic README
create_readme() {
    print_step "9" "Managing Documentation"
    
    if [ -f "README.md" ]; then
        print_warning "README.md already exists, skipping creation"
        print_success "Found existing README.md"
    else
        cat > README.md << 'EOF'
# 🏦 Customer Registration Service

Secure customer registration system dengan integrasi Dukcapil untuk verifikasi KTP.

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- PostgreSQL 12+

### Setup
```bash
# 1. Run setup script
chmod +x setup.sh && ./setup.sh

# 2. Update database credentials di .env file
nano .env

# 3. Setup database
./scripts/setup-db.sh

# 4. Run application
./scripts/run.sh
```

## 🔗 API Endpoints

- `POST /api/registration/register` - Customer registration
- `POST /api/auth/login` - Customer login
- `POST /api/verification/nik` - NIK verification

### Health Checks
- `GET /api/registration/health`
- `GET /api/auth/health`
- `GET /api/verification/health`

## 📋 Available Scripts

- `./scripts/run.sh` - Run application
- `./scripts/setup-db.sh` - Setup databases
- `./scripts/clean-db.sh` - Clean customer database

## 🧪 Quick Test

```bash
# Test health
curl http://localhost:8080/api/registration/health

# Test NIK verification
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"3175031234567890","namaLengkap":"John Doe","tanggalLahir":"1990-05-15"}'
```

## 🔧 Configuration

Update `.env` file dengan database credentials:
```
DB_USERNAME=your_username
DB_PASSWORD=your_password
```
EOF
        print_success "Documentation created"
    fi
    echo ""
}
# 🏦 Customer Registration Service

Secure customer registration system dengan integrasi Dukcapil untuk verifikasi KTP.

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- PostgreSQL 12+

### Setup
```bash
# 1. Run setup script
chmod +x setup.sh && ./setup.sh

# 2. Update database credentials di .env file
nano .env

# 3. Setup database
./scripts/setup-db.sh

# 4. Run application
./scripts/run.sh
```

## 🔗 API Endpoints

- `POST /api/registration/register` - Customer registration
- `POST /api/auth/login` - Customer login
- `POST /api/verification/nik` - NIK verification

### Health Checks
- `GET /api/registration/health`
- `GET /api/auth/health`
- `GET /api/verification/health`

## 📋 Available Scripts

- `./scripts/run.sh` - Run application
- `./scripts/setup-db.sh` - Setup databases
- `./scripts/clean-db.sh` - Clean customer database

## 🧪 Quick Test

```bash
# Test health
curl http://localhost:8080/api/registration/health

# Test NIK verification
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"3175031234567890","namaLengkap":"John Doe","tanggalLahir":"1990-05-15"}'
```

## 🔧 Configuration

Update `.env` file dengan database credentials:
```
DB_USERNAME=your_username
DB_PASSWORD=your_password
```
EOF
    
    print_success "Documentation created"
    echo ""
}

# Show final instructions
show_final_instructions() {
    print_step "10" "Setup Complete"
    
    echo -e "${GREEN}🎉 Customer Registration Service setup completed!${NC}"
    echo ""
    echo -e "${YELLOW}📋 Next Steps:${NC}"
    echo -e "  1. ${BLUE}Update database credentials in .env:${NC}"
    echo -e "     nano .env"
    echo ""
    echo -e "  2. ${BLUE}Setup database:${NC}"
    echo -e "     ./scripts/setup-db.sh"
    echo ""
    echo -e "  3. ${BLUE}Run application:${NC}"
    echo -e "     ./scripts/run.sh"
    echo ""
    echo -e "${YELLOW}🔗 Test endpoints:${NC}"
    echo -e "  ${BLUE}http://localhost:8080/api/registration/health${NC}"
    echo -e "  ${BLUE}http://localhost:8080/api/auth/health${NC}"
    echo -e "  ${BLUE}http://localhost:8080/api/verification/health${NC}"
    echo ""
    echo -e "${GREEN}✅ Happy coding! 🚀${NC}"
}

# Main execution
main() {
    echo -e "${BLUE}🔐 CUSTOMER REGISTRATION SERVICE SETUP${NC}"
    echo -e "${BLUE}=====================================${NC}"
    echo ""
    
    check_prerequisites
    check_project_structure
    create_env_file
    create_application_properties
    create_sql_scripts
    create_pom_xml
    create_utility_scripts
    create_gitignore
    create_readme
    show_final_instructions
}

# Run main function
main "$@"