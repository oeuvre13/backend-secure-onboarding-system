// package com.reg.regis.service;

// import com.reg.regis.dto.RegistrationRequest;
// import com.reg.regis.model.Customer;
// import com.reg.regis.repository.CustomerRepository;
// import com.reg.regis.security.JwtUtil;
// import com.reg.regis.service.RegistrationService.RegistrationStats;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.crypto.password.PasswordEncoder;

// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.assertj.core.api.Assertions.assertThatThrownBy;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class RegistrationServiceTest {

//     @Mock
//     private CustomerRepository customerRepository;

//     @Mock
//     private PasswordEncoder passwordEncoder;

//     @Mock
//     private JwtUtil jwtUtil;

//     @InjectMocks
//     private RegistrationService registrationService;

//     private RegistrationRequest validRequest;
//     private Customer testCustomer;

//     @BeforeEach
//     void setUp() {
//         // Setup valid registration request
//         validRequest = new RegistrationRequest();
//         validRequest.setName("John Doe");
//         validRequest.setEmail("john@test.com");
//         validRequest.setPassword("SecurePass123!");
//         validRequest.setPhone("+6281234567890");
//         validRequest.setAge(25);

//         // Setup test customer
//         testCustomer = new Customer();
//         testCustomer.setId(1L);
//         testCustomer.setName("John Doe");
//         testCustomer.setEmail("john@test.com");
//         testCustomer.setPassword("hashedPassword123");
//         testCustomer.setPhone("+6281234567890");
//         testCustomer.setAge(25);
//         testCustomer.setEmailVerified(false);
//     }

//     // ================================
//     // REGISTRATION TESTS
//     // ================================

//     @Test
//     void shouldRegisterCustomerSuccessfully() {
//         // Given
//         when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//         when(customerRepository.existsByPhone(anyString())).thenReturn(false);
//         when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
//         when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

//         // When
//         Customer result = registrationService.registerCustomer(validRequest);

//         // Then
//         assertThat(result).isNotNull();
//         assertThat(result.getName()).isEqualTo("John Doe");
//         assertThat(result.getEmail()).isEqualTo("john@test.com");
//         assertThat(result.getEmailVerified()).isFalse();
        
//         // Verify interactions
//         verify(customerRepository).existsByEmailIgnoreCase("john@test.com");
//         verify(customerRepository).existsByPhone("+6281234567890");
//         verify(passwordEncoder).encode("SecurePass123!");
//         verify(customerRepository).save(any(Customer.class));
//     }

//     @Test
//     void shouldConvertEmailToLowerCaseWhenRegistering() {
//         // Given
//         validRequest.setEmail("JOHN@TEST.COM");
//         when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//         when(customerRepository.existsByPhone(anyString())).thenReturn(false);
//         when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
//         when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
//             Customer customer = invocation.getArgument(0);
//             customer.setId(1L);
//             return customer;
//         });

//         // When
//         Customer result = registrationService.registerCustomer(validRequest);

//         // Then
//         verify(customerRepository).save(argThat(customer -> 
//             customer.getEmail().equals("john@test.com")
//         ));
//     }

//     @Test
//     void shouldThrowExceptionWhenEmailAlreadyExists() {
//         // Given
//         when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(true);

//         // When & Then
//         assertThatThrownBy(() -> registrationService.registerCustomer(validRequest))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessage("Email already registered");

//         // Verify that save is never called
//         verify(customerRepository, never()).save(any(Customer.class));
//         verify(passwordEncoder, never()).encode(anyString());
//     }

//     @Test
//     void shouldThrowExceptionWhenPhoneAlreadyExists() {
//         // Given
//         when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//         when(customerRepository.existsByPhone(anyString())).thenReturn(true);

//         // When & Then
//         assertThatThrownBy(() -> registrationService.registerCustomer(validRequest))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessage("Phone number already registered");

//         // Verify that save is never called
//         verify(customerRepository, never()).save(any(Customer.class));
//         verify(passwordEncoder, never()).encode(anyString());
//     }

//     @Test
//     void shouldSetDefaultEmailVerifiedToFalse() {
//         // Given
//         when(customerRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
//         when(customerRepository.existsByPhone(anyString())).thenReturn(false);
//         when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword123");
//         when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

//         // When
//         registrationService.registerCustomer(validRequest);

//         // Then
//         verify(customerRepository).save(argThat(customer -> 
//             !customer.getEmailVerified()
//         ));
//     }

//     // ================================
//     // AUTHENTICATION TESTS
//     // ================================

//     @Test
//     void shouldAuthenticateCustomerSuccessfully() {
//         // Given
//         String email = "john@test.com";
//         String password = "SecurePass123!";
//         String expectedToken = "jwt.token.here";

//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testCustomer));
//         when(passwordEncoder.matches(password, testCustomer.getPassword())).thenReturn(true);
//         when(jwtUtil.generateToken(email)).thenReturn(expectedToken);

//         // When
//         String token = registrationService.authenticateCustomer(email, password);

//         // Then
//         assertThat(token).isEqualTo(expectedToken);
        
//         verify(customerRepository).findByEmailIgnoreCase(email);
//         verify(passwordEncoder).matches(password, testCustomer.getPassword());
//         verify(jwtUtil).generateToken(email);
//     }

//     @Test
//     void shouldThrowExceptionWhenEmailNotFound() {
//         // Given
//         String email = "notfound@test.com";
//         String password = "password";

//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

//         // When & Then
//         assertThatThrownBy(() -> registrationService.authenticateCustomer(email, password))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessage("Invalid email or password");

//         verify(passwordEncoder, never()).matches(anyString(), anyString());
//         verify(jwtUtil, never()).generateToken(anyString());
//     }

//     @Test
//     void shouldThrowExceptionWhenPasswordIncorrect() {
//         // Given
//         String email = "john@test.com";
//         String wrongPassword = "wrongPassword";

//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testCustomer));
//         when(passwordEncoder.matches(wrongPassword, testCustomer.getPassword())).thenReturn(false);

//         // When & Then
//         assertThatThrownBy(() -> registrationService.authenticateCustomer(email, wrongPassword))
//                 .isInstanceOf(RuntimeException.class)
//                 .hasMessage("Invalid email or password");

//         verify(jwtUtil, never()).generateToken(anyString());
//     }

//     @Test
//     void shouldHandleCaseInsensitiveEmailInAuthentication() {
//         // Given
//         String emailUpperCase = "JOHN@TEST.COM";
//         String password = "SecurePass123!";
//         String expectedToken = "jwt.token.here";

//         when(customerRepository.findByEmailIgnoreCase(emailUpperCase)).thenReturn(Optional.of(testCustomer));
//         when(passwordEncoder.matches(password, testCustomer.getPassword())).thenReturn(true);
//         when(jwtUtil.generateToken(emailUpperCase)).thenReturn(expectedToken);

//         // When
//         String token = registrationService.authenticateCustomer(emailUpperCase, password);

//         // Then
//         assertThat(token).isEqualTo(expectedToken);
//         verify(customerRepository).findByEmailIgnoreCase(emailUpperCase);
//     }

//     // ================================
//     // EMAIL VERIFICATION TESTS
//     // ================================

//     @Test
//     void shouldVerifyEmailSuccessfully() {
//         // Given
//         String email = "john@test.com";
//         Customer unverifiedCustomer = new Customer();
//         unverifiedCustomer.setEmail(email);
//         unverifiedCustomer.setEmailVerified(false);

//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(unverifiedCustomer));
//         when(customerRepository.save(any(Customer.class))).thenReturn(unverifiedCustomer);

//         // When
//         registrationService.verifyEmail(email);

//         // Then
//         verify(customerRepository).findByEmailIgnoreCase(email);
//         verify(customerRepository).save(argThat(customer -> customer.getEmailVerified()));
//     }

//     @Test
//     void shouldHandleVerifyEmailForNonExistentUser() {
//         // Given
//         String email = "notfound@test.com";
//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

//         // When
//         registrationService.verifyEmail(email);

//         // Then
//         verify(customerRepository).findByEmailIgnoreCase(email);
//         verify(customerRepository, never()).save(any(Customer.class));
//     }

//     @Test
//     void shouldHandleVerifyEmailForAlreadyVerifiedUser() {
//         // Given
//         String email = "john@test.com";
//         Customer verifiedCustomer = new Customer();
//         verifiedCustomer.setEmail(email);
//         verifiedCustomer.setEmailVerified(true);

//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(verifiedCustomer));
//         when(customerRepository.save(any(Customer.class))).thenReturn(verifiedCustomer);

//         // When
//         registrationService.verifyEmail(email);

//         // Then
//         verify(customerRepository).save(argThat(customer -> customer.getEmailVerified()));
//     }

//     // ================================
//     // PASSWORD STRENGTH TESTS
//     // ================================

//     @Test
//     void shouldReturnWeakForShortPassword() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("123")).isEqualTo("weak");
//         assertThat(registrationService.checkPasswordStrength("weak")).isEqualTo("weak");
//         assertThat(registrationService.checkPasswordStrength("password")).isEqualTo("weak");
//     }

//     @Test
//     void shouldReturnWeakForPasswordWithOnlyTwoCriteria() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("Password")).isEqualTo("weak"); // Only length + cases
//         assertThat(registrationService.checkPasswordStrength("password123")).isEqualTo("weak"); // Only length + lower + number
//     }

//     @Test
//     void shouldReturnMediumForPasswordWithThreeOrFourCriteria() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("Password123")).isEqualTo("medium"); // length + cases + number
//         assertThat(registrationService.checkPasswordStrength("Password!")).isEqualTo("medium"); // length + cases + special
//         assertThat(registrationService.checkPasswordStrength("password123!")).isEqualTo("medium"); // length + lower + number + special
//     }

//     @Test
//     void shouldReturnStrongForPasswordWithAllCriteria() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("Password123!")).isEqualTo("strong");
//         assertThat(registrationService.checkPasswordStrength("MySecure123@")).isEqualTo("strong");
//         assertThat(registrationService.checkPasswordStrength("ComplexPass456#")).isEqualTo("strong");
//     }

//     @Test
//     void shouldHandlePasswordWithSpecialCharacters() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("Pass123@$!%*?&")).isEqualTo("strong");
//         assertThat(registrationService.checkPasswordStrength("Test456#")).isEqualTo("strong");
//     }

//     @Test
//     void shouldHandleEmptyAndNullPasswords() {
//         // When & Then
//         assertThat(registrationService.checkPasswordStrength("")).isEqualTo("weak");
//         assertThat(registrationService.checkPasswordStrength(null)).isEqualTo("weak");
//     }

//     // ================================
//     // JWT TOKEN UTILITY TESTS
//     // ================================

//     @Test
//     void shouldGetEmailFromValidToken() {
//         // Given
//         String token = "valid.jwt.token";
//         String expectedEmail = "john@test.com";
//         when(jwtUtil.getEmailFromToken(token)).thenReturn(expectedEmail);

//         // When
//         String email = registrationService.getEmailFromToken(token);

//         // Then
//         assertThat(email).isEqualTo(expectedEmail);
//         verify(jwtUtil).getEmailFromToken(token);
//     }

//     @Test
//     void shouldReturnNullForInvalidToken() {
//         // Given
//         String invalidToken = "invalid.token";
//         when(jwtUtil.getEmailFromToken(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

//         // When
//         String email = registrationService.getEmailFromToken(invalidToken);

//         // Then
//         assertThat(email).isNull();
//         verify(jwtUtil).getEmailFromToken(invalidToken);
//     }

//     @Test
//     void shouldValidateTokenSuccessfully() {
//         // Given
//         String token = "valid.jwt.token";
//         when(jwtUtil.validateToken(token)).thenReturn(true);

//         // When
//         boolean isValid = registrationService.validateToken(token);

//         // Then
//         assertThat(isValid).isTrue();
//         verify(jwtUtil).validateToken(token);
//     }

//     @Test
//     void shouldReturnFalseForInvalidToken() {
//         // Given
//         String invalidToken = "invalid.token";
//         when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

//         // When
//         boolean isValid = registrationService.validateToken(invalidToken);

//         // Then
//         assertThat(isValid).isFalse();
//         verify(jwtUtil).validateToken(invalidToken);
//     }

//     // ================================
//     // CUSTOMER RETRIEVAL TESTS
//     // ================================

//     @Test
//     void shouldGetCustomerByEmailSuccessfully() {
//         // Given
//         String email = "john@test.com";
//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testCustomer));

//         // When
//         Optional<Customer> result = registrationService.getCustomerByEmail(email);

//         // Then
//         assertThat(result).isPresent();
//         assertThat(result.get().getEmail()).isEqualTo(email);
//         verify(customerRepository).findByEmailIgnoreCase(email);
//     }

//     @Test
//     void shouldReturnEmptyForNonExistentEmail() {
//         // Given
//         String email = "notfound@test.com";
//         when(customerRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

//         // When
//         Optional<Customer> result = registrationService.getCustomerByEmail(email);

//         // Then
//         assertThat(result).isEmpty();
//         verify(customerRepository).findByEmailIgnoreCase(email);
//     }

//     // ================================
//     // REGISTRATION STATISTICS TESTS
//     // ================================

//     @Test
//     void shouldCalculateRegistrationStatsCorrectly() {
//         // Given
//         when(customerRepository.countTotalCustomers()).thenReturn(10L);
//         when(customerRepository.countVerifiedCustomers()).thenReturn(7L);

//         // When
//         RegistrationStats stats = registrationService.getRegistrationStats();

//         // Then
//         assertThat(stats.getTotalCustomers()).isEqualTo(10L);
//         assertThat(stats.getVerifiedCustomers()).isEqualTo(7L);
//         assertThat(stats.getVerificationRate()).isEqualTo(70.0);

//         verify(customerRepository).countTotalCustomers();
//         verify(customerRepository).countVerifiedCustomers();
//     }

//     @Test
//     void shouldHandleZeroCustomersInStats() {
//         // Given
//         when(customerRepository.countTotalCustomers()).thenReturn(0L);
//         when(customerRepository.countVerifiedCustomers()).thenReturn(0L);

//         // When
//         RegistrationStats stats = registrationService.getRegistrationStats();

//         // Then
//         assertThat(stats.getTotalCustomers()).isEqualTo(0L);
//         assertThat(stats.getVerifiedCustomers()).isEqualTo(0L);
//         assertThat(stats.getVerificationRate()).isEqualTo(0.0);
//     }

//     @Test
//     void shouldHandleAllVerifiedCustomers() {
//         // Given
//         when(customerRepository.countTotalCustomers()).thenReturn(5L);
//         when(customerRepository.countVerifiedCustomers()).thenReturn(5L);

//         // When
//         RegistrationStats stats = registrationService.getRegistrationStats();

//         // Then
//         assertThat(stats.getTotalCustomers()).isEqualTo(5L);
//         assertThat(stats.getVerifiedCustomers()).isEqualTo(5L);
//         assertThat(stats.getVerificationRate()).isEqualTo(100.0);
//     }

//     @Test
//     void shouldHandleNoVerifiedCustomers() {
//         // Given
//         when(customerRepository.countTotalCustomers()).thenReturn(10L);
//         when(customerRepository.countVerifiedCustomers()).thenReturn(0L);

//         // When
//         RegistrationStats stats = registrationService.getRegistrationStats();

//         // Then
//         assertThat(stats.getTotalCustomers()).isEqualTo(10L);
//         assertThat(stats.getVerifiedCustomers()).isEqualTo(0L);
//         assertThat(stats.getVerificationRate()).isEqualTo(0.0);
//     }

//     @Test
//     void shouldCalculatePartialVerificationRate() {
//         // Given
//         when(customerRepository.countTotalCustomers()).thenReturn(3L);
//         when(customerRepository.countVerifiedCustomers()).thenReturn(1L);

//         // When
//         RegistrationStats stats = registrationService.getRegistrationStats();

//         // Then
//         assertThat(stats.getTotalCustomers()).isEqualTo(3L);
//         assertThat(stats.getVerifiedCustomers()).isEqualTo(1L);
//         assertThat(stats.getVerificationRate()).isEqualTo(33.333333333333336); // 1/3 * 100
//     }

//     // ================================
//     // CUSTOMER REPOSITORY ACCESS TESTS
//     // ================================

//     @Test
//     void shouldProvideAccessToCustomerRepository() {
//         // When
//         CustomerRepository repository = registrationService.getCustomerRepository();

//         // Then
//         assertThat(repository).isEqualTo(customerRepository);
//     }

//     // ================================
//     // EDGE CASE TESTS
//     // ================================

//     @Test
//     void shouldHandleNullTokenInGetEmailFromToken() {
//         // Given
//         when(jwtUtil.getEmailFromToken(null)).thenThrow(new RuntimeException("Null token"));

//         // When
//         String email = registrationService.getEmailFromToken(null);

//         // Then
//         assertThat(email).isNull();
//     }

//     @Test
//     void shouldHandleEmptyTokenInGetEmailFromToken() {
//         // Given
//         when(jwtUtil.getEmailFromToken("")).thenThrow(new RuntimeException("Empty token"));

//         // When
//         String email = registrationService.getEmailFromToken("");

//         // Then
//         assertThat(email).isNull();
//     }

//     @Test
//     void shouldHandleSpecialCharactersInEmail() {
//         // Given
//         String specialEmail = "test+special@example-domain.com";
//         when(customerRepository.findByEmailIgnoreCase(specialEmail)).thenReturn(Optional.of(testCustomer));

//         // When
//         Optional<Customer> result = registrationService.getCustomerByEmail(specialEmail);

//         // Then
//         assertThat(result).isPresent();
//         verify(customerRepository).findByEmailIgnoreCase(specialEmail);
//     }
// }