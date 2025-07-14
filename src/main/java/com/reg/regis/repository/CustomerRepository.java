package com.reg.regis.repository;

import com.reg.regis.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByEmailIgnoreCase(@Param("email") String email);
    
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    boolean existsByNomorTelepon(String nomorTelepon);
    
    // NIK related methods untuk DevSecOps
    @Query("SELECT c FROM Customer c WHERE c.nik = :nik")
    Optional<Customer> findByNik(@Param("nik") String nik);
    
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.nik = :nik")
    boolean existsByNik(@Param("nik") String nik);
    
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email) AND c.emailVerified = :verified")
    Optional<Customer> findByEmailAndEmailVerified(@Param("email") String email, @Param("verified") Boolean verified);
    
    @Query("SELECT COUNT(c) FROM Customer c")
    Long countTotalCustomers();
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.emailVerified = true")
    Long countVerifiedCustomers();
    
    // Security queries untuk audit trail
    @Query("SELECT c FROM Customer c WHERE c.nik = :nik AND LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByNikAndEmail(@Param("nik") String nik, @Param("email") String email);
    
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= CURRENT_DATE")
    Long countTodayRegistrations();
}