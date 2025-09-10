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
@Table(name = "fixed_deposits")
public class FixedDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fd_id")
    private Integer fdId;

    @Column(name = "fd_app_id")
    private Integer fdAppId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    @Column(name = "tenure_months")
    private Integer tenureMonths;
    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;
    @Column(name = "maturity_amount", precision = 15, scale = 2)
    private BigDecimal maturityAmount;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "maturity_date")
    private Date maturityDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum Status {
        ACTIVE, CLOSED, PENDING, MATURED, EARLY_CLOSE
    }
}
