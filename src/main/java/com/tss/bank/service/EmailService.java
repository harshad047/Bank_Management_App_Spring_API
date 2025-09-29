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
        String subject = "🎉 Account Approved - Bank Management System";
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #27ae60; margin: 0;">🎉 Congratulations!</h1>
                        <h2 style="color: #2c3e50; margin: 10px 0;">Your Account Has Been Approved</h2>
                    </div>
                    
                    <div style="background-color: #d5f4e6; padding: 20px; border-radius: 8px; border-left: 4px solid #27ae60; margin: 20px 0;">
                        <p style="margin: 0; font-size: 16px;"><strong>Dear %s,</strong></p>
                        <p style="margin: 10px 0 0 0;">We are pleased to inform you that your bank account application has been <strong style="color: #27ae60;">APPROVED</strong>.</p>
                    </div>
                    
                    <div style="margin: 25px 0;">
                        <h3 style="color: #34495e;">What's Next?</h3>
                        <ul style="color: #555; padding-left: 20px;">
                            <li>You can now log in to your account using your credentials</li>
                            <li>Access all banking services including transfers, deposits, and account management</li>
                            <li>Set up your security questions for enhanced protection</li>
                            <li>Explore our mobile banking features</li>
                        </ul>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <div style="background-color: #3498db; color: white; padding: 15px; border-radius: 5px; display: inline-block;">
                            <strong>🏦 Welcome to Bank Management System!</strong>
                        </div>
                    </div>
                    
                    <div style="border-top: 1px solid #ecf0f1; padding-top: 20px; text-align: center; color: #7f8c8d; font-size: 14px;">
                        <p>If you have any questions, please contact our customer support.</p>
                        <p style="margin: 10px 0 0 0;"><strong>Best regards,<br>Bank Management Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }

    public void sendUserRejectionEmail(String userEmail, String userName, String rejectionReason) {
        String subject = "❌ Account Application Status - Bank Management System";
        String htmlBody = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #e74c3c; margin: 0;">Account Application Update</h1>
                        <h2 style="color: #2c3e50; margin: 10px 0;">Application Status Notification</h2>
                    </div>
                    
                    <div style="background-color: #fadbd8; padding: 20px; border-radius: 8px; border-left: 4px solid #e74c3c; margin: 20px 0;">
                        <p style="margin: 0; font-size: 16px;"><strong>Dear %s,</strong></p>
                        <p style="margin: 10px 0 0 0;">We regret to inform you that your bank account application has been <strong style="color: #e74c3c;">REJECTED</strong>.</p>
                    </div>
                    
                    <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #34495e; margin-top: 0;">Rejection Reason:</h3>
                        <p style="color: #555; font-size: 15px; margin: 0;"><strong>%s</strong></p>
                    </div>
                    
                    <div style="margin: 25px 0;">
                        <h3 style="color: #34495e;">What Can You Do Next?</h3>
                        <ul style="color: #555; padding-left: 20px;">
                            <li>Review the rejection reason and address any issues</li>
                            <li>Contact our customer service team for clarification</li>
                            <li>Reapply with corrected information if applicable</li>
                            <li>Provide additional documentation if required</li>
                        </ul>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <div style="background-color: #f39c12; color: white; padding: 15px; border-radius: 5px; display: inline-block;">
                            <strong>📞 Need Help? Contact Customer Support</strong>
                        </div>
                    </div>
                    
                    <div style="border-top: 1px solid #ecf0f1; padding-top: 20px; text-align: center; color: #7f8c8d; font-size: 14px;">
                        <p>If you believe this decision was made in error or have additional information to provide, please contact our customer service team.</p>
                        <p>Thank you for your interest in our services.</p>
                        <p style="margin: 10px 0 0 0;"><strong>Best regards,<br>Bank Management Team</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, rejectionReason != null ? rejectionReason : "Not specified");
        
        sendHtmlEmail(userEmail, subject, htmlBody);
    }
}
