package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import com.tss.bank.entity.UserEnquiry;

public interface UserEnquiryService {
    
    UserEnquiry save(UserEnquiry userEnquiry);
    
    Optional<UserEnquiry> findById(Integer enquiryId);
    
    List<UserEnquiry> findByUserId(Integer userId);
    
    List<UserEnquiry> findByStatus(UserEnquiry.Status status);
    
    List<UserEnquiry> findByQueryType(UserEnquiry.QueryType queryType);
    
    List<UserEnquiry> findByUserIdAndStatus(Integer userId, UserEnquiry.Status status);
    
    List<UserEnquiry> findByQueryTypeAndStatus(UserEnquiry.QueryType queryType, UserEnquiry.Status status);
    
    Long countOpenEnquiries();
    
    List<UserEnquiry> findAll();
    
    void deleteById(Integer enquiryId);
    
    UserEnquiry update(UserEnquiry userEnquiry);
}
