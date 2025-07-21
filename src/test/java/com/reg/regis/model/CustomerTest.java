// package com.reg.regis.model;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.BeforeEach;
// import static org.junit.jupiter.api.Assertions.*;
// import java.time.LocalDate;
// import java.time.LocalDateTime;

// class CustomerTest {

//     private Customer customer;

//     @BeforeEach
//     void setUp() {
//         customer = new Customer();
//     }

//     @Test
//     void testPrePersist() {
//         // Test default jenisKartu dan timestamp
//         customer.prePersist();
        
//         assertNotNull(customer.getCreatedAt());
//         assertNotNull(customer.getUpdatedAt());
//         assertEquals("Silver", customer.getJenisKartu());
//     }

//     @Test
//     void testPreUpdate() {
//         LocalDateTime oldTime = LocalDateTime.now().minusHours(1);
//         customer.setUpdatedAt(oldTime);
        
//         customer.preUpdate();
        
//         assertTrue(customer.getUpdatedAt().isAfter(oldTime));
//     }

//     @Test
//     void testCustomerSettersAndGetters() {
//         // Test basic fields
//         customer.setNamaLengkap("John Doe");
//         customer.setNik("1234567890123456");
//         customer.setEmail("john@example.com");
//         customer.setTanggalLahir(LocalDate.of(1990, 5, 15));
        
//         assertEquals("John Doe", customer.getNamaLengkap());
//         assertEquals("1234567890123456", customer.getNik());
//         assertEquals("john@example.com", customer.getEmail());
//         assertEquals(LocalDate.of(1990, 5, 15), customer.getTanggalLahir());
//     }

//     @Test
//     void testEmailVerifiedDefault() {
//         // Default emailVerified should be false
//         assertFalse(customer.getEmailVerified());
        
//         customer.setEmailVerified(true);
//         assertTrue(customer.getEmailVerified());
//     }

//     @Test
//     void testJenisKartuDefault() {
//         customer.setJenisKartu(null);
//         customer.prePersist();
        
//         assertEquals("Silver", customer.getJenisKartu());
//     }
// }