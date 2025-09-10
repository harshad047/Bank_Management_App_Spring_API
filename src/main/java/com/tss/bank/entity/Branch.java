package com.tss.bank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "branches")
public class Branch {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Integer branchId;
    
    @Column(name = "branch_name", nullable = false, length = 100)
    private String branchName;
    
    @Column(name = "branch_code", nullable = false, unique = true, length = 10)
    private String branchCode;
    
    @Column(name = "ifsc_code", nullable = false, unique = true, length = 11)
    private String ifscCode;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 100)
    private String state;
    
    @Column(nullable = false, length = 100)
    private String country;
    
    @Column(nullable = false, length = 10)
    private String pincode;
    
    @Column(nullable = false, length = 255)
    private String address;
    
    @Column(name = "manager_name", length = 100)
    private String managerName;
    
    @Column(name = "contact_number", length = 15)
    private String contactNumber;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;
    
    @Column(name = "created_at")
    private Date createdAt;
    
    @Column(name = "updated_at")
    private Date updatedAt;
    
    @OneToMany(mappedBy = "branch")
    private List<User> users;
    
    @OneToMany(mappedBy = "branch")
    private List<Account> accounts;
    
    public enum Status {
        ACTIVE, INACTIVE, CLOSED
    }
}
