package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    
    private Long totalUsers;
    private Long activeUsers;
    private Long pendingUsers;
    private Long totalAccounts;
    private Long totalTransactions;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;
    private Long pendingFDApplications;
    private Long activeFDs;
    private Long pendingEnquiries;
    private Long resolvedEnquiries;
}
