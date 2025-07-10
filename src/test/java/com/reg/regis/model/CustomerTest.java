package com.reg.regis.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    private Validator validator;
    private Customer customer;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@test.com");
        customer.setPassword("hashedPassword123");
        customer.setPhone("+6281234567890");
        customer.setAge(25);
        customer.setEmailVerified(false);
    }

    @Test
    void shouldCreateValidCustomer() {
        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).isEmpty();
        assertThat(customer.getName()).isEqualTo("John Doe");
        assertThat(customer.getEmail()).isEqualTo("john@test.com");
        assertThat(customer.getEmailVerified()).isFalse();
    }

    @Test
    void shouldValidateNameNotBlank() {
        // Given
        customer.setName("");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required");
    }

    @Test
    void shouldValidateNameLength() {
        // Given
        customer.setName("A");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name must be between 2 and 100 characters");
    }

    @Test
    void shouldValidateNamePattern() {
        // Given
        customer.setName("John@Doe");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name can only contain letters and spaces");
    }

    @Test
    void shouldValidateEmailNotBlank() {
        // Given
        customer.setEmail("");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required");
    }

    @Test
    void shouldValidateEmailFormat() {
        // Given
        customer.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Please provide a valid email");
    }

    @Test
    void shouldValidatePasswordNotBlank() {
        // Given
        customer.setPassword("");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required");
    }

    @Test
    void shouldValidatePasswordLength() {
        // Given
        customer.setPassword("short");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must be at least 8 characters");
    }

    @Test
    void shouldValidatePhoneNotBlank() {
        // Given
        customer.setPhone("");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Phone number is required");
    }

    @Test
    void shouldValidatePhonePattern() {
        // Given
        customer.setPhone("+1234567890");

        // When
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Phone must be valid Indonesian number (+62)");
    }

    @Test
    void shouldSetTimestampsOnPrePersist() {
        // Given
        customer.setCreatedAt(null);
        customer.setUpdatedAt(null);

        // When
        customer.prePersist();

        // Then
        assertThat(customer.getCreatedAt()).isNotNull();
        assertThat(customer.getUpdatedAt()).isNotNull();
        assertThat(customer.getCreatedAt()).isEqualTo(customer.getUpdatedAt());
    }

    @Test
    void shouldUpdateTimestampOnPreUpdate() {
        // Given
        LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        
        customer.setCreatedAt(originalCreatedAt);
        customer.setUpdatedAt(originalUpdatedAt);

        // When
        customer.preUpdate();

        // Then
        assertThat(customer.getCreatedAt()).isEqualTo(originalCreatedAt); // Should not change
        assertThat(customer.getUpdatedAt()).isAfter(originalUpdatedAt); // Should be updated
    }

    @Test
    void shouldCreateCustomerWithConstructor() {
        // When
        Customer newCustomer = new Customer("Jane Doe", "jane@test.com", "password123", "+6281234567891", 30);

        // Then
        assertThat(newCustomer.getName()).isEqualTo("Jane Doe");
        assertThat(newCustomer.getEmail()).isEqualTo("jane@test.com");
        assertThat(newCustomer.getPassword()).isEqualTo("password123");
        assertThat(newCustomer.getPhone()).isEqualTo("+6281234567891");
        assertThat(newCustomer.getAge()).isEqualTo(30);
    }

    @Test
    void shouldHaveDefaultEmailVerifiedFalse() {
        // Given
        Customer newCustomer = new Customer();

        // Then
        assertThat(newCustomer.getEmailVerified()).isFalse();
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        customer.setId(1L);
        customer.setCreatedAt(now);
        customer.setUpdatedAt(now);
        customer.setEmailVerified(true);

        // Then
        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getCreatedAt()).isEqualTo(now);
        assertThat(customer.getUpdatedAt()).isEqualTo(now);
        assertThat(customer.getEmailVerified()).isTrue();
    }

    @Test
    void shouldAcceptValidNames() {
        String[] validNames = {
            "John Doe",
            "Jane Smith",
            "Maria Garcia",
            "Ahmed Al Rahman",
            "李小明",
            "José María"
        };

        for (String name : validNames) {
            customer.setName(name);
            Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void shouldAcceptValidEmails() {
        String[] validEmails = {
            "test@example.com",
            "user.name@domain.co.id",
            "user+tag@example.org",
            "123@domain.com"
        };

        for (String email : validEmails) {
            customer.setEmail(email);
            Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void shouldAcceptValidPhones() {
        String[] validPhones = {
            "+6281234567890",
            "+628123456789",
            "+6281234567890123"
        };

        for (String phone : validPhones) {
            customer.setPhone(phone);
            Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
            assertThat(violations).isEmpty();
        }
    }
}