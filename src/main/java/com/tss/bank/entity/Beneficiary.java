package com.tss.bank.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "beneficiaries")
public class Beneficiary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beneficiary_id")
    private Integer beneficiaryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "beneficiary_name", length = 100)
    private String beneficiaryName;
    @Column(name = "beneficiary_acno", length = 20)
    private String beneficiaryAccountNumber;
    @Column(name = "beneficiary_ifsc", length = 20)
    private String ifscCode;
    @Column(name = "bank_name", length = 100)
    private String bankName;
    @Column(name = "branch_name", length = 100)
    private String branchName;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(length = 50)
    private String nickname;
    @Column(name = "added_at")
    private Date addedAt;
    @Column(name = "is_active")
    private Boolean isActive;
}