package com.reg.regis.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    void testCustomerCreation() {
        assertNotNull(customer);
        assertEquals(0, customer.getFailedLoginAttempts());
    }

    @Test
    void testSettersAndGetters() {
        customer.setNamaLengkap("John Doe");
        customer.setNik("1234567890123456");
        customer.setEmail("john@test.com");
        customer.setNomorTelepon("081234567890");
        
        assertEquals("John Doe", customer.getNamaLengkap());
        assertEquals("1234567890123456", customer.getNik());
        assertEquals("john@test.com", customer.getEmail());
        assertEquals("081234567890", customer.getNomorTelepon());
    }

    @Test
    void testPrePersist() {
        customer.prePersist();
        
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getUpdatedAt());
        assertEquals("Silver", customer.getJenisKartu());
    }

    @Test
    void testPreUpdate() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        customer.preUpdate();
        
        assertTrue(customer.getUpdatedAt().isAfter(before));
    }

    @Test
    void testIsAccountLocked_NotLocked() {
        assertFalse(customer.isAccountLocked());
    }

    @Test
    void testIsAccountLocked_Locked() {
        customer.setAccountLockedUntil(LocalDateTime.now().plusHours(1));
        assertTrue(customer.isAccountLocked());
    }

    @Test
    void testIsAccountLocked_ExpiredLock() {
        customer.setAccountLockedUntil(LocalDateTime.now().minusHours(1));
        assertFalse(customer.isAccountLocked());
    }

    @Test
    void testRelationships() {
        Alamat alamat = new Alamat();
        Wali wali = new Wali();
        
        customer.setAlamat(alamat);
        customer.setWali(wali);
        
        assertEquals(alamat, customer.getAlamat());
        assertEquals(wali, customer.getWali());
    }
}
