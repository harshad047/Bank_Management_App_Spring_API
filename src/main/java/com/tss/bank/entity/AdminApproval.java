package com.tss.bank.entity;

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
@Table(name = "admin_approvals")
public class AdminApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Integer approvalId;

    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "admin_id")
    private Integer adminId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
    @Column(length = 255)
    private String reason;
    @Column(name = "created_at")
    private Date createdAt;

    public enum Action {
        APPROVE, REJECT
    }
}

