package com.reg.regis.service;

import com.reg.regis.dto.DukcapilResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
/*** SECURITY PATCH ***/
import org.springframework.core.ParameterizedTypeReference;
/**********************/

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DukcapilClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.dukcapil.base-url}")
    private String dukcapilBaseUrl;

    @Value("${app.dukcapil.verify-nik-endpoint}")
    private String verifyNikEndpoint;

    @Value("${app.dukcapil.check-nik-endpoint}")
    private String checkNikEndpoint;

    /**
     * Verifikasi NIK, nama lengkap, dan tanggal lahir via Dukcapil Service
     */
    public DukcapilResponseDto verifyNikNameAndBirthDate(String nik, String namaLengkap, LocalDate tanggalLahir) {
        try {
            String url = dukcapilBaseUrl + verifyNikEndpoint;

            // Prepare request dengan SEMUA field yang dibutuhkan Dukcapil
            Map<String, Object> request = new HashMap<>();
            request.put("nik", nik);
            request.put("namaLengkap", namaLengkap);
            request.put("tanggalLahir", tanggalLahir.toString()); // Format: yyyy-MM-dd

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Customer-Service/1.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            System.out.println("🌐 Calling Dukcapil Service: " + url);
            System.out.println("📤 Request: " + request);

            // Make HTTP call
            ResponseEntity<DukcapilResponseDto> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                DukcapilResponseDto.class
            );

            DukcapilResponseDto responseBody = response.getBody();
            System.out.println("📥 Response from Dukcapil: " + responseBody);

            if (responseBody != null) {
                return responseBody;
            } else {
                return new DukcapilResponseDto(false, "Tidak ada response dari Dukcapil Service");
            }

        } catch (ResourceAccessException e) {
            System.err.println("❌ Dukcapil Service tidak dapat diakses: " + e.getMessage());
            return new DukcapilResponseDto(false, "Dukcapil Service tidak dapat diakses. Pastikan service berjalan di " + dukcapilBaseUrl);

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Client error dari Dukcapil Service: " + e.getMessage());
            return new DukcapilResponseDto(false, "Error validasi dari Dukcapil Service: " + e.getResponseBodyAsString());

        } catch (HttpServerErrorException e) {
            System.err.println("❌ Server error dari Dukcapil Service: " + e.getMessage());
            return new DukcapilResponseDto(false, "Dukcapil Service mengalami error internal");

        } catch (Exception e) {
            System.err.println("❌ Unexpected error calling Dukcapil Service: " + e.getMessage());
            e.printStackTrace();
            return new DukcapilResponseDto(false, "Terjadi kesalahan saat menghubungi Dukcapil Service: " + e.getMessage());
        }
    }

    /**
     * Check apakah NIK exists di Dukcapil
     */
    public boolean isNikExists(String nik) {
        try {
            String url = dukcapilBaseUrl + checkNikEndpoint;

            // Prepare request
            Map<String, String> request = new HashMap<>();
            request.put("nik", nik);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            System.out.println("🌐 Checking NIK existence: " + url);

            // Make HTTP call
            // *** PERBAIKAN BARIS 119 ***
            // Pastikan ResponseEntity juga dideklarasikan dengan tipe generik yang tepat
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange( // <--- PERUBAHAN DI SINI
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("exists")) {
                return (Boolean) responseBody.get("exists");
            }

            return false;

        } catch (Exception e) {
            System.err.println("❌ Error checking NIK existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get Dukcapil Service health status
     */
    public boolean isDukcapilServiceHealthy() {
        try {
            String url = dukcapilBaseUrl + "/health";

            // *** PERBAIKAN BARIS 144 ***
            // Pastikan ResponseEntity juga dideklarasikan dengan tipe generik yang tepat
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange( // <--- PERUBAHAN DI SINI
                url,
                HttpMethod.GET,
                null, // No request body for GET
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            Map<String, Object> body = response.getBody();

            return body != null && "OK".equals(body.get("status"));

        } catch (Exception e) {
            System.err.println("❌ Dukcapil Service health check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get base URL untuk debugging
     */
    public String getDukcapilBaseUrl() {
        return dukcapilBaseUrl;
    }
}