// package com.reg.regis.controller;

// import com.reg.regis.service.RegistrationService;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.http.MediaType;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.mockito.Mockito.*;
// import java.util.Map;
// import java.util.Optional;
// import com.reg.regis.model.Customer;

// @WebMvcTest(RegistrationController.class)
// class RegistrationControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private RegistrationService registrationService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Test
//     void testHealthCheck() throws Exception {
//         RegistrationService.RegistrationStats mockStats = 
//             new RegistrationService.RegistrationStats(100, 80, 80.0, true, "http://dukcapil.test");
        
//         when(registrationService.getRegistrationStats()).thenReturn(mockStats);
        
//         mockMvc.perform(get("/auth/health"))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.status").value("OK"))
//             .andExpect(jsonPath("$.dukcapilService.available").value(true));
//     }

//     @Test
//     void testCheckPasswordStrength() throws Exception {
//         Map<String, String> request = Map.of("password", "Test123@");
        
//         when(registrationService.checkPasswordStrength("Test123@"))
//             .thenReturn("sedang");
        
//         mockMvc.perform(post("/auth/check-password")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.strength").value("sedang"));
//     }

//     @Test
//     void testValidateNik_ValidNik() throws Exception {
//         Map<String, String> request = Map.of("nik", "1234567890123456");
        
//         when(registrationService.validateNikFormat("1234567890123456"))
//             .thenReturn(true);
//         when(registrationService.getCustomerByNik("1234567890123456"))
//             .thenReturn(Optional.empty());
        
//         mockMvc.perform(post("/auth/validate-nik")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.valid").value(true))
//             .andExpect(jsonPath("$.exists").value(false));
//     }

//     @Test
//     void testVerifyEmail() throws Exception {
//         Map<String, String> request = Map.of("email", "test@example.com");
        
//         doNothing().when(registrationService).verifyEmail("test@example.com");
        
//         mockMvc.perform(post("/auth/verify-email")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(request)))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.message").value("Email berhasil diverifikasi"));
//     }

//     @Test
//     void testGetStats() throws Exception {
//         RegistrationService.RegistrationStats mockStats = 
//             new RegistrationService.RegistrationStats(50, 40, 80.0, true, "http://dukcapil.test");
        
//         when(registrationService.getRegistrationStats()).thenReturn(mockStats);
        
//         mockMvc.perform(get("/auth/stats"))
//             .andExpect(status().isOk());
        
//         verify(registrationService).getRegistrationStats();
//     }

//     @Test
//     void testGetProfile_MissingToken() throws Exception {
//         mockMvc.perform(get("/auth/profile"))
//             .andExpect(status().isUnauthorized())
//             .andExpect(jsonPath("$.error").value("Token tidak ditemukan"));
//     }
// }