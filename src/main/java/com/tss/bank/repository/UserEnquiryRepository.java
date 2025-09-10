package com.tss.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.bank.entity.UserEnquiry;

public interface UserEnquiryRepository extends JpaRepository<UserEnquiry, Integer> {
    
    List<UserEnquiry> findByUserUserId(Integer userId);
    
    List<UserEnquiry> findByStatus(UserEnquiry.Status status);
    
    List<UserEnquiry> findByQueryType(UserEnquiry.QueryType queryType);
    
    List<UserEnquiry> findByUserUserIdAndStatus(Integer userId, UserEnquiry.Status status);
    
    List<UserEnquiry> findByQueryTypeAndStatus(UserEnquiry.QueryType queryType, UserEnquiry.Status status);
    
    Long countByStatus(UserEnquiry.Status status);
}
