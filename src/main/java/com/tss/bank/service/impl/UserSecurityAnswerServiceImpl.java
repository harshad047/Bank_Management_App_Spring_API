package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.bank.entity.UserSecurityAnswer;
import com.tss.bank.repository.UserSecurityAnswerRepository;
import com.tss.bank.service.UserSecurityAnswerService;

@Service
public class UserSecurityAnswerServiceImpl implements UserSecurityAnswerService {

    @Autowired
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @Override
    public UserSecurityAnswer save(UserSecurityAnswer userSecurityAnswer) {
        return userSecurityAnswerRepository.save(userSecurityAnswer);
    }

    @Override
    public Optional<UserSecurityAnswer> findById(Integer answerId) {
        return userSecurityAnswerRepository.findById(answerId);
    }

    @Override
    public List<UserSecurityAnswer> findByUserId(Integer userId) {
        return userSecurityAnswerRepository.findByUserUserId(userId);
    }

    @Override
    public List<UserSecurityAnswer> findByQuestionId(Integer questionId) {
        return userSecurityAnswerRepository.findByQuestionQuestionId(questionId);
    }

    @Override
    public Optional<UserSecurityAnswer> findByUserIdAndQuestionId(Integer userId, Integer questionId) {
        return userSecurityAnswerRepository.findByUserUserIdAndQuestionQuestionId(userId, questionId);
    }

    @Override
    public Long countByUserId(Integer userId) {
        return userSecurityAnswerRepository.countByUserUserId(userId);
    }

    @Override
    public List<UserSecurityAnswer> findAll() {
        return userSecurityAnswerRepository.findAll();
    }

    @Override
    public void deleteById(Integer answerId) {
        userSecurityAnswerRepository.deleteById(answerId);
    }

    @Override
    public UserSecurityAnswer update(UserSecurityAnswer userSecurityAnswer) {
        return userSecurityAnswerRepository.save(userSecurityAnswer);
    }
}
