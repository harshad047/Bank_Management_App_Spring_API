package com.tss.bank.service;

import java.util.List;
import java.util.Optional;

import com.tss.bank.entity.SecurityQuestion;

public interface SecurityQuestionService {
    
    SecurityQuestion save(SecurityQuestion securityQuestion);
    
    Optional<SecurityQuestion> findById(Integer questionId);
    
    List<SecurityQuestion> findByQuestionContaining(String keyword);
    
    Long countAllQuestions();
    
    List<SecurityQuestion> findAll();
    
    void deleteById(Integer questionId);
    
    SecurityQuestion update(SecurityQuestion securityQuestion);
}
