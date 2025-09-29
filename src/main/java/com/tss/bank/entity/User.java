	package com.tss.bank.entity;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 100)
    private String password;
    @Column(nullable = false, length = 100)
    private String email;
    @Column(length = 20)
    private String phone;
    @Column(name = "first_name", length = 50)
    private String firstName;
    @Column(name = "last_name", length = 50)
    private String lastName;
    @Column(length = 255)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(name = "approved_by")
    private Integer approvedBy;
    @Column(name = "approved_at")
    private Date approvedAt;
    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;
    @Column(name = "email_verified_at")
    private Date emailVerifiedAt;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

    @OneToMany(mappedBy = "user")
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "user")
    private List<Beneficiary> beneficiaries;

    @OneToMany(mappedBy = "user")
    private List<FixedDeposit> fixedDeposits;

    @OneToMany(mappedBy = "user")
    private List<UserSecurityAnswer> securityAnswers;

    @OneToMany(mappedBy = "user")
    private List<UserEnquiry> enquiries;

    public enum Status {
        ACTIVE, INACTIVE, PENDING, REJECTED, EMAIL_UNVERIFIED
    }

    public enum Role {
        USER, ADMIN, SUPER_ADMIN
    }
}
