# 🚀 cURL & Postman Guide untuk Registration

## 📋 **Endpoint:** `POST /registration/register`
- **URL:** `http://localhost:8080/registration/register`
- **Method:** POST
- **Content-Type:** application/json
- **Response:** Auto-login dengan Set-Cookie

---

## 💻 **cURL Commands**

### 🎯 **1. Basic Registration (Complete Data)**
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

### 🎯 **2. Registration dengan Response Headers**
```bash
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -v \
  -d '{
    "namaLengkap": "Ahmad Fauzi",
    "namaIbuKandung": "Fatimah",
    "nomorTelepon": "081234567890",
    "email": "ahmad.fauzi@example.com",
    "password": "StrongPass123!",
    "tipeAkun": "BNI Syariah",
    "tempatLahir": "Bandung",
    "tanggalLahir": "1992-03-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Software Engineer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": ">Rp10 - 25 juta",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 1024,
    "alamat": {
      "namaAlamat": "Jl. Asia Afrika No. 123",
      "provinsi": "Jawa Barat",
      "kota": "Bandung",
      "kecamatan": "Sumur Bandung",
      "kelurahan": "Braga",
      "kodePos": "40111"
    },
    "wali": {
      "jenisWali": "Ibu",
      "namaLengkapWali": "Siti Khadijah",
      "pekerjaanWali": "Guru",
      "alamatWali": "Jl. Asia Afrika No. 123",
      "nomorTeleponWali": "081987654321"
    }
  }'
```

### 🎯 **3. Test dengan Data Minimal**
```bash
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Test User",
    "namaIbuKandung": "Test Mother",
    "nomorTelepon": "081111111111",
    "email": "testuser@example.com",
    "password": "TestPass123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "Perempuan",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Mahasiswa",
    "sumberPenghasilan": "Orang Tua",
    "rentangGaji": "<Rp2.5 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1025,
    "alamat": {
      "namaAlamat": "Jl. Test No. 1",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Menteng",
      "kodePos": "10310"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Father",
      "pekerjaanWali": "Pegawai Negeri",
      "alamatWali": "Jl. Test No. 1",
      "nomorTeleponWali": "081222222222"
    }
  }'
```

### 🎯 **4. Test Profile Access (setelah registration)**
```bash
# Setelah registration berhasil, test akses profile
curl -X GET http://localhost:8080/registration/profile \
  -b cookies.txt \
  -H "Accept: application/json"
```

### 🎯 **5. Formatted Output dengan jq**
```bash
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -s \
  -d '{
    "namaLengkap": "Pretty JSON",
    "namaIbuKandung": "JSON Mother",
    "nomorTelepon": "083333333333",
    "email": "prettyjson@example.com",
    "password": "Pretty123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Surabaya",
    "tanggalLahir": "1988-12-25",
    "jenisKelamin": "Laki-laki",
    "agama": "Kristen",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Designer",
    "sumberPenghasilan": "Freelance",
    "rentangGaji": "Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Bisnis",
    "kodeRekening": 1026,
    "alamat": {
      "namaAlamat": "Jl. Pemuda No. 99",
      "provinsi": "Jawa Timur",
      "kota": "Surabaya",
      "kecamatan": "Gubeng",
      "kelurahan": "Gubeng",
      "kodePos": "60281"
    },
    "wali": {
      "jenisWali": "Ibu",
      "namaLengkapWali": "Pretty Mother",
      "pekerjaanWali": "Dokter",
      "alamatWali": "Jl. Pemuda No. 99",
      "nomorTeleponWali": "083444444444"
    }
  }' | jq '.'
```

---

## 📱 **Postman Collection**

### 🔧 **1. Setup Environment**
```json
{
  "name": "Registration Service Environment",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080",
      "enabled": true
    },
    {
      "key": "auth_token",
      "value": "",
      "enabled": true
    }
  ]
}
```

### 🔧 **2. Collection Structure**
```json
{
  "info": {
    "name": "Customer Registration Service",
    "description": "API Collection untuk Customer Registration dengan Dukcapil Integration",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Registration",
      "item": [
        {
          "name": "Register Customer",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"namaLengkap\": \"{{customer_name}}\",\n  \"namaIbuKandung\": \"{{mother_name}}\",\n  \"nomorTelepon\": \"{{phone_number}}\",\n  \"email\": \"{{email}}\",\n  \"password\": \"{{password}}\",\n  \"tipeAkun\": \"BNI Taplus\",\n  \"tempatLahir\": \"{{birth_place}}\",\n  \"tanggalLahir\": \"{{birth_date}}\",\n  \"jenisKelamin\": \"{{gender}}\",\n  \"agama\": \"{{religion}}\",\n  \"statusPernikahan\": \"{{marital_status}}\",\n  \"pekerjaan\": \"{{job}}\",\n  \"sumberPenghasilan\": \"Gaji\",\n  \"rentangGaji\": \">Rp5 - 10 juta\",\n  \"tujuanPembuatanRekening\": \"Tabungan\",\n  \"kodeRekening\": {{account_code}},\n  \"alamat\": {\n    \"namaAlamat\": \"{{address}}\",\n    \"provinsi\": \"{{province}}\",\n    \"kota\": \"{{city}}\",\n    \"kecamatan\": \"{{district}}\",\n    \"kelurahan\": \"{{village}}\",\n    \"kodePos\": \"{{postal_code}}\"\n  },\n  \"wali\": {\n    \"jenisWali\": \"{{guardian_type}}\",\n    \"namaLengkapWali\": \"{{guardian_name}}\",\n    \"pekerjaanWali\": \"{{guardian_job}}\",\n    \"alamatWali\": \"{{guardian_address}}\",\n    \"nomorTeleponWali\": \"{{guardian_phone}}\"\n  }\n}"
            },
            "url": {
              "raw": "{{base_url}}/registration/register",
              "host": ["{{base_url}}"],
              "path": ["registration", "register"]
            },
            "description": "Register customer baru dengan validasi Dukcapil"
          },
          "response": []
        }
      ]
    }
  ]
}
```

### 🔧 **3. Postman Request Body (Complete)**
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

### 🔧 **4. Postman Variables**
```json
{
  "customer_name": "John Doe",
  "mother_name": "Jane Mother",
  "phone_number": "081234567890",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "birth_place": "Jakarta",
  "birth_date": "1990-05-15",
  "gender": "Laki-laki",
  "religion": "Islam",
  "marital_status": "Belum Kawin",
  "job": "Software Engineer",
  "account_code": 1027,
  "address": "Jl. Sudirman No. 123",
  "province": "DKI Jakarta",
  "city": "Jakarta Pusat",
  "district": "Tanah Abang",
  "village": "Bendungan Hilir",
  "postal_code": "10210",
  "guardian_type": "Ayah",
  "guardian_name": "John Father",
  "guardian_job": "Engineer",
  "guardian_address": "Jl. Sudirman No. 123",
  "guardian_phone": "081987654321"
}
```

### 🔧 **5. Postman Pre-request Script**
```javascript
// Generate random email untuk testing
const timestamp = Date.now();
const randomEmail = `test.user.${timestamp}@example.com`;
pm.environment.set("email", randomEmail);

// Generate random phone number
const randomPhone = `0812${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`;
pm.environment.set("phone_number", randomPhone);

// Generate random account code
const randomAccountCode = Math.floor(Math.random() * 9000) + 1000;
pm.environment.set("account_code", randomAccountCode);

console.log("Generated email:", randomEmail);
console.log("Generated phone:", randomPhone);
console.log("Generated account code:", randomAccountCode);
```

### 🔧 **6. Postman Tests Script**
```javascript
// Test response status
pm.test("Registration successful", function () {
    pm.response.to.have.status(200);
});

// Test response structure
pm.test("Response has success field", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('success');
    pm.expect(responseJson.success).to.be.true;
});

// Test customer data
pm.test("Response contains customer data", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('customer');
    pm.expect(responseJson.customer).to.have.property('email');
    pm.expect(responseJson.customer).to.have.property('namaLengkap');
});

// Extract and save auth token from cookie
pm.test("Auth cookie is set", function () {
    const authCookie = pm.cookies.get("authToken");
    pm.expect(authCookie).to.not.be.undefined;
    pm.environment.set("auth_token", authCookie);
    console.log("Auth token saved:", authCookie);
});

// Log response for debugging
console.log("Response:", pm.response.text());
```

---

## 🧪 **Testing Scenarios**

### ✅ **1. Happy Path Test**
```bash
# Basic successful registration
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{ ... complete valid data ... }'

# Expected: 200 OK with success=true and Set-Cookie
```

### ❌ **2. Validation Error Tests**
```bash
# Test missing required field
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User"
    // Missing other required fields
  }'

# Expected: 400 Bad Request with validation errors
```

```bash
# Test duplicate email
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "existing@example.com",
    ... // complete data with existing email
  }'

# Expected: 400 Bad Request with "Email sudah terdaftar"
```

### 🔄 **3. End-to-End Test**
```bash
# 1. Register
curl -X POST http://localhost:8080/registration/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{ ... }'

# 2. Access profile with auto-login cookie
curl -X GET http://localhost:8080/registration/profile \
  -b cookies.txt

# 3. Login again (optional)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -c cookies2.txt \
  -d '{"email":"registered@email.com","password":"password"}'
```

---

## 📊 **Expected Responses**

### ✅ **Success Response**
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
    "emailVerified": false,
    "alamat": { ... },
    "wali": { ... }
  }
}
```

### ❌ **Error Response**
```json
{
  "success": false,
  "error": "Email sudah terdaftar dalam sistem",
  "type": "validation_error"
}
```