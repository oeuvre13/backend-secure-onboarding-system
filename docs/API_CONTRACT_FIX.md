# 📄 API Contract - Customer Registration Service

## Service Information
- **Service Name:** Customer Registration Service dengan Dukcapil Integration
- **Version:** 1.0.0
- **Base URL:** `http://localhost:8080/api`
- **Protocol:** HTTP/HTTPS
- **Content-Type:** `application/json`
- **Authentication:** Cookie-based (HTTP-only cookies)

## 🎯 Service Architecture
3 Controller dengan endpoint yang berbeda:
1. **`/api/registration`** - Registration Management
2. **`/api/auth`** - Authentication & Login  
3. **`/api/verification`** - NIK & Data Verification

---

# 📋 API Endpoints

## 1. REGISTRATION MANAGEMENT (`/api/registration`) 📝

### 🏥 **GET** `/api/auth/health`
Health check untuk registration service

#### Response (200)
```json
{
  "status": "OK",
  "service": "Customer Registration Service dengan Dukcapil Integration",
  "timestamp": 1721120400000,
  "dukcapilService": {
    "url": "http://localhost:8081",
    "available": true
  },
  "statistics": {
    "totalCustomers": 150,
    "verifiedCustomers": 135,
    "verificationRate": "90%"
  },
  "endpoints": {
    "register": "POST /auth/register",
    "checkPassword": "POST /auth/check-password",
    "validateNik": "POST /auth/validate-nik",
    "verifyEmail": "POST /auth/verify-email",
    "stats": "GET /auth/stats",
    "health": "GET /auth/health",
    "profile": "GET /auth/profile"
  }
}
```

---

### 📝 **POST** `/api/auth/register` ⭐ **MAIN ENDPOINT**
Registrasi customer baru dengan validasi Dukcapil + Auto Login

#### Request Body
```json
{
  "namaLengkap": "John Doe",
  "nik": "3175031234567890",
  "namaIbuKandung": "Mary Doe",
  "nomorTelepon": "081234567890",
  "email": "john.doe@example.com",
  "password": "JohnDoe123!",
  "tipeAkun": "BNI Taplus",
  "tempatLahir": "Jakarta",
  "tanggalLahir": "1990-05-15",
  "jenisKelamin": "Laki-laki",
  "agama": "Islam",
  "statusPernikahan": "Belum Kawin",
  "pekerjaan": "Software Engineer",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": "5-10 juta",
  "tujuanPembuatanRekening": "Tabungan",
  "kodeRekening": 1001,
  "alamat": {
    "namaAlamat": "Jl. Sudirman No. 123",
    "provinsi": "DKI Jakarta",
    "kota": "Jakarta Pusat",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "kodePos": "10210"
  },
  "wali": {
    "jenisWali": "Ayah",
    "namaLengkapWali": "Robert Doe",
    "pekerjaanWali": "Pensiunan",
    "alamatWali": "Jl. Sudirman No. 123",
    "nomorTeleponWali": "081298765432"
  }
}
```

#### Success Response (200) + Set-Cookie
```json
{
  "success": true,
  "message": "Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.",
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

#### Validation Error (400)
```json
{
  "success": false,
  "error": "Email sudah terdaftar dalam sistem",
  "type": "validation_error"
}
```

**Set-Cookie Header:**
```
Set-Cookie: authToken=<jwt-token>; HttpOnly; Path=/; Max-Age=86400; Domain=localhost
```

---

### 🔑 **POST** `/api/auth/check-password`
Check password strength

#### Request Body
```json
{
  "password": "Password123!"
}
```

#### Response (200)
```json
{
  "strength": "STRONG"
}
```

---

### ✅ **POST** `/api/auth/validate-nik`
Validasi format NIK

#### Request Body
```json
{
  "nik": "3175031234567890"
}
```

#### Response (200)
```json
{
  "valid": true,
  "exists": false,
  "message": "NIK valid"
}
```

---

### 📧 **POST** `/api/auth/verify-email`
Verify customer email

#### Request Body
```json
{
  "email": "john.doe@example.com"
}
```

#### Response (200)
```json
{
  "message": "Email berhasil diverifikasi"
}
```

---

### 📊 **GET** `/api/auth/stats`
Registration statistics

#### Response (200)
```json
{
  "totalCustomers": 150,
  "verifiedCustomers": 135,
  "verificationRate": 90.0,
  "dukcapilServiceUrl": "http://localhost:8081",
  "dukcapilServiceAvailable": true
}
```

---

### 👤 **GET** `/api/auth/profile`
Get customer profile (requires authentication)

#### Headers
```
Cookie: authToken=<jwt-token>
```

#### Success Response (200)
```json
{
  "profile": {
    "id": 1,
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

---

## 2. AUTHENTICATION (`/api/auth`) 🔐

### 🔐 **POST** `/api/auth/login` ⭐ **MAIN ENDPOINT**
Customer login dengan email dan password

#### Request Body
```json
{
  "email": "john.doe@example.com",
  "password": "JohnDoe123!"
}
```

#### Success Response (200) + Set-Cookie
```json
{
  "message": "Login berhasil",
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Engineer",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

#### Failed Response (400)
```json
{
  "error": "Email atau password tidak valid"
}
```

---

### 👤 **GET** `/api/auth/me`
Get current authenticated user

#### Headers
```
Cookie: authToken=<jwt-token>
```

#### Success Response (200)
```json
{
  "authenticated": true,
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "email": "john.doe@example.com",
    "nomorTelepon": "081234567890",
    "tipeAkun": "BNI Taplus",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Tanah Abang",
      "kelurahan": "Bendungan Hilir",
      "kodePos": "10210"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Robert Doe",
      "pekerjaanWali": "Pensiunan",
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

---

### 🚪 **POST** `/api/auth/logout`
Logout user (clear cookie)

#### Response (200) + Clear-Cookie
```json
{
  "message": "Logout berhasil"
}
```

---

### 🔄 **POST** `/api/auth/refresh-token`
Refresh JWT token

#### Headers
```
Cookie: authToken=<jwt-token>
```

#### Success Response (200) + Set-Cookie
```json
{
  "message": "Token berhasil diperbarui"
}
```

---

### ✅ **GET** `/api/auth/check-auth`
Check authentication status

#### Headers
```
Cookie: authToken=<jwt-token>
```

#### Authenticated Response (200)
```json
{
  "authenticated": true,
  "email": "john.doe@example.com"
}
```

#### Not Authenticated Response (200)
```json
{
  "authenticated": false
}
```

---

## 3. VERIFICATION SERVICE (`/api/verification`) 🔍

### 🏥 **GET** `/api/verification/health`
Health check verification service

#### Response (200)
```json
{
  "status": "OK",
  "service": "Verification Service (Enhanced with tanggalLahir)",
  "timestamp": 1721120400000,
  "endpoints": {
    "nikVerification": "POST /verification/nik (requires: nik, namaLengkap, tanggalLahir)",
    "emailVerification": "POST /verification/email",
    "phoneVerification": "POST /verification/phone",
    "nikCheck": "POST /verification/nik-check",
    "stats": "GET /verification/stats"
  }
}
```

---

### 🔍 **POST** `/api/verification/nik` ⭐ **MAIN ENDPOINT**
Verifikasi NIK dengan nama lengkap dan tanggal lahir via Dukcapil Service

#### Request Body
```json
{
  "nik": "3175031234567890",
  "namaLengkap": "John Doe",
  "tanggalLahir": "1990-05-15"
}
```

#### Success Response (200)
```json
{
  "valid": true,
  "message": "NIK dan nama cocok",
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "LAKI_LAKI",
    "alamat": "Jl. Sudirman No. 123, RT 001/RW 002",
    "kecamatan": "Tanah Abang",
    "kelurahan": "Bendungan Hilir",
    "agama": "ISLAM",
    "statusPerkawinan": "BELUM KAWIN"
  }
}
```

#### Failed Response (200)
```json
{
  "valid": false,
  "message": "Data tidak cocok dengan database Dukcapil",
  "data": {}
}
```

---

### 📧 **POST** `/api/verification/email`
Verifikasi ketersediaan email

#### Request Body
```json
{
  "email": "user@example.com"
}
```

#### Available Response (200)
```json
{
  "available": true,
  "message": "Email tersedia",
  "data": {}
}
```

---

### 📱 **POST** `/api/verification/phone`
Verifikasi ketersediaan nomor telepon

#### Request Body
```json
{
  "nomorTelepon": "081234567890"
}
```

#### Available Response (200)
```json
{
  "available": true,
  "message": "Nomor telepon tersedia",
  "data": {}
}
```

---

### 🆔 **POST** `/api/verification/nik-check`
Check NIK tanpa nama (simple check)

#### Request Body
```json
{
  "nik": "3175031234567890"
}
```

#### Registered Response (200)
```json
{
  "registered": true,
  "message": "NIK terdaftar di database Dukcapil"
}
```

---

### 📊 **GET** `/api/verification/stats`
Verification statistics

#### Response (200)
```json
{
  "totalNikChecks": 105,
  "validNik": 78,
  "invalidNik": 27,
  "emailVerifications": 120,
  "availableEmails": 115,
  "takenEmails": 5,
  "phoneVerifications": 110,
  "availablePhones": 105,
  "takenPhones": 5
}
```

---

## 🗂 Endpoint Summary

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| **Registration Management** |
| GET | `/api/auth/health` | Health check | ❌ |
| POST | `/api/auth/register` | Customer registration + auto login | ❌ |
| POST | `/api/auth/check-password` | Password strength check | ❌ |
| POST | `/api/auth/validate-nik` | NIK format validation | ❌ |
| POST | `/api/auth/verify-email` | Email verification | ❌ |
| GET | `/api/auth/stats` | Registration statistics | ❌ |
| GET | `/api/auth/profile` | Customer profile | ✅ |
| **Authentication** |
| POST | `/api/auth/login` | Customer login | ❌ |
| GET | `/api/auth/me` | Current user info | ✅ |
| POST | `/api/auth/logout` | Logout user | ✅ |
| POST | `/api/auth/refresh-token` | Refresh JWT token | ✅ |
| GET | `/api/auth/check-auth` | Check auth status | ✅ |
| **Verification Service** |
| GET | `/api/verification/health` | Health check | ❌ |
| POST | `/api/verification/nik` | NIK + name + birthdate verification | ❌ |
| POST | `/api/verification/email` | Email availability check | ❌ |
| POST | `/api/verification/phone` | Phone availability check | ❌ |
| POST | `/api/verification/nik-check` | NIK existence check | ❌ |
| GET | `/api/verification/stats` | Verification statistics | ❌ |

---

## 🔧 Configuration & Security

### Cookie Configuration
- **Name:** `authToken`
- **HttpOnly:** `true` (XSS protection)
- **Secure:** `false` (development) / `true` (production)
- **Path:** `/`
- **Domain:** `localhost` (development)
- **Max-Age:** `86400` seconds (24 hours)

### External Dependencies
- **Dukcapil Service:** `http://localhost:8081` (NIK verification)
- **Database:** PostgreSQL for customer data
- **JWT:** Token-based authentication