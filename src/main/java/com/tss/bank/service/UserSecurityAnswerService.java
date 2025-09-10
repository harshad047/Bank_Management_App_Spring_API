package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import com.tss.bank.entity.UserSecurityAnswer;

public interface UserSecurityAnswerService {
    
    UserSecurityAnswer save(UserSecurityAnswer userSecurityAnswer);
    
    Optional<UserSecurityAnswer> findById(Integer answerId);
    
    List<UserSecurityAnswer> findByUserId(Integer userId);
    
    List<UserSecurityAnswer> findByQuestionId(Integer questionId);
    
    Optional<UserSecurityAnswer> findByUserIdAndQuestionId(Integer userId, Integer questionId);
    
    Long countByUserId(Integer userId);
    
    List<UserSecurityAnswer> findAll();
    
    void deleteById(Integer answerId);
    
    UserSecurityAnswer update(UserSecurityAnswer userSecurityAnswer);
}
