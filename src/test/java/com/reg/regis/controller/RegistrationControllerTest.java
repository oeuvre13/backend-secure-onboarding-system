// package com.reg.regis.controller;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.reg.regis.dto.RegistrationRequest;
// import com.reg.regis.model.Customer;
// import com.reg.regis.service.RegistrationService;
// import com.reg.regis.service.RegistrationService.RegistrationStats;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Optional;

// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest(RegistrationController.class)
// @ActiveProfiles("test")
// class RegistrationControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private RegistrationService registrationService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private RegistrationRequest validRequest;
//     private Customer testCustomer;

//     @BeforeEach
//     void setUp() {
//         validRequest = new RegistrationRequest();
//         validRequest.setName("John Doe");
//         validRequest.setEmail("john@test.com");
//         validRequest.setPassword("SecurePass123!");
//         validRequest.setPhone("+6281234567890");
//         validRequest.setAge(25);

//         testCustomer = new Customer();
//         testCustomer.setId(1L);
//         testCustomer.setName("John Doe");
//         testCustomer.setEmail("john@test.com");
//         testCustomer.setPhone("+6281234567890");
//         testCustomer.setAge(25);
//         testCustomer.setEmailVerified(false);
//     }

//     @Test
//     void shouldRegisterCustomerSuccessfully() throws Exception {
//         // Given
//         when(registrationService.registerCustomer(any(RegistrationRequest.class))).thenReturn(testCustomer);
//         when(registrationService.authenticateCustomer(anyString(), anyString())).thenReturn("jwt.token.here");

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Registration successful"))
//                 .andExpect(jsonPath("$.customer.name").value("John Doe"))
//                 .andExpected(jsonPath("$.customer.email").value("john@test.com"))
//                 .andExpect(jsonPath("$.customer.emailVerified").value(false));
//     }

//     @Test
//     void shouldReturnBadRequestForInvalidEmail() throws Exception {
//         // Given
//         validRequest.setEmail("invalid-email");

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void shouldReturnBadRequestForWeakPassword() throws Exception {
//         // Given
//         validRequest.setPassword("weak");

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void shouldReturnBadRequestForInvalidPhone() throws Exception {
//         // Given
//         validRequest.setPhone("+1234567890"); // Non-Indonesian phone

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void shouldReturnBadRequestForUnderage() throws Exception {
//         // Given
//         validRequest.setAge(16);

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void shouldHandleRegistrationError() throws Exception {
//         // Given
//         when(registrationService.registerCustomer(any(RegistrationRequest.class)))
//                 .thenThrow(new RuntimeException("Email already registered"));

//         // When & Then
//         mockMvc.perform(post("/auth/register")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validRequest)))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error").value("Email already registered"));
//     }

//     @Test
//     void shouldLoginSuccessfully() throws Exception {
//         // Given
//         when(registrationService.authenticateCustomer("john@test.com", "SecurePass123!"))
//                 .thenReturn("jwt.token.here");
//         when(registrationService.getCustomerByEmail("john@test.com"))
//                 .thenReturn(Optional.of(testCustomer));

//         // When & Then
//         mockMvc.perform(post("/auth/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"email\":\"john@test.com\",\"password\":\"SecurePass123!\"}"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Login successful"))
//                 .andExpect(jsonPath("$.customer.name").value("John Doe"));
//     }

//     @Test
//     void shouldHandleLoginError() throws Exception {
//         // Given
//         when(registrationService.authenticateCustomer(anyString(), anyString()))
//                 .thenThrow(new RuntimeException("Invalid email or password"));

//         // When & Then
//         mockMvc.perform(post("/auth/login")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"email\":\"wrong@test.com\",\"password\":\"wrongpass\"}"))
//                 .andExpect(status().isBadRequest())
//                 .andExpect(jsonPath("$.error").value("Invalid email or password"));
//     }

//     @Test
//     void shouldCheckPasswordStrength() throws Exception {
//         // Given
//         when(registrationService.checkPasswordStrength("SecurePass123!")).thenReturn("strong");

//         // When & Then
//         mockMvc.perform(post("/auth/check-password")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"password\":\"SecurePass123!\"}"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.strength").value("strong"));
//     }

//     @Test
//     void shouldVerifyEmail() throws Exception {
//         // When & Then
//         mockMvc.perform(post("/auth/verify-email")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content("{\"email\":\"john@test.com\"}"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Email verified successfully"));
//     }

//     @Test
//     void shouldGetRegistrationStats() throws Exception {
//         // Given
//         RegistrationStats stats = new RegistrationStats(10L, 7L, 70.0);
//         when(registrationService.getRegistrationStats()).thenReturn(stats);

//         // When & Then
//         mockMvc.perform(get("/auth/stats"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.totalCustomers").value(10))
//                 .andExpect(jsonPath("$.verifiedCustomers").value(7))
//                 .andExpect(jsonPath("$.verificationRate").value(70.0));
//     }

//     @Test
//     void shouldReturnHealthStatus() throws Exception {
//         // When & Then
//         mockMvc.perform(get("/auth/health"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.status").value("OK"))
//                 .andExpect(jsonPath("$.service").value("Secure Customer Registration"))
//                 .andExpect(jsonPath("$.timestamp").exists());
//     }

//     @Test
//     void shouldHandleGetCurrentUser() throws Exception {
//         // Given
//         when(registrationService.getEmailFromToken("valid.jwt.token")).thenReturn("john@test.com");
//         when(registrationService.getCustomerByEmail("john@test.com")).thenReturn(Optional.of(testCustomer));

//         // When & Then
//         mockMvc.perform(get("/auth/me")
//                 .cookie(new jakarta.servlet.http.Cookie("authToken", "valid.jwt.token")))
//                 .andExpect(status().isOk())
//                 .andExpected(jsonPath("$.authenticated").value(true))
//                 .andExpect(jsonPath("$.customer.name").value("John Doe"));
//     }

//     @Test
//     void shouldReturnUnauthorizedForInvalidToken() throws Exception {
//         // When & Then
//         mockMvc.perform(get("/auth/me")
//                 .cookie(new jakarta.servlet.http.Cookie("authToken", "invalid.token")))
//                 .andExpect(status().isUnauthorized())
//                 .andExpect(jsonPath("$.error").value("Authentication failed"));
//     }

//     @Test
//     void shouldLogoutSuccessfully() throws Exception {
//         // When & Then
//         mockMvc.perform(post("/auth/logout"))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.message").value("Logged out successfully"));
//     }
// }