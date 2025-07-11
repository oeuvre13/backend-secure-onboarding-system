package com.reg.regis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.reg.regis.model.KtpDukcapil;

import java.util.Optional;

@Repository
public interface KtpDukcapilRepository extends JpaRepository<KtpDukcapil, Long> {
    
    Optional<KtpDukcapil> findByNik(String nik);
    
    boolean existsByNik(String nik);
    
    @Query("SELECT k FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama)")
    Optional<KtpDukcapil> findByNikAndNama(@Param("nik") String nik, @Param("nama") String nama);
    
    @Query("SELECT COUNT(k) FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama)")
    Long countByNikAndNama(@Param("nik") String nik, @Param("nama") String nama);
    
    @Query("SELECT k FROM KtpDukcapil k WHERE k.nik LIKE :nikPattern")
    Optional<KtpDukcapil> findByNikPattern(@Param("nikPattern") String nikPattern);
}
