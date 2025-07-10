package com.reg.regis.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrationRequestTest {

    private Validator validator;
    private RegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRequest = new RegistrationRequest();
        validRequest.setName("John Doe");
        validRequest.setEmail("john@test.com");
        validRequest.setPassword("SecurePass123!");
        validRequest.setPhone("+6281234567890");
        validRequest.setAge(25);
    }

    @Test
    void shouldPassValidationForValidRequest() {
        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationForBlankName() {
        // Given
        validRequest.setName("");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name is required");
    }

    @Test
    void shouldFailValidationForNullName() {
        // Given
        validRequest.setName(null);

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name is required");
    }

    @Test
    void shouldFailValidationForShortName() {
        // Given
        validRequest.setName("A");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name must be between 2 and 100 characters");
    }

    @Test
    void shouldFailValidationForLongName() {
        // Given
        validRequest.setName("A".repeat(101));

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name must be between 2 and 100 characters");
    }

    @Test
    void shouldFailValidationForNameWithSpecialCharacters() {
        // Given
        validRequest.setName("John@Doe");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Name can only contain letters and spaces");
    }

    @Test
    void shouldPassValidationForNameWithSpaces() {
        // Given
        validRequest.setName("John Doe Smith");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationForInvalidEmail() {
        // Given
        validRequest.setEmail("invalid-email");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Please provide a valid email");
    }

    @Test
    void shouldFailValidationForBlankEmail() {
        // Given
        validRequest.setEmail("");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Email is required");
    }

    @Test
    void shouldFailValidationForShortPassword() {
        // Given
        validRequest.setPassword("Short1!");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Password must be at least 8 characters");
    }

    @Test
    void shouldFailValidationForPasswordWithoutUppercase() {
        // Given
        validRequest.setPassword("lowercase123!");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Password must contain uppercase, lowercase, number, and special character");
    }

    @Test
    void shouldFailValidationForPasswordWithoutLowercase() {
        // Given
        validRequest.setPassword("UPPERCASE123!");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Password must contain uppercase, lowercase, number, and special character");
    }

    @Test
    void shouldFailValidationForPasswordWithoutNumber() {
        // Given
        validRequest.setPassword("Password!");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Password must contain uppercase, lowercase, number, and special character");
    }

    @Test
    void shouldFailValidationForPasswordWithoutSpecialChar() {
        // Given
        validRequest.setPassword("Password123");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Password must contain uppercase, lowercase, number, and special character");
    }

    @Test
    void shouldFailValidationForInvalidPhoneFormat() {
        // Given
        validRequest.setPhone("+1234567890"); // Non-Indonesian

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Phone must be valid Indonesian number (+62)");
    }

    @Test
    void shouldFailValidationForPhoneWithoutCountryCode() {
        // Given
        validRequest.setPhone("081234567890");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Phone must be valid Indonesian number (+62)");
    }

    @Test
    void shouldPassValidationForValidIndonesianPhone() {
        // Given
        validRequest.setPhone("+6281234567890");

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationForUnderage() {
        // Given
        validRequest.setAge(16);

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Age must be at least 17");
    }

    @Test
    void shouldFailValidationForTooOld() {
        // Given
        validRequest.setAge(121);

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Age must be less than 120");
    }

    @Test
    void shouldFailValidationForNullAge() {
        // Given
        validRequest.setAge(null);

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("Age is required");
    }

    @Test
    void shouldPassValidationForBoundaryAge() {
        // Given
        validRequest.setAge(17); // Minimum age

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).isEmpty();

        // Test maximum age
        validRequest.setAge(120);
        violations = validator.validate(validRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldHandleMultipleValidationErrors() {
        // Given
        validRequest.setName(""); // Invalid
        validRequest.setEmail("invalid"); // Invalid
        validRequest.setPassword("weak"); // Invalid
        validRequest.setPhone("123"); // Invalid
        validRequest.setAge(10); // Invalid

        // When
        Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(validRequest);

        // Then
        assertThat(violations).hasSize(5);
    }
}