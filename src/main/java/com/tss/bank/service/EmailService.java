package com.tss.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@bankmanagement.com}")
    private String fromEmail;

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }

    public void sendUserApprovalEmail(String userEmail, String userName) {
        String subject = "Account Approved - Bank Management System";
        String htmlBody = String.format("""
            <html>
            <body>
                <h2>Congratulations! Your Account Has Been Approved</h2>
                <p>Dear %s,</p>
                <p>We are pleased to inform you that your bank account application has been <strong>approved</strong>.</p>
                <p>You can now log in to your account and access all banking services.</p>
                <br>
                <p>Welcome to our Bank Management System!</p>
                <p>Best regards,<br>Bank Management Team</p>
            </body>
            </html>
            """, userName);
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }

    public void sendUserRejectionEmail(String userEmail, String userName, String rejectionReason) {
        String subject = "Account Application Status - Bank Management System";
        String htmlBody = String.format("""
            <html>
            <body>
                <h2>Account Application Update</h2>
                <p>Dear %s,</p>
                <p>We regret to inform you that your bank account application has been <strong>rejected</strong>.</p>
                <p><strong>Reason:</strong> %s</p>
                <p>If you believe this decision was made in error or if you have additional information to provide, 
                   please contact our customer service team.</p>
                <br>
                <p>Thank you for your interest in our services.</p>
                <p>Best regards,<br>Bank Management Team</p>
            </body>
            </html>
            """, userName, rejectionReason != null ? rejectionReason : "Not specified");
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }
}
