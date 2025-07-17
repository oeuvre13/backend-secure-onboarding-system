package com.reg.regis.config;

import com.reg.regis.security.JwtAuthFilter; // Import filter JWT Anda
import com.reg.regis.service.CustomerUserDetailsService; // Import UserDetailsService Anda
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Tambahkan ini
import org.springframework.security.authentication.AuthenticationProvider; // Tambahkan ini
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Tambahkan ini
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Tambahkan ini
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Tambahkan ini
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Tambahkan ini
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //  Untuk mengaktifkan @PreAuthorize di metode controller
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // **INJECT FILTER JWT ANDA**

    @Autowired
    private CustomerUserDetailsService userDetailsService; // **INJECT USERDETAILSSERVICE ANDA**

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/verification/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/error").permitAll()
                // .requestMatchers("OPTIONS", "/**").permitAll()           // give warning
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Explicitly use HttpMethod.OPTIONS
                .requestMatchers("/api/**").authenticated() // **TAMBAHKAN INI**: Lindungi semua endpoint di bawah /api/
                .anyRequest().authenticated() // **UBAH INI (opsional)**: Ganti .permitAll() menjadi .authenticated() jika semua jalur lain harus dilindungi secara default. Jika tidak, tetap .permitAll()
            )
            .authenticationProvider(authenticationProvider()) // **TAMBAHKAN INI**: Daftarkan AuthenticationProvider kustom Anda
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // **TAMBAHKAN INI**: Masukkan filter JWT Anda sebelum filter autentikasi standar Spring Security
            .headers(headers -> headers
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .addHeaderWriter((request, response) -> {
                    response.setHeader("X-Frame-Options", "DENY");
                    response.setHeader("X-Content-Type-Options", "nosniff");
                    response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                    response.setHeader("X-XSS-Protection", "1; mode=block");
                    response.setHeader("X-Service", "Customer-Registration-Service");
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                })
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Token tidak valid atau sudah expire\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Access denied\",\"message\":\"Akses ditolak\"}");
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // **TAMBAHKAN INI**: Bean untuk AuthenticationProvider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // authProvider.setUserDetailsService(userDetailsService); // Gunakan UserDetailsService kustom Anda
        // authProvider.setPasswordEncoder(passwordEncoder());
        // return authProvider;

        /**** SECURITY PATCH ****/
        // Pass userDetailsService and passwordEncoder directly into the constructor
        // No need for separate setter calls.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
        
        /************************/
    }

    // **TAMBAHKAN INI**: Bean untuk AuthenticationManager
    // Dibutuhkan di LoginController untuk melakukan autentikasi pengguna
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://*.trycloudflare.com"
        ));

        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));

        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));

        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "Set-Cookie"
        ));

        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}