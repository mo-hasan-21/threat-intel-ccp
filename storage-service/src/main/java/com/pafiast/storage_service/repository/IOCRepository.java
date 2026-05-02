package com.pafiast.storage_service.repository;

import com.pafiast.storage_service.entity.IOC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOCRepository extends JpaRepository<IOC, Long> {
    
    Optional<IOC> findByIocValue(String iocValue);
    
    Optional<IOC> findByIocValueAndIocType(String iocValue, String iocType);
    
    List<IOC> findByIocType(String iocType);
    
    List<IOC> findBySeverityScoreGreaterThan(int score);
    
    @Query("SELECT i FROM IOC i WHERE i.iocValue LIKE %:keyword% OR i.source LIKE %:keyword%")
    List<IOC> searchIOCs(@Param("keyword") String keyword);
    
    @Query("SELECT AVG(i.severityScore) FROM IOC i")
    Double getAverageSeverityScore();
    
    @Query("SELECT COUNT(i) FROM IOC i WHERE i.iocType = :type")
    Long countByIocType(@Param("type") String type);
}