package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.bank.entity.SecurityQuestion;
import com.tss.bank.repository.SecurityQuestionRepository;
import com.tss.bank.service.SecurityQuestionService;

@Service
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @Override
    public SecurityQuestion save(SecurityQuestion securityQuestion) {
        return securityQuestionRepository.save(securityQuestion);
    }

    @Override
    public Optional<SecurityQuestion> findById(Integer questionId) {
        return securityQuestionRepository.findById(questionId);
    }

    @Override
    public List<SecurityQuestion> findByQuestionContaining(String keyword) {
        return securityQuestionRepository.findByQuestionTextContaining(keyword);
    }

    @Override
    public Long countAllQuestions() {
        return securityQuestionRepository.count();
    }

    @Override
    public List<SecurityQuestion> findAll() {
        return securityQuestionRepository.findAll();
    }

    @Override
    public void deleteById(Integer questionId) {
        securityQuestionRepository.deleteById(questionId);
    }

    @Override
    public SecurityQuestion update(SecurityQuestion securityQuestion) {
        return securityQuestionRepository.save(securityQuestion);
    }
}
