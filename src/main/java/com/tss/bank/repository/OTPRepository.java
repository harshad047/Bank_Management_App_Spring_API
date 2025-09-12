package com.tss.bank.repository;

import com.tss.bank.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Integer> {
    
    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpCode = :otpCode AND o.otpType = :otpType AND o.isUsed = false AND o.expiresAt > :currentTime ORDER BY o.createdAt DESC")
    Optional<OTP> findValidOTP(@Param("email") String email, 
                              @Param("otpCode") String otpCode, 
                              @Param("otpType") OTP.OTPType otpType,
                              @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT o FROM OTP o WHERE o.email = :email AND o.otpType = :otpType AND o.isUsed = false ORDER BY o.createdAt DESC")
    Optional<OTP> findLatestUnusedOTP(@Param("email") String email, @Param("otpType") OTP.OTPType otpType);
    
    void deleteByEmailAndOtpType(String email, OTP.OTPType otpType);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
