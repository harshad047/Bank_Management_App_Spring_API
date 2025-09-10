package com.tss.bank.entity;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "fd_applications")
public class FDApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fd_app_id")
    private Integer fdAppId;

    @Column(name = "user_id")
    private Integer userId;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "tenure_months")
    private Integer tenureMonths;
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;
    @Column(name = "application_date")
    private Date applicationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "rejection_reason", length = 255)
    private String rejectionReason;
    @Column(name = "approved_by")
    private Integer approvedBy;
    @Column(name = "approved_at")
    private Date approvedAt;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}