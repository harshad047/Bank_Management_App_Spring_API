# Bank Management System - DTOs Documentation

This document provides an overview of the Data Transfer Objects (DTOs) created for the Bank Management System.

## Package Structure

```
com.tss.bank.dto/
├── request/          # Request DTOs for API endpoints
└── response/         # Response DTOs for API responses
```

## Request DTOs

### User Management
- **UserRegistrationRequest**: User registration with validation for username, password, email, and phone
- **UserLoginRequest**: User login credentials
- **PasswordChangeRequest**: Password change with current/new password validation
- **UserSecurityAnswerRequest**: Security question answers

### Account Management
- **AccountCreationRequest**: Account creation with initial balance validation

### Transaction Management
- **TransactionRequest**: Transaction operations with amount and type validation
- **TransferRequest**: Money transfer between accounts with IFSC validation

### Beneficiary Management
- **BeneficiaryRequest**: Add beneficiary with account number and IFSC validation

### Fixed Deposit Management
- **FixedDepositRequest**: FD creation with amount and tenure validation
- **FDApplicationRequest**: FD application submission

### Admin Management
- **AdminLoginRequest**: Admin authentication
- **AdminApprovalRequest**: Admin approval/rejection of applications

### Support
- **UserEnquiryRequest**: User support queries with type validation
- **SecurityQuestionRequest**: Security question management

## Response DTOs

### Core Responses
- **ApiResponse<T>**: Generic API response wrapper with success/error handling
- **PagedResponse<T>**: Paginated response for list operations

### Entity Responses
- **UserResponse**: User information (excluding sensitive data)
- **AccountResponse**: Account details
- **TransactionResponse**: Transaction history
- **TransferResponse**: Transfer details
- **BeneficiaryResponse**: Beneficiary information
- **FixedDepositResponse**: FD details
- **FDApplicationResponse**: FD application status
- **AdminResponse**: Admin information
- **UserEnquiryResponse**: Support query details
- **SecurityQuestionResponse**: Available security questions

## Validation Features

### Common Validations
- **@NotNull**: Required fields
- **@NotBlank**: Non-empty strings
- **@Size**: String length constraints
- **@Email**: Email format validation
- **@Pattern**: Regex validation for specific formats
- **@DecimalMin/@DecimalMax**: Numeric range validation
- **@Min/@Max**: Integer range validation

### Custom Validations
- **Password**: Minimum 8 characters with uppercase, lowercase, digit, and special character
- **Phone**: 10-15 digit validation
- **Account Number**: 10-20 digit validation
- **IFSC Code**: Standard Indian IFSC format validation
- **Username**: Alphanumeric with underscores only

## Usage Examples

### Request Validation
```java
@PostMapping("/register")
public ResponseEntity<ApiResponse<UserResponse>> registerUser(
    @Valid @RequestBody UserRegistrationRequest request) {
    // Validation is automatically handled by @Valid annotation
}
```

### Response Wrapper
```java
return ResponseEntity.ok(ApiResponse.success(userResponse, "User registered successfully"));
```

### Error Handling
```java
return ResponseEntity.badRequest().body(ApiResponse.error("Validation failed"));
```

## Dependencies

- **spring-boot-starter-validation**: For Bean Validation (JSR-303)
- **modelmapper**: For automatic object mapping between entities and DTOs
- **lombok**: For reducing boilerplate code
- **jakarta.validation**: Validation annotations

## ModelMapper Integration

The project uses ModelMapper for automatic mapping between entities and DTOs. This eliminates the need for manual mapper classes.

### Configuration
ModelMapper is configured in `ModelMapperConfig.java` with:
- **STRICT** matching strategy for precise field mapping
- Field-level access enabled
- Private field access allowed

### Usage Examples

#### Basic Mapping
```java
@Autowired
private MappingService mappingService;

// Entity to Response DTO
UserResponse userResponse = mappingService.map(user, UserResponse.class);

// Request DTO to Entity
User user = mappingService.map(userRegistrationRequest, User.class);
```

#### List Mapping
```java
List<UserResponse> userResponses = mappingService.mapList(users, UserResponse.class);
```

#### Update Existing Object
```java
mappingService.mapTo(userUpdateRequest, existingUser);
```

### Custom Validation Annotations

#### @ValidAccountNumber
Custom validator for bank account numbers:
- Removes spaces and special characters
- Validates 10-20 digit length
- Ensures numeric format

#### @ValidIFSC
Custom validator for IFSC codes:
- Validates standard Indian IFSC format (ABCD0123456)
- First 4 characters: alphabets
- 5th character: zero
- Last 6 characters: alphanumeric
