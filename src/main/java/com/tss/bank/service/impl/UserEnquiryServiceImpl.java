package com.tss.bank.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tss.bank.entity.UserEnquiry;
import com.tss.bank.repository.UserEnquiryRepository;
import com.tss.bank.service.UserEnquiryService;

@Service
public class UserEnquiryServiceImpl implements UserEnquiryService {

    @Autowired
    private UserEnquiryRepository userEnquiryRepository;

    @Override
    public UserEnquiry save(UserEnquiry userEnquiry) {
        return userEnquiryRepository.save(userEnquiry);
    }

    @Override
    public Optional<UserEnquiry> findById(Integer enquiryId) {
        return userEnquiryRepository.findById(enquiryId);
    }

    @Override
    public List<UserEnquiry> findByUserId(Integer userId) {
        return userEnquiryRepository.findByUserUserId(userId);
    }

    @Override
    public List<UserEnquiry> findByStatus(UserEnquiry.Status status) {
        return userEnquiryRepository.findByStatus(status);
    }

    @Override
    public List<UserEnquiry> findByQueryType(UserEnquiry.QueryType queryType) {
        return userEnquiryRepository.findByQueryType(queryType);
    }

    @Override
    public List<UserEnquiry> findByUserIdAndStatus(Integer userId, UserEnquiry.Status status) {
        return userEnquiryRepository.findByUserUserIdAndStatus(userId, status);
    }

    @Override
    public List<UserEnquiry> findByQueryTypeAndStatus(UserEnquiry.QueryType queryType, UserEnquiry.Status status) {
        return userEnquiryRepository.findByQueryTypeAndStatus(queryType, status);
    }

    @Override
    public Long countOpenEnquiries() {
        return userEnquiryRepository.countByStatus(UserEnquiry.Status.OPEN);
    }

    @Override
    public List<UserEnquiry> findAll() {
        return userEnquiryRepository.findAll();
    }

    @Override
    public void deleteById(Integer enquiryId) {
        userEnquiryRepository.deleteById(enquiryId);
    }

    @Override
    public UserEnquiry update(UserEnquiry userEnquiry) {
        return userEnquiryRepository.save(userEnquiry);
    }
}
