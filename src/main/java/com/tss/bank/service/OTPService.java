package com.tss.bank.service;

import com.tss.bank.entity.OTP;
import com.tss.bank.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom random = new SecureRandom();

    public String generateAndSendOTP(String email, OTP.OTPType otpType) {
        // Generate 6-digit OTP
        String otpCode = String.format("%06d", random.nextInt(1000000));
        
        // Delete any existing unused OTPs for this email and type
        otpRepository.deleteByEmailAndOtpType(email, otpType);
        
        // Create new OTP
        OTP otp = OTP.builder()
                .otpCode(otpCode)
                .email(email)
                .otpType(otpType)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .isUsed(false)
                .isVerified(false)
                .build();
        
        otpRepository.save(otp);
        
        // Send OTP via email
        String subject = getOTPEmailSubject(otpType);
        String message = getOTPEmailMessage(otpCode, otpType);
        emailService.sendSimpleEmail(email, subject, message);
        
        return otpCode; // Return for testing purposes only
    }

    public boolean verifyOTP(String email, String otpCode, OTP.OTPType otpType) {
        Optional<OTP> otpOptional = otpRepository.findValidOTP(
            email, otpCode, otpType, LocalDateTime.now()
        );
        
        if (otpOptional.isPresent()) {
            OTP otp = otpOptional.get();
            otp.setIsUsed(true);
            otp.setIsVerified(true);
            otpRepository.save(otp);
            return true;
        }
        
        return false;
    }

    public boolean isOTPValid(String email, String otpCode, OTP.OTPType otpType) {
        return otpRepository.findValidOTP(
            email, otpCode, otpType, LocalDateTime.now()
        ).isPresent();
    }

    @Transactional
    public void cleanupExpiredOTPs() {
        otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String getOTPEmailSubject(OTP.OTPType otpType) {
        return switch (otpType) {
            case LOGIN -> "Your Login OTP - Bank Management System";
            case PASSWORD_RESET -> "Password Reset OTP - Bank Management System";
            case EMAIL_VERIFICATION -> "Email Verification OTP - Bank Management System";
        };
    }

    private String getOTPEmailMessage(String otpCode, OTP.OTPType otpType) {
        String purpose = switch (otpType) {
            case LOGIN -> "complete your login";
            case PASSWORD_RESET -> "reset your password";
            case EMAIL_VERIFICATION -> "verify your email address";
        };

        return String.format("""
            Dear User,
            
            Your OTP to %s is: %s
            
            This OTP will expire in %d minutes.
            
            If you did not request this OTP, please ignore this email.
            
            Best regards,
            Bank Management System Team
            """, purpose, otpCode, OTP_EXPIRY_MINUTES);
    }
}
