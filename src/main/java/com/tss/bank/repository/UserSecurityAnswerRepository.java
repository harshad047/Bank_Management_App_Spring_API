package com.tss.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tss.bank.entity.UserSecurityAnswer;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Integer> {
    
    List<UserSecurityAnswer> findByUserUserId(Integer userId);
    
    List<UserSecurityAnswer> findByQuestionQuestionId(Integer questionId);
    
    Optional<UserSecurityAnswer> findByUserUserIdAndQuestionQuestionId(Integer userId, Integer questionId);
    
    Long countByUserUserId(Integer userId);
}
