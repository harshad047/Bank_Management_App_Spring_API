package com.tss.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryResponse {
    
    private Integer beneficiaryId;
    private Integer userId;
    private Integer accountId;
    private String beneficiaryName;
    private String beneficiaryAcno;
    private String beneficiaryIfsc;
    private String nickname;
    private Date addedAt;
    private Boolean isActive;
}
