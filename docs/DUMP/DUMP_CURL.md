# Quick Login Tests - Copy & Paste Ready

# 1. Health Check
curl -X GET "http://localhost:8080/api/auth/health" \
  -H "Content-Type: application/json"

# 2. Registration (Create Test User)
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "namaIbuKandung": "Test Mother",
    "nomorTelepon": "081234567890",
    "email": "testuser@example.com",
    "password": "TestPassword123!",
    "tipeAkun": "Individual",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Software Developer",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "5-10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1001,
    "alamat": {
      "namaAlamat": "Jl. Test No. 123",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Selatan",
      "kecamatan": "Kebayoran Baru",
      "kelurahan": "Melawai",
      "kodePos": "12160"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Father",
      "pekerjaanWali": "Pensiunan",
      "alamatWali": "Jl. Test No. 123",
      "nomorTeleponWali": "081298765432"
    }
  }'

# 3. Login (Valid Credentials)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "email": "testuser@example.com",
    "password": "TestPassword123!"
  }'

# 4. Login (Invalid Email)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@example.com",
    "password": "TestPassword123!"
  }'

# 5. Login (Invalid Password)
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "WrongPassword"
  }'

# 6. Get Current User (with cookie)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 7. Get Current User (without cookie - should fail)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json"

# 8. Check Auth Status (with cookie)
curl -X GET "http://localhost:8080/api/auth/check-auth" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 9. Get Customer Profile (with cookie)
curl -X GET "http://localhost:8080/api/auth/profile" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 10. Password Strength Check
curl -X POST "http://localhost:8080/api/auth/check-password" \
  -H "Content-Type: application/json" \
  -d '{
    "password": "StrongPassword123!"
  }'

# 11. Logout
curl -X POST "http://localhost:8080/api/auth/logout" \
  -H "Content-Type: application/json" \
  -b cookies.txt

# 12. Try Access After Logout (should fail)
curl -X GET "http://localhost:8080/api/auth/me" \
  -H "Content-Type: application/json" \
  -b cookies.txt