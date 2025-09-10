package com.tss.bank.dto.response;

import com.tss.bank.entity.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponse {
    
    private Integer branchId;
    private String branchName;
    private String branchCode;
    private String ifscCode;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String address;
    private String managerName;
    private String contactNumber;
    private String email;
    private Branch.Status status;
    private Date createdAt;
    private Date updatedAt;
}
