# Bank Management System - Security Enhancement Guide

## 🔐 JWT Security & OTP Authentication Implementation

This document outlines the comprehensive security enhancements implemented in the Bank Management System, including JWT user-specific authorization, OTP verification, and email notifications.

## 🚀 Features Implemented

### 1. **Enhanced JWT Security**
- ✅ **User-specific JWT tokens** with userId claims
- ✅ **Cross-user access prevention** - tokens are bound to specific users
- ✅ **Proper authorization validation** in all controllers
- ✅ **Admin privilege separation** - admins retain full access

### 2. **OTP Authentication**
- ✅ **Two-factor authentication** during login
- ✅ **6-digit OTP generation** with 5-minute expiry
- ✅ **Email-based OTP delivery**
- ✅ **Secure OTP validation** and cleanup

### 3. **Email Notifications**
- ✅ **User approval notifications** - automatic emails when admin approves users
- ✅ **User rejection notifications** - automatic emails with rejection reasons
- ✅ **HTML email templates** for professional communication

## 🔧 API Endpoints

### Authentication Flow

#### Step 1: Initial Login (Username/Password)
```http
POST /api/v1/auth/login-step1
Content-Type: application/json

{
    "username": "user123",
    "password": "password123"
}
```

**Response:**
```json
{
    "success": true,
    "message": "OTP sent successfully",
    "data": {
        "message": "OTP sent to your registered email",
        "email": "u***@example.com",
        "nextStep": "verify-otp"
    }
}
```

#### Step 2: OTP Verification
```http
POST /api/v1/auth/login-step2
Content-Type: application/json

{
    "email": "user@example.com",
    "otpCode": "123456"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "username": "user123",
        "role": "USER",
        "userId": 1,
        "userType": "USER"
    }
}
```

### Legacy Login (Backward Compatibility)
```http
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "user123",
    "password": "password123"
}
```
*Note: This endpoint is deprecated and will be removed in future versions.*

## 🛡️ Security Features

### JWT Token Structure
```json
{
    "sub": "username",
    "role": "USER|ADMIN|SUPER_ADMIN",
    "userId": 123,
    "iat": 1641234567,
    "exp": 1641320967
}
```

### Authorization Validation
- **User-specific endpoints**: Validates that users can only access their own data
- **Account-specific endpoints**: Validates account ownership before operations
- **Admin endpoints**: Restricted to ADMIN/SUPER_ADMIN roles only

### OTP Security
- **6-digit random codes** generated using SecureRandom
- **5-minute expiry** for enhanced security
- **One-time use** - OTPs are invalidated after successful verification
- **Email masking** - partial email shown for privacy (u***@example.com)

## 📧 Email Configuration

### Setup Gmail SMTP (Recommended)
1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account Settings → Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. **Update application.properties**:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-digit-app-password
```

### Email Templates
- **Approval Email**: Professional HTML template with congratulations message
- **Rejection Email**: Detailed rejection with reason and contact information
- **OTP Email**: Clear formatting with expiry information

## 🔒 Security Improvements

### Before Enhancement
- ❌ JWT tokens could access any user's data
- ❌ No OTP verification
- ❌ No email notifications
- ❌ Cross-user access vulnerability

### After Enhancement
- ✅ JWT tokens bound to specific users
- ✅ Two-factor authentication with OTP
- ✅ Automatic email notifications
- ✅ Comprehensive authorization validation
- ✅ Admin privilege separation

## 🚦 Testing the Implementation

### 1. Test User Registration & Approval
```bash
# 1. Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User"
  }'

# 2. Admin approves user (triggers approval email)
curl -X PUT http://localhost:8080/api/v1/admin/users/1/approve \
  -H "Authorization: Bearer <admin-token>"
```

### 2. Test OTP Login Flow
```bash
# 1. Initial login (triggers OTP email)
curl -X POST http://localhost:8080/api/v1/auth/login-step1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 2. Verify OTP (check email for code)
curl -X POST http://localhost:8080/api/v1/auth/login-step2 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otpCode": "123456"
  }'
```

### 3. Test Authorization Security
```bash
# Try accessing another user's data (should fail)
curl -X GET http://localhost:8080/api/v1/users/2/profile \
  -H "Authorization: Bearer <user1-token>"
```

## 📋 Database Schema Updates

### New OTP Table
```sql
CREATE TABLE otps (
    otp_id INT AUTO_INCREMENT PRIMARY KEY,
    otp_code VARCHAR(6) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    otp_type ENUM('LOGIN', 'PASSWORD_RESET', 'EMAIL_VERIFICATION') NOT NULL
);
```

## 🔧 Configuration Requirements

### Required Dependencies (Already Added)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

### Environment Variables
```properties
# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400

# Email Configuration
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## 🎯 Benefits Achieved

1. **Enhanced Security**: Users cannot access other users' data
2. **Two-Factor Authentication**: OTP verification adds extra security layer
3. **Professional Communication**: Automated email notifications improve user experience
4. **Audit Trail**: All approval/rejection actions are logged with admin ID and timestamp
5. **Scalable Architecture**: Centralized authorization service for easy maintenance

## 🚨 Important Notes

1. **Email Setup Required**: Configure SMTP settings before testing email features
2. **OTP Expiry**: OTPs expire in 5 minutes for security
3. **Token Security**: JWT tokens now include userId for proper validation
4. **Backward Compatibility**: Old login endpoint still works but is deprecated
5. **Admin Privileges**: Admins retain full access to all resources

## 🔄 Migration Guide

If upgrading from previous version:
1. Run database migrations to create OTP table
2. Update email configuration in application.properties
3. Test OTP flow with a valid email account
4. Update frontend to use new two-step login process

---

**Security Enhancement Complete! 🎉**

Your banking system now has enterprise-grade security with JWT user authorization, OTP authentication, and automated email notifications.
