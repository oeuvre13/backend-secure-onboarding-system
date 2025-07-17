# 🚀 cURL & Postman Guide untuk Customer Registration Service

## 📋 **Base URL:** `http://localhost:8080/api`

---

# 🔐 CUSTOMER REGISTRATION SERVICE - QUICK TESTS (Real Data)

## 📝 **cURL Commands**

### 1. Health Checks
```bash
echo "📊 Health Checks:"
curl -X GET "http://localhost:8080/api/registration/health" | jq
curl -X GET "http://localhost:8080/api/auth/health" | jq  
curl -X GET "http://localhost:8080/api/verification/health" | jq
```

### 2. NIK Verification Tests (Real Dukcapil Data)
```bash
echo "🔍 Testing John Doe (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tanggalLahir": "1990-05-15"
  }' | jq

echo "🔍 Testing Jane Smith (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175032345678901",
    "namaLengkap": "Jane Smith",
    "tanggalLahir": "1995-08-22"
  }' | jq

echo "🔍 Testing Ahmad Rahman (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175033456789012",
    "namaLengkap": "Ahmad Rahman",
    "tanggalLahir": "1985-12-10"
  }' | jq

echo "🔍 Testing Test User One (Valid):"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "1234567890123456",
    "namaLengkap": "Test User One",
    "tanggalLahir": "1995-01-01"
  }' | jq

echo "❌ Testing Invalid Name:"
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{
    "nik": "3175031234567890",
    "namaLengkap": "Wrong Name",
    "tanggalLahir": "1990-05-15"
  }' | jq
```

### 3. NIK Check (Simple)
```bash
echo "🔍 NIK Check Tests:"
curl -X POST "http://localhost:8080/api/verification/nik-check" \
  -H "Content-Type: application/json" \
  -d '{"nik": "3175031234567890"}' | jq

curl -X POST "http://localhost:8080/api/verification/nik-check" \
  -H "Content-Type: application/json" \
  -d '{"nik": "1234567890123456"}' | jq
```

### 4. Email & Phone Verification
```bash
echo "📧 Email Verification Tests:"
curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{"email": "newuser@example.com"}' | jq

curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{"email": "existing@example.com"}' | jq

echo "📱 Phone Verification Tests:"
curl -X POST "http://localhost:8080/api/verification/phone" \
  -H "Content-Type: application/json" \
  -d '{"nomorTelepon": "081999888777"}' | jq

curl -X POST "http://localhost:8080/api/verification/phone" \
  -H "Content-Type: application/json" \
  -d '{"nomorTelepon": "081234567890"}' | jq
```

### 5. Validation Tests
```bash
echo "✅ NIK Format Validation:"
curl -X POST "http://localhost:8080/api/registration/validate-nik" \
  -H "Content-Type: application/json" \
  -d '{"nik": "3175031234567890"}' | jq

echo "🔒 Password Strength Check:"
curl -X POST "http://localhost:8080/api/registration/check-password" \
  -H "Content-Type: application/json" \
  -d '{"password": "JohnDoe123!"}' | jq
```

### 6. Customer Registration (Main Test)
```bash
echo "👤 Registration Test - John Doe:"
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
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
      "namaAlamat": "Jl. Sudirman No. 123, RT 001/RW 002",
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
      "alamatWali": "Jl. Sudirman No. 123, RT 001/RW 002",
      "nomorTeleponWali": "081298765432"
    }
  }' | jq

echo "👤 Registration Test - Jane Smith:"
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "namaLengkap": "Jane Smith",
    "nik": "3175032345678901",
    "namaIbuKandung": "Anna Smith",
    "nomorTelepon": "081234567891",
    "email": "jane.smith@example.com",
    "password": "JaneSmith123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-08-22",
    "jenisKelamin": "Perempuan",
    "agama": "Kristen",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Marketing Manager",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "10-15 juta",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 1002,
    "alamat": {
      "namaAlamat": "Jl. Gatot Subroto No. 456, RT 003/RW 004",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Setiabudi",
      "kelurahan": "Kuningan Timur",
      "kodePos": "12950"
    },
    "wali": {
      "jenisWali": "Suami",
      "namaLengkapWali": "Michael Smith",
      "pekerjaanWali": "Konsultan",
      "alamatWali": "Jl. Gatot Subroto No. 456, RT 003/RW 004",
      "nomorTeleponWali": "081298765433"
    }
  }' | jq
```

### 7. Profile Access (Auto-login test)
```bash
echo "👤 John Profile (using auto-login cookie):"
curl -X GET "http://localhost:8080/api/registration/profile" \
  -b cookies.txt | jq

echo "👤 Jane Profile (using auto-login cookie):"
curl -X GET "http://localhost:8080/api/registration/profile" \
  -b cookies.txt | jq
```

### 8. Login Tests
```bash
echo "🔐 Login Tests:"
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "john.doe@example.com",
    "password": "JohnDoe123!"
  }' | jq

curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "jane.smith@example.com", 
    "password": "JaneSmith123!"
  }' | jq
```

### 9. Auth Tests
```bash
echo "👤 Get Current User (Auth/Me):"
curl -X GET "http://localhost:8080/api/auth/me" \
  -b cookies.txt | jq

echo "✅ Check Auth Status:"
curl -X GET "http://localhost:8080/api/auth/check-auth" \
  -b cookies.txt | jq

echo "🔄 Refresh Token:"
curl -X POST "http://localhost:8080/api/auth/refresh-token" \
  -b cookies.txt \
  -c refresh_cookies.txt | jq

echo "🚪 Logout:"
curl -X POST "http://localhost:8080/api/auth/logout" \
  -b cookies.txt | jq
```

### 10. Statistics
```bash
echo "📊 Statistics:"
curl -X GET "http://localhost:8080/api/registration/stats" | jq
curl -X GET "http://localhost:8080/api/verification/stats" | jq
```

---

# 📱 **Postman Collection**

## 🔧 **Environment Variables**
```json
{
  "name": "Customer Registration Environment",
  "values": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api",
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

## 📋 **Collection Structure**

### **1. Health Checks**
- **GET** `{{base_url}}/registration/health`
- **GET** `{{base_url}}/auth/health`
- **GET** `{{base_url}}/verification/health`

### **2. Registration**

#### **Register Customer**
- **Method:** POST
- **URL:** `{{base_url}}/registration/register`
- **Headers:**
  ```
  Content-Type: application/json
  ```
- **Body (raw JSON):**
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
      "namaAlamat": "Jl. Sudirman No. 123, RT 001/RW 002",
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
      "alamatWali": "Jl. Sudirman No. 123, RT 001/RW 002",
      "nomorTeleponWali": "081298765432"
    }
  }
  ```

#### **Check Password Strength**
- **Method:** POST
- **URL:** `{{base_url}}/registration/check-password`
- **Body:**
  ```json
  {
    "password": "JohnDoe123!"
  }
  ```

#### **Validate NIK**
- **Method:** POST
- **URL:** `{{base_url}}/registration/validate-nik`
- **Body:**
  ```json
  {
    "nik": "3175031234567890"
  }
  ```

#### **Verify Email**
- **Method:** POST
- **URL:** `{{base_url}}/registration/verify-email`
- **Body:**
  ```json
  {
    "email": "john.doe@example.com"
  }
  ```

#### **Get Profile**
- **Method:** GET
- **URL:** `{{base_url}}/registration/profile`
- **Headers:** *Cookie akan diatur otomatis*

#### **Registration Stats**
- **Method:** GET
- **URL:** `{{base_url}}/registration/stats`

### **3. Authentication**

#### **Login**
- **Method:** POST
- **URL:** `{{base_url}}/auth/login`
- **Body:**
  ```json
  {
    "email": "john.doe@example.com",
    "password": "JohnDoe123!"
  }
  ```

#### **Get Current User**
- **Method:** GET
- **URL:** `{{base_url}}/auth/me`

#### **Check Authentication**
- **Method:** GET
- **URL:** `{{base_url}}/auth/check-auth`

#### **Refresh Token**
- **Method:** POST
- **URL:** `{{base_url}}/auth/refresh-token`

#### **Logout**
- **Method:** POST
- **URL:** `{{base_url}}/auth/logout`

### **4. Verification**

#### **NIK Verification (Main)**
- **Method:** POST
- **URL:** `{{base_url}}/verification/nik`
- **Body:**
  ```json
  {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tanggalLahir": "1990-05-15"
  }
  ```

#### **Email Verification**
- **Method:** POST
- **URL:** `{{base_url}}/verification/email`
- **Body:**
  ```json
  {
    "email": "newuser@example.com"
  }
  ```

#### **Phone Verification**
- **Method:** POST
- **URL:** `{{base_url}}/verification/phone`
- **Body:**
  ```json
  {
    "nomorTelepon": "081999888777"
  }
  ```

#### **NIK Check (Simple)**
- **Method:** POST
- **URL:** `{{base_url}}/verification/nik-check`
- **Body:**
  ```json
  {
    "nik": "3175031234567890"
  }
  ```

#### **Verification Stats**
- **Method:** GET
- **URL:** `{{base_url}}/verification/stats`

## 🔧 **Postman Pre-request Scripts**

### **For Registration (Generate Random Data)**
```javascript
// Generate random email untuk testing
const timestamp = Date.now();
const randomEmail = `test.user.${timestamp}@example.com`;
pm.environment.set("test_email", randomEmail);

// Generate random phone number
const randomPhone = `0812${Math.floor(Math.random() * 100000000).toString().padStart(8, '0')}`;
pm.environment.set("test_phone", randomPhone);

// Generate random account code
const randomAccountCode = Math.floor(Math.random() * 9000) + 1000;
pm.environment.set("account_code", randomAccountCode);

console.log("Generated email:", randomEmail);
console.log("Generated phone:", randomPhone);
console.log("Generated account code:", randomAccountCode);
```

### **For Authentication (Extract Token)**
```javascript
// Extract auth token from response headers atau cookies jika diperlukan
pm.test("Auth cookie is set", function () {
    const authCookie = pm.cookies.get("authToken");
    if (authCookie) {
        pm.environment.set("auth_token", authCookie);
        console.log("Auth token saved:", authCookie);
    }
});
```

## 🔧 **Postman Tests Scripts**

### **For Registration Endpoint**
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

// Check auto-login cookie
pm.test("Auth cookie is set", function () {
    const authCookie = pm.cookies.get("authToken");
    pm.expect(authCookie).to.not.be.undefined;
    pm.environment.set("auth_token", authCookie);
    console.log("Auth token saved:", authCookie);
});

// Log response
console.log("Response:", pm.response.text());
```

### **For Verification Endpoint**
```javascript
// Test NIK verification response
pm.test("NIK verification completed", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has valid field", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('valid');
    pm.expect(responseJson).to.have.property('message');
});

pm.test("Valid NIK returns data", function () {
    const responseJson = pm.response.json();
    if (responseJson.valid) {
        pm.expect(responseJson).to.have.property('data');
        pm.expect(responseJson.data).to.not.be.empty;
    }
});
```

### **For Login Endpoint**
```javascript
// Test login success
pm.test("Login successful", function () {
    pm.response.to.have.status(200);
});

pm.test("Response contains customer data", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('message');
    pm.expect(responseJson).to.have.property('customer');
});

// Extract and save auth token
pm.test("Auth cookie is set", function () {
    const authCookie = pm.cookies.get("authToken");
    pm.expect(authCookie).to.not.be.undefined;
    pm.environment.set("auth_token", authCookie);
});
```

---

# 🧪 **Complete Test Scenarios**

## ✅ **Happy Path Test Flow**
```bash
#!/bin/bash
echo "🚀 COMPLETE HAPPY PATH TEST"

# 1. Health Check
curl -X GET "http://localhost:8080/api/registration/health"

# 2. NIK Verification
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"3175031234567890","namaLengkap":"John Doe","tanggalLahir":"1990-05-15"}'

# 3. Email Check
curl -X POST "http://localhost:8080/api/verification/email" \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe.test@example.com"}'

# 4. Register Customer
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -c test_cookies.txt \
  -d '{ ... complete registration data ... }'

# 5. Access Profile (auto-login)
curl -X GET "http://localhost:8080/api/registration/profile" \
  -b test_cookies.txt

# 6. Manual Login Test
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c login_cookies.txt \
  -d '{"email":"john.doe.test@example.com","password":"JohnDoe123!"}'

# 7. Check Auth Status
curl -X GET "http://localhost:8080/api/auth/check-auth" \
  -b login_cookies.txt

echo "✅ Happy path test completed!"
```

## ❌ **Error Test Scenarios**
```bash
#!/bin/bash
echo "❌ ERROR TEST SCENARIOS"

# 1. Invalid NIK Format
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"123","namaLengkap":"Test","tanggalLahir":"1990-01-01"}'

# 2. Wrong Name/Date Combination
curl -X POST "http://localhost:8080/api/verification/nik" \
  -H "Content-Type: application/json" \
  -d '{"nik":"3175031234567890","namaLengkap":"Wrong Name","tanggalLahir":"1990-05-15"}'

# 3. Duplicate Email Registration
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com", ... }'

# 4. Wrong Login Credentials
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"WrongPassword"}'

# 5. Access without Auth
curl -X GET "http://localhost:8080/api/registration/profile"

echo "❌ Error tests completed!"
```

## 📊 **Performance & Load Test**
```bash
#!/bin/bash
echo "📊 LOAD TEST - Multiple Requests"

# Test health endpoint dengan multiple requests
for i in {1..10}; do
  echo "Request $i:"
  curl -X GET "http://localhost:8080/api/registration/health" \
    -w "Time: %{time_total}s, Status: %{http_code}\n" \
    -s -o /dev/null
done

# Test NIK verification dengan different data
declare -a niks=("3175031234567890" "3175032345678901" "3175033456789012")
declare -a names=("John Doe" "Jane Smith" "Ahmad Rahman")
declare -a dates=("1990-05-15" "1995-08-22" "1985-12-10")

for i in {0..2}; do
  echo "Testing ${names[$i]}:"
  curl -X POST "http://localhost:8080/api/verification/nik" \
    -H "Content-Type: application/json" \
    -d "{\"nik\":\"${niks[$i]}\",\"namaLengkap\":\"${names[$i]}\",\"tanggalLahir\":\"${dates[$i]}\"}" \
    -w "Time: %{time_total}s, Status: %{http_code}\n" \
    -s -o /dev/null
done

echo "📊 Load test completed!"
```

---

# 🔍 **Debugging & Troubleshooting**

## **Common Issues**

### **1. Cookie Issues**
```bash
# Check if cookies are being set
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -v \
  -d '{"email":"test@example.com","password":"password"}'

# Check cookie file content
cat cookies.txt
```

### **2. CORS Issues**
```bash
# Test CORS with OPTIONS request
curl -X OPTIONS "http://localhost:8080/api/registration/register" \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

### **3. JSON Validation**
```bash
# Test with invalid JSON
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -d '{"namaLengkap":"Test"' # Invalid JSON

# Test with missing required fields
curl -X POST "http://localhost:8080/api/registration/register" \
  -H "Content-Type: application/json" \
  -d '{"namaLengkap":"Test Only"}'
```

### **4. Connection Test**
```bash
# Test service connectivity
curl -X GET "http://localhost:8080/api/registration/health" \
  --connect-timeout 5 \
  --max-time 10 \
  -v

# Test Dukcapil service connectivity (jika available)
curl -X GET "http://localhost:8081/health" \
  --connect-timeout 5 \
  --max-time 10 \
  -v
```

---

# 📝 **Response Examples**

## ✅ **Success Responses**

### Registration Success
```json
{
  "success": true,
  "message": "Registrasi berhasil! Data Anda telah terverifikasi dengan KTP Dukcapil.",
  "customer": {
    "id": 1,
    "namaLengkap": "John Doe",
    "nik": "3175031234567890",
    "email": "john.doe@example.com",
    "tipeAkun": "BNI Taplus"
  }
}
```

### NIK Verification Success
```json
{
  "valid": true,
  "message": "NIK dan nama cocok",
  "data": {
    "nik": "3175031234567890",
    "namaLengkap": "John Doe",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "LAKI_LAKI"
  }
}
```

## ❌ **Error Responses**

### Validation Error
```json
{
  "success": false,
  "error": "Email sudah terdaftar dalam sistem",
  "type": "validation_error"
}
```

### NIK Verification Failed
```json
{
  "valid": false,
  "message": "Data tidak cocok dengan database Dukcapil",
  "data": {}
}
```

### Authentication Error
```json
{
  "error": "Email atau password tidak valid"
}
```