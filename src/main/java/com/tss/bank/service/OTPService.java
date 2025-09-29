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
        // Rate limiting check
        if (hasRecentOTP(email, otpType)) {
            throw new RuntimeException("Please wait before requesting another OTP. Rate limit exceeded.");
        }
        
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
        emailService.sendHtmlEmail(email, subject, message);
        
        return otpCode; // Return for testing purposes only
    }

    private boolean hasRecentOTP(String email, OTP.OTPType otpType) {
        Optional<OTP> recentOTP = otpRepository.findLatestUnusedOTP(email, otpType);
        if (recentOTP.isPresent()) {
            LocalDateTime createdAt = recentOTP.get().getCreatedAt();
            return createdAt.isAfter(LocalDateTime.now().minusMinutes(1)); // 1 minute rate limit
        }
        return false;
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
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;">
                    <h2 style="color: #2c3e50; text-align: center;">Bank Management System</h2>
                    <h3 style="color: #34495e;">OTP Verification Required</h3>
                    
                    <p>Dear User,</p>
                    
                    <p>Your OTP to <strong>%s</strong> is:</p>
                    
                    <div style="text-align: center; margin: 20px 0;">
                        <span style="font-size: 24px; font-weight: bold; color: #e74c3c; background-color: #f8f9fa; padding: 10px 20px; border-radius: 5px; letter-spacing: 3px;">%s</span>
                    </div>
                    
                    <p style="color: #e67e22;"><strong>⏰ This OTP will expire in %d minutes.</strong></p>
                    
                    <p style="color: #7f8c8d; font-size: 14px;">
                        If you did not request this OTP, please ignore this email and contact our support team immediately.
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #ecf0f1; margin: 20px 0;">
                    
                    <p style="text-align: center; color: #95a5a6; font-size: 12px;">
                        Best regards,<br>
                        <strong>Bank Management System Team</strong>
                    </p>
                </div>
            </body>
            </html>
            """, purpose, otpCode, OTP_EXPIRY_MINUTES);
    }
}
