#!/bin/bash
# Complete CURL Test Commands for Customer Registration API
# Base URL: http://localhost:8080/api/auth

echo "=== CUSTOMER REGISTRATION API TESTING ==="
echo

# 1. HEALTH CHECK
echo "1. Testing Health Check..."
curl -X GET http://localhost:8080/api/auth/health
echo -e "\n"

# 2. REGISTRATION TEST (Valid Data)
echo "2. Testing Customer Registration (Valid Data)..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Ahmad Fauzi",
    "namaIbuKandung": "Siti Aminah",
    "nomorTelepon": "081234567890",
    "email": "ahmad.fauzi@example.com",
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
  }' \
  -c cookies.txt
echo -e "\n"

# 3. LOGIN TEST
echo "3. Testing Customer Login..."
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com",
    "password": "Password123!"
  }' \
  -c cookies.txt
echo -e "\n"

# 4. GET CURRENT USER
echo "4. Testing Get Current User..."
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt
echo -e "\n"

# 5. CHECK AUTHENTICATION
echo "5. Testing Check Authentication..."
curl -X GET http://localhost:8080/api/auth/check-auth \
  -b cookies.txt
echo -e "\n"

# 6. PASSWORD STRENGTH CHECK
echo "6. Testing Password Strength Check..."
curl -X POST http://localhost:8080/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "Password123!"
  }'
echo -e "\n"

# 7. REGISTRATION STATS
echo "7. Testing Registration Statistics..."
curl -X GET http://localhost:8080/api/auth/stats
echo -e "\n"

# 8. VERIFY EMAIL
echo "8. Testing Email Verification..."
curl -X POST http://localhost:8080/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com"
  }'
echo -e "\n"

# 9. REFRESH TOKEN
echo "9. Testing Refresh Token..."
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt
echo -e "\n"

# 10. LOGOUT
echo "10. Testing Logout..."
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt
echo -e "\n"

echo "=== ERROR TESTING ==="
echo

# 11. DUPLICATE EMAIL TEST
echo "11. Testing Duplicate Email Registration..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Budi Santoso",
    "namaIbuKandung": "Ani Budi",
    "nomorTelepon": "081987654321",
    "email": "ahmad.fauzi@example.com",
    "password": "Password456!",
    "tipeAkun": "BNI Simpedes",
    "tempatLahir": "Bandung",
    "tanggalLahir": "1990-05-15",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Wiraswasta",
    "sumberPenghasilan": "Usaha",
    "rentangGaji": "Rp2 - 5 juta",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 1024,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 5",
      "provinsi": "Jawa Barat",
      "kota": "Bandung",
      "kecamatan": "Coblong",
      "kelurahan": "Dago",
      "kodePos": "40132"
    },
    "wali": {
      "jenisWali": "Ibu",
      "namaLengkapWali": "Sari Wati",
      "pekerjaanWali": "Ibu Rumah Tangga",
      "alamatWali": "Jl. Sudirman No. 5",
      "nomorTeleponWali": "081567890123"
    }
  }'
echo -e "\n"

# 12. INVALID EMAIL FORMAT TEST
echo "12. Testing Invalid Email Format..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User",
    "namaIbuKandung": "Test Ibu",
    "nomorTelepon": "081111222333",
    "email": "invalid-email",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1025,
    "alamat": {
      "namaAlamat": "Jl. Test",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Menteng",
      "kodePos": "10310"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Ayah",
      "pekerjaanWali": "Karyawan",
      "alamatWali": "Jl. Test",
      "nomorTeleponWali": "081444555666"
    }
  }'
echo -e "\n"

# 13. WEAK PASSWORD TEST
echo "13. Testing Weak Password..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User 2",
    "namaIbuKandung": "Test Ibu 2",
    "nomorTelepon": "081222333444",
    "email": "test2@example.com",
    "password": "123",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-01-01",
    "jenisKelamin": "Perempuan",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Mahasiswa",
    "sumberPenghasilan": "Orang Tua",
    "rentangGaji": "<\Rp2 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1026,
    "alamat": {
      "namaAlamat": "Jl. Test 2",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Timur",
      "kecamatan": "Jatinegara",
      "kelurahan": "Kampung Melayu",
      "kodePos": "13330"
    },
    "wali": {
      "jenisWali": "Ibu",
      "namaLengkapWali": "Test Ibu Wali",
      "pekerjaanWali": "Guru",
      "alamatWali": "Jl. Test 2",
      "nomorTeleponWali": "081555666777"
    }
  }'
echo -e "\n"

# 14. INVALID PHONE FORMAT TEST
echo "14. Testing Invalid Phone Format..."
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test User 3",
    "namaIbuKandung": "Test Ibu 3",
    "nomorTelepon": "021-1234567",
    "email": "test3@example.com",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1027,
    "alamat": {
      "namaAlamat": "Jl. Test 3",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Barat",
      "kecamatan": "Kebon Jeruk",
      "kelurahan": "Kebon Jeruk",
      "kodePos": "11530"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Ayah 3",
      "pekerjaanWali": "Dokter",
      "alamatWali": "Jl. Test 3",
      "nomorTeleponWali": "123456789"
    }
  }'
echo -e "\n"

# 15. LOGIN WITH WRONG PASSWORD
echo "15. Testing Login with Wrong Password..."
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com",
    "password": "WrongPassword123!"
  }'
echo -e "\n"

# 16. LOGIN WITH NON-EXISTENT EMAIL
echo "16. Testing Login with Non-existent Email..."
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "Password123!"
  }'
echo -e "\n"

# 17. ACCESS PROTECTED ENDPOINT WITHOUT TOKEN
echo "17. Testing Access Protected Endpoint Without Token..."
curl -X GET http://localhost:8080/api/auth/me
echo -e "\n"

echo "=== TESTING COMPLETED ==="

# Individual CURL Commands - Copy paste satu per satu untuk testing

# ===========================================
# 1. HEALTH CHECK
# ===========================================
curl -X GET http://localhost:8080/api/auth/health

# ===========================================
# 2. REGISTER CUSTOMER (SUCCESS CASE)
# ===========================================
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Ahmad Fauzi",
    "namaIbuKandung": "Siti Aminah",
    "nomorTelepon": "081234567890",
    "email": "ahmad.fauzi@example.com",
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
  }' \
  -c cookies.txt

# ===========================================
# 3. LOGIN CUSTOMER
# ===========================================
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com",
    "password": "Password123!"
  }' \
  -c cookies.txt

# ===========================================
# 4. GET CURRENT USER PROFILE
# ===========================================
curl -X GET http://localhost:8080/api/auth/me \
  -b cookies.txt

# ===========================================
# 5. CHECK AUTHENTICATION STATUS
# ===========================================
curl -X GET http://localhost:8080/api/auth/check-auth \
  -b cookies.txt

# ===========================================
# 6. CHECK PASSWORD STRENGTH
# ===========================================
curl -X POST http://localhost:8080/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "Password123!"
  }'

# Test weak password
curl -X POST http://localhost:8080/api/auth/check-password \
  -H "Content-Type: application/json" \
  -d '{
    "password": "123"
  }'

# ===========================================
# 7. VERIFY EMAIL
# ===========================================
curl -X POST http://localhost:8080/api/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com"
  }'

# ===========================================
# 8. GET REGISTRATION STATISTICS
# ===========================================
curl -X GET http://localhost:8080/api/auth/stats

# ===========================================
# 9. REFRESH TOKEN
# ===========================================
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -b cookies.txt \
  -c cookies.txt

# ===========================================
# 10. LOGOUT
# ===========================================
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt

# ===========================================
# ERROR TEST CASES
# ===========================================

# DUPLICATE EMAIL TEST
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test Duplicate",
    "namaIbuKandung": "Test Ibu",
    "nomorTelepon": "081999888777",
    "email": "ahmad.fauzi@example.com",
    "password": "Password123!",
    "tipeAkun": "BNI Simpedes",
    "tempatLahir": "Bandung",
    "tanggalLahir": "1990-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Kawin",
    "pekerjaan": "Wiraswasta",
    "sumberPenghasilan": "Usaha",
    "rentangGaji": "Rp2 - 5 juta",
    "tujuanPembuatanRekening": "Investasi",
    "kodeRekening": 1024,
    "alamat": {
      "namaAlamat": "Jl. Sudirman No. 5",
      "provinsi": "Jawa Barat",
      "kota": "Bandung",
      "kecamatan": "Coblong",
      "kelurahan": "Dago",
      "kodePos": "40132"
    },
    "wali": {
      "jenisWali": "Ibu",
      "namaLengkapWali": "Sari Wati",
      "pekerjaanWali": "Ibu Rumah Tangga",
      "alamatWali": "Jl. Sudirman No. 5",
      "nomorTeleponWali": "081567890123"
    }
  }'

# INVALID EMAIL FORMAT TEST
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test Invalid Email",
    "namaIbuKandung": "Test Ibu",
    "nomorTelepon": "081111222333",
    "email": "invalid-email-format",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-01-01",
    "jenisKelamin": "Perempuan",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Mahasiswa",
    "sumberPenghasilan": "Orang Tua",
    "rentangGaji": "<\Rp2 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1025,
    "alamat": {
      "namaAlamat": "Jl. Test Invalid",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Pusat",
      "kecamatan": "Menteng",
      "kelurahan": "Menteng",
      "kodePos": "10310"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Ayah",
      "pekerjaanWali": "Karyawan",
      "alamatWali": "Jl. Test Invalid",
      "nomorTeleponWali": "081444555666"
    }
  }'

# INVALID PHONE FORMAT TEST
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "namaLengkap": "Test Invalid Phone",
    "namaIbuKandung": "Test Ibu",
    "nomorTelepon": "021-1234567",
    "email": "test.phone@example.com",
    "password": "Password123!",
    "tipeAkun": "BNI Taplus",
    "tempatLahir": "Jakarta",
    "tanggalLahir": "1995-01-01",
    "jenisKelamin": "Laki-laki",
    "agama": "Islam",
    "statusPernikahan": "Belum Kawin",
    "pekerjaan": "Karyawan",
    "sumberPenghasilan": "Gaji",
    "rentangGaji": "Rp5 - 10 juta",
    "tujuanPembuatanRekening": "Tabungan",
    "kodeRekening": 1026,
    "alamat": {
      "namaAlamat": "Jl. Test Phone",
      "provinsi": "DKI Jakarta",
      "kota": "Jakarta Barat",
      "kecamatan": "Kebon Jeruk",
      "kelurahan": "Kebon Jeruk",
      "kodePos": "11530"
    },
    "wali": {
      "jenisWali": "Ayah",
      "namaLengkapWali": "Test Ayah Phone",
      "pekerjaanWali": "Dokter",
      "alamatWali": "Jl. Test Phone",
      "nomorTeleponWali": "123456789"
    }
  }'

# WRONG PASSWORD LOGIN TEST
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "ahmad.fauzi@example.com",
    "password": "WrongPassword123!"
  }'

# NON-EXISTENT EMAIL LOGIN TEST
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nonexistent@example.com",
    "password": "Password123!"
  }'

# ACCESS PROTECTED ENDPOINT WITHOUT TOKEN
curl -X GET http://localhost:8080/api/auth/me