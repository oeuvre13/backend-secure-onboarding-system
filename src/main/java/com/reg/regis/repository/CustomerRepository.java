package com.reg.regis.repository;

import com.reg.regis.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by email (case insensitive)
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByEmailIgnoreCase(@Param("email") String email);
    
    /**
     * Check if email already exists
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
    
    /**
     * Check if phone number already exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Find customer by email and email verified status
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email) AND c.emailVerified = :verified")
    Optional<Customer> findByEmailAndEmailVerified(@Param("email") String email, @Param("verified") Boolean verified);
    
    /**
     * Count total registered customers
     */
    @Query("SELECT COUNT(c) FROM Customer c")
    Long countTotalCustomers();
    
    /**
     * Count verified customers
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.emailVerified = true")
    Long countVerifiedCustomers();
}