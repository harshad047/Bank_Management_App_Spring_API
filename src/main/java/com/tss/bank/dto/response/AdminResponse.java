package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminResponse {
    
    private Integer adminId;
    private String username;
    private String email;
    private Boolean isSuperAdmin;
}
