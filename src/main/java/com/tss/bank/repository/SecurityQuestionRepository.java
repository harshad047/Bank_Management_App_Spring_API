package com.tss.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.bank.entity.SecurityQuestion;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Integer> {
    
    List<SecurityQuestion> findByQuestionTextContaining(String keyword);
}
