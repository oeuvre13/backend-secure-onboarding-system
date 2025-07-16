# 📄 API Contract - Customer Registration Service (Final Version)

## Service Information
- **Service Name:** Customer Registration Service dengan Dukcapil Integration
- **Version:** 1.0.0
- **Base URL:** `http://localhost:8080`
- **Protocol:** HTTP/HTTPS
- **Content-Type:** `application/json`
- **Authentication:** Cookie-based (HTTP-only cookies)

## 🎯 Service Architecture (Final & Synced)
3 Controller dengan rate limiting yang tepat:
1. **`/registration`** - Registration Management (NO Rate Limiting)
2. **`/auth`** - Authentication & Login (Rate Limited ✅)
3. **`/verification`** - NIK & Data Verification (Rate Limited ✅)

## 🔧 Rate Limiting Configuration
- **Enabled:** `true`
- **Capacity:** 10 requests per client
- **Refill Rate:** 2 requests per minute
- **Applied to:** `/auth/**` and `/verification/**`
- **Client Identification:** IP Address (with X-Forwarded-For support)
- **Response on Limit:** HTTP 429 + `{"error":"Too many requests. Please try again later."}`

---

# 📋 API Endpoints

## 1. REGISTRATION MANAGEMENT (`/registration`) 📝
**🚫 NO Rate Limiting Applied**

### 🏥 **GET** `/registration/health`
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
    "register": "POST /registration/register",
    "checkPassword": "POST /registration/check-password",
    "validateNik": "POST /registration/validate-nik",
    "verifyEmail": "POST /registration/verify-email",
    "stats": "GET /registration/stats",
    "health": "GET /registration/health",
    "profile": "GET /registration/profile"
  }
}
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/registration/health
```

---

### 📝 **POST** `/registration/register` ⭐ **MAIN ENDPOINT**
Registrasi customer baru dengan validasi Dukcapil + Auto Login

#### Request Body
```json
{
  "namaLengkap": "Bostang Pejompongan",
  "namaIbuKandung": "Siti Aminah", 
  "nomorTelepon": "089651524900",
  "email": "bostang.p@example.com",
  "password": "Password123!",
  "tipeAkun": "BNI Taplus",
  "tempatLahir": "Jakarta",
  "tanggalLahir": "1995-07-10",
  "jenisKelamin": "Laki-laki",
  "agama": "Islam",
  "statusPernikahan": "Belum Kawin",
  "pekerjaan": "Karyawan Swasta",
  "sumberPenghasilan": "Gaji",
  "rentangGaji": ">Rp5 - 10 juta",
  "tujuanPembuatanRekening": "Tabungan",
  "kodeRekening": 1023,
  "alamat": {
    "namaAlamat": "Jl. Melati No. 10",
    "provinsi": "DKI Jakarta",
    "kota": "Jakarta Selatan", 
    "kecamatan": "Kebayoran Baru",
    "kelurahan": "Melawai",
    "kodePos": "12160"
  },
  "wali": {
    "jenisWali": "Ayah",
    "namaLengkapWali": "Budi Hartono",
    "pekerjaanWali": "Pensiunan",
    "alamatWali": "Jl. Melati No. 10",
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
    "namaLengkap": "Bostang Pejompongan",
    "nik": "3175031234567890",
    "email": "bostang.p@example.com",
    "nomorTelepon": "089651524900",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan Swasta",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Melati No. 10",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kebayoran Baru",
      "kelurahan": "Melawai",
      "kodePos": "12160"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Budi Hartono",
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

#### System Error (400)
```json
{
  "success": false,
  "error": "Terjadi kesalahan sistem: Database connection failed",
  "type": "system_error"
}
```

**Set-Cookie Header:**
```
Set-Cookie: authToken=<jwt-token>; HttpOnly; Path=/; Max-Age=86400; Domain=localhost
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Bostang Pejompongan",
    "namaIbuKandung": "Siti Aminah",
    "nomorTelepon": "089651524900",
    "email": "bostang.p@example.com",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan Swasta",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": ">Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1023,
    "alamat": {
      "namaAlamat": "Jl. Melati No. 10",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kebayoran Baru",
      "kelurahan": "Melawai",
      "kodePos": "12160"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Budi Hartono",
      "pekerjaanWali": "Pensiunan",
      "alamatWali": "Jl. Melati No. 10",
      "nomorTeleponWali": "081298765432"
    }
  }'
```

---

### 🔑 **POST** `/registration/check-password`
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

**Password Strength Values:**
- `WEAK` - Password terlalu lemah
- `MEDIUM` - Password cukup kuat  
- `STRONG` - Password sangat kuat

#### cURL Example
```bash
curl -X POST http://localhost:8080/registration/check-password \
  -H "Content-Type: application/json" \
  -d '{"password": "Password123!"}'
```

---

### ✅ **POST** `/registration/validate-nik`
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

#### cURL Example
```bash
curl -X POST http://localhost:8080/registration/validate-nik \
  -H "Content-Type: application/json" \
  -d '{"nik": "3175031234567890"}'
```

---

### 📧 **POST** `/registration/verify-email`
Verify customer email

#### Request Body
```json
{
  "email": "bostang.p@example.com"
}
```

#### Response (200)
```json
{
  "message": "Email berhasil diverifikasi"
}
```

#### Error Response (400)
```json
{
  "error": "Email tidak valid atau sudah diverifikasi"
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/registration/verify-email \
  -H "Content-Type: application/json" \
  -d '{"email": "bostang.p@example.com"}'
```

---

### 📊 **GET** `/registration/stats`
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

#### cURL Example
```bash
curl -X GET http://localhost:8080/registration/stats
```

---

### 👤 **GET** `/registration/profile`
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
    "namaLengkap": "Bostang Pejompongan",
    "nik": "3175031234567890",
    "email": "bostang.p@example.com",
    "nomorTelepon": "089651524900",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan Swasta",
    "emailVerified": false,
    "alamat": {
      "namaAlamat": "Jl. Melati No. 10",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kebayoran Baru",
      "kelurahan": "Melawai",
      "kodePos": "12160"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Budi Hartono",
      "pekerjaanWali": "Pensiunan",
      "nomorTeleponWali": "081298765432"
    }
  }
}
```

#### Unauthorized (401)
```json
{
  "error": "Token tidak ditemukan"
}
```

#### Not Found (404)
```json
{
  "error": "Customer tidak ditemukan"
}
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/registration/profile \
  -b cookies.txt
```

---

## 2. AUTHENTICATION (`/auth`) 🔐
**🚨 Rate Limited: 10 requests/minute per IP**

### 🔐 **POST** `/auth/login` ⭐ **MAIN ENDPOINT**
Customer login dengan email dan password

#### Rate Limit
- **Limit:** 10 requests per minute per IP
- **Response on exceeded:** HTTP 429 + `{"error":"Too many requests. Please try again later."}`

#### Request Body
```json
{
  "email": "bostang.p@example.com",
  "password": "Password123!"
}
```

#### Success Response (200) + Set-Cookie
```json
{
  "message": "Login berhasil",
  "customer": {
    "id": 1,
    "namaLengkap": "Bostang Pejompongan",
    "email": "bostang.p@example.com",
    "nomorTelepon": "089651524900",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan Swasta",
    "emailVerified": false,
    "alamat": {...},
    "wali": {...}
  }
}
```

#### Failed Response (400)
```json
{
  "error": "Email atau password tidak valid"
}
```

#### Rate Limit Response (429)
```json
{
  "error": "Too many requests. Please try again later."
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "bostang.p@example.com",
    "password": "Password123!"
  }'
```

---

### 👤 **GET** `/auth/me`
Get current authenticated user

#### Rate Limit
- **Limit:** 10 requests per minute per IP

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
    "namaLengkap": "Bostang Pejompongan",
    "email": "bostang.p@example.com",
    "nomorTelepon": "089651524900",
    "tipeAkun": "BNI Taplus",
    "emailVerified": false,
    "alamat": {...},
    "wali": {...}
  }
}
```

#### Unauthorized (401)
```json
{
  "error": "Tidak terautentikasi"
}
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/auth/me \
  -b cookies.txt
```

---

### 🚪 **POST** `/auth/logout`
Logout user (clear cookie)

#### Rate Limit
- **Limit:** 10 requests per minute per IP

#### Response (200) + Clear-Cookie
```json
{
  "message": "Logout berhasil"
}
```

**Clear-Cookie Header:**
```
Set-Cookie: authToken=; HttpOnly; Path=/; Max-Age=0; Domain=localhost
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/auth/logout \
  -b cookies.txt
```

---

### 🔄 **POST** `/auth/refresh-token`
Refresh JWT token

#### Rate Limit
- **Limit:** 10 requests per minute per IP

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

#### Failed Response (401)
```json
{
  "error": "Token tidak valid"
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt
```

---

### ✅ **GET** `/auth/check-auth`
Check authentication status

#### Rate Limit
- **Limit:** 10 requests per minute per IP

#### Headers
```
Cookie: authToken=<jwt-token>
```

#### Authenticated Response (200)
```json
{
  "authenticated": true,
  "email": "bostang.p@example.com"
}
```

#### Not Authenticated Response (200)
```json
{
  "authenticated": false
}
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/auth/check-auth \
  -b cookies.txt
```

---

## 3. VERIFICATION SERVICE (`/verification`) 🔍
**🚨 Rate Limited: 10 requests/minute per IP**

### 🏥 **GET** `/verification/health`
Health check verification service

#### Rate Limit
- **Limit:** 10 requests per minute per IP

#### Response (200)
```json
{
  "status": "OK",
  "service": "Verification Service (via Dukcapil Integration)",
  "timestamp": 1721120400000,
  "endpoints": {
    "nikVerification": "POST /verification/nik",
    "emailVerification": "POST /verification/email",
    "phoneVerification": "POST /verification/phone",
    "nikCheck": "POST /verification/nik-check",
    "stats": "GET /verification/stats"
  }
}
```

#### cURL Example
```bash
curl -X GET http://localhost:8080/verification/health
```

---

### 🔍 **POST** `/verification/nik` ⭐ **MAIN ENDPOINT**
Verifikasi NIK dengan nama lengkap via Dukcapil Service

#### Rate Limit
- **Limit:** 10 requests per minute per IP

#### Request Body
```json
{
  "nik": "1234567890123456",
  "namaLengkap": "Ahmad Fauzi"
}
```

#### Success Response (200)
```json
{
  "valid": true,
  "message": "NIK dan nama cocok",
  "data": {
    "nik": "1234567890123456",
    "namaLengkap": "Ahmad Fauzi",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-07-10",
    "jenisKelamin": "Laki-laki",
    "alamat": "Jl. Melati No. 10",
    "kecamatan": "Kebayoran Baru", 
    "kelurahan": "Melawai",
    "agama": "Islam",
    "statusPerkawinan": "Belum Kawin"
  }
}
```

#### Failed Response (200)
```json
{
  "valid": false,
  "message": "NIK tidak terdaftar di database Dukcapil",
  "data": {}
}
```

#### Validation Error (400)
```json
{
  "valid": false,
  "message": "NIK harus 16 digit angka"
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/verification/nik \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "1234567890123456",
    "namaLengkap": "Ahmad Fauzi"
  }'
```

---

### 📧 **POST** `/verification/email`
Verifikasi ketersediaan email

#### Rate Limit
- **Limit:** 10 requests per minute per IP

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

#### Not Available Response (200)
```json
{
  "available": false,
  "message": "Email sudah digunakan",
  "data": {}
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/verification/email \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com"}'
```

---

### 📱 **POST** `/verification/phone`
Verifikasi ketersediaan nomor telepon

#### Rate Limit
- **Limit:** 10 requests per minute per IP

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

#### cURL Example
```bash
curl -X POST http://localhost:8080/verification/phone \
  -H "Content-Type: application/json" \
  -d '{"nomorTelepon": "081234567890"}'
```

---

### 🆔 **POST** `/verification/nik-check`
Check NIK tanpa nama (simple check)

#### Rate Limit
- **Limit:** 10 requests per minute per IP

#### Request Body
```json
{
  "nik": "1234567890123456"
}
```

#### Registered Response (200)
```json
{
  "registered": true,
  "message": "NIK terdaftar di database Dukcapil"
}
```

#### Not Registered Response (200)
```json
{
  "registered": false,
  "message": "NIK tidak terdaftar di database Dukcapil"
}
```

#### Validation Error (400)
```json
{
  "registered": false,
  "message": "NIK harus 16 digit"
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/verification/nik-check \
  -H "Content-Type: application/json" \
  -d '{"nik": "1234567890123456"}'
```

---

### 📊 **GET** `/verification/stats`
Verification statistics

#### Rate Limit
- **Limit:** 10 requests per minute per IP

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

#### cURL Example
```bash
curl -X GET http://localhost:8080/verification/stats
```

---

## 🔧 Configuration & Security

### Rate Limiting Configuration
```properties
# application.properties
app.rateLimit.enabled=true
app.rateLimit.capacity=10          # Max 10 requests
app.rateLimit.refillRate=2         # Refill 2 requests per minute
```

### Dukcapil Client Configuration
```properties
# application.properties
app.dukcapil.timeout=10000         # 10 seconds timeout
```

### CORS Configuration
```java
// All controllers
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")

# application.properties
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

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
- **Bucket4j:** Rate limiting implementation

---

## 📊 Data Models

### Complete Registration Request
```json
{
  "namaLengkap": "string",
  "namaIbuKandung": "string",
  "nomorTelepon": "string",
  "email": "string (unique)",
  "password": "string",
  "tipeAkun": "string",
  "tempatLahir": "string",
  "tanggalLahir": "YYYY-MM-DD",
  "jenisKelamin": "Laki-laki | Perempuan",
  "agama": "string",
  "statusPernikahan": "string",
  "pekerjaan": "string",
  "sumberPenghasilan": "string",
  "rentangGaji": "string",
  "tujuanPembuatanRekening": "string",
  "kodeRekening": "integer",
  "alamat": {
    "namaAlamat": "string",
    "provinsi": "string",
    "kota": "string",
    "kecamatan": "string",
    "kelurahan": "string",
    "kodePos": "string"
  },
  "wali": {
    "jenisWali": "Ayah | Ibu",
    "namaLengkapWali": "string",
    "pekerjaanWali": "string",
    "alamatWali": "string",
    "nomorTeleponWali": "string"
  }
}
```

---

## 🗂 Endpoint Summary (Final & Synced)

| Method | Endpoint | Description | Auth Required | Rate Limited |
|--------|----------|-------------|---------------|--------------|
| **Registration Management** |
| GET | `/registration/health` | Health check | ❌ | ❌ |
| POST | `/registration/register` | Customer registration + auto login | ❌ | ❌ |
| POST | `/registration/check-password` | Password strength check | ❌ | ❌ |
| POST | `/registration/validate-nik` | NIK format validation | ❌ | ❌ |
| POST | `/registration/verify-email` | Email verification | ❌ | ❌ |
| GET | `/registration/stats` | Registration statistics | ❌ | ❌ |
| GET | `/registration/profile` | Customer profile | ✅ | ❌ |
| **Authentication** |
| POST | `/auth/login` | Customer login | ❌ | ✅ |
| GET | `/auth/me` | Current user info | ✅ | ✅ |
| POST | `/auth/logout` | Logout user | ✅ | ✅ |
| POST | `/auth/refresh-token` | Refresh JWT token | ✅ | ✅ |
| GET | `/auth/check-auth` | Check auth status | ✅ | ✅ |
| **Verification Service** |
| GET | `/verification/health` | Health check | ❌ | ✅ |
| POST | `/verification/nik` | NIK + name verification | ❌ | ✅ |
| POST | `/verification/email` | Email availability check | ❌ | ✅ |
| POST | `/verification/phone` | Phone availability check | ❌ | ✅ |
| POST | `/verification/nik-check` | NIK existence check | ❌ | ✅ |
| GET | `/verification/stats` | Verification statistics | ❌ | ✅ |

---

## 🚀 Testing Guide

### Test Rate Limiting
```bash
# Test rate limited endpoint (should get 429 after 10 requests)
for i in {1..12}; do
  echo "Request $i:"
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@test.com","password":"wrong"}' \
    -w "Status: %{http_code}\n"
  sleep 1
done
```

### Test Non-Rate Limited
```bash
# Test non-rate limited endpoint (should always work)
for i in {1..15}; do
  echo "Request $i:"
  curl -X POST http://localhost:8080/registration/check-password \
    -H "Content-Type: application/json" \
    -d '{"password":"test123"}' \
    -w "Status: %{http_code}\n"
done
```

### Complete Registration Flow
```bash
# 1. Check health
curl http://localhost:8080/registration/health

# 2. Register customer (gets auto-login cookie)
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{ ... complete registration data ... }'

# 3. Check profile (using cookie from registration)
curl -X GET http://localhost:8080/registration/profile \
  -b cookies.txt

# 4. Verify NIK
curl -X POST http://localhost:8080/verification/nik \
  -H "Content-Type: application/json" \
  -d '{"nik":"1234567890123456","namaLengkap":"Ahmad Fauzi"}'
```