// src/main/java/com/reg/regis/controller/ProtectedResourceController.java
package com.reg.regis.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Opsional, untuk role-based authorization
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @RequestMapping("/api") // Contoh base path untuk API yang dilindungi
@CrossOrigin(origins = "${app.cors.allowed-origins}", allowCredentials = "true")
public class ProtectedResourceController {

    @GetMapping("/protected-resource")
    @PreAuthorize("isAuthenticated()") // Pastikan user sudah terautentikasi (opsional: tambahkan role, misal "hasRole('CUSTOMER')")
    public ResponseEntity<String> getProtectedResource() {
        // Di sini Anda bisa mendapatkan informasi user yang sudah terautentikasi
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String username = authentication.getName(); // Email user dari JWT
        return ResponseEntity.ok("Data rahasia dari server! Anda berhasil mengakses sumber daya yang dilindungi.");
    }
}