package com.tss.bank.service;

import com.tss.bank.entity.OTP;
import com.tss.bank.entity.User;
import com.tss.bank.exception.UserApiException;
import com.tss.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class EmailVerificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    public boolean verifyEmailWithOTP(String email, String otpCode) {
        // Verify OTP
        boolean isOTPValid = otpService.verifyOTP(email, otpCode, OTP.OTPType.EMAIL_VERIFICATION);
        
        if (!isOTPValid) {
            return false;
        }

        // Find user by email and update verification status
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Update email verification status
            user.setEmailVerified(true);
            user.setEmailVerifiedAt(Date.valueOf(LocalDate.now()));
            user.setStatus(User.Status.PENDING); // Move to pending for admin approval
            
            userRepository.save(user);
            return true;
        }
        
        throw new UserApiException("User not found with email: " + email);
    }

    public void resendVerificationOTP(String email) {
        // Check if user exists and is in EMAIL_UNVERIFIED status
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserApiException("User not found with email: " + email);
        }
        
        User user = userOptional.get();
        if (user.getStatus() != User.Status.EMAIL_UNVERIFIED) {
            throw new UserApiException("Email verification not required for this user");
        }
        
        // Generate and send new OTP
        otpService.generateAndSendOTP(email, OTP.OTPType.EMAIL_VERIFICATION);
    }
}
