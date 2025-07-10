package com.reg.regis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableTransactionManagement
public class RegistrationAbsoluteApplication {

    public static void main(String[] args) {
        // Load .env variables
        Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(RegistrationAbsoluteApplication.class, args);
        System.out.println("🔐 Secure Customer Registration API Started!");
        System.out.println("📍 API Base URL: http://localhost:8080/api");
        System.out.println("🔗 Health Check: http://localhost:8080/api/auth/health");
    }

}
