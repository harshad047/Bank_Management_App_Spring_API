package com.tss.bank.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.entity.FDApplication;
import com.tss.bank.repository.FDApplicationRepository;
import com.tss.bank.service.FDApplicationService;

@Service
@Transactional
public class FDApplicationServiceImpl implements FDApplicationService {

    @Autowired
    private FDApplicationRepository fdApplicationRepository;

    @Override
    public FDApplication save(FDApplication fdApplication) {
        return fdApplicationRepository.save(fdApplication);
    }

    @Override
    public Optional<FDApplication> findById(Integer fdAppId) {
        return fdApplicationRepository.findById(fdAppId);
    }

    @Override
    public List<FDApplication> findByUserId(Integer userId) {
        return fdApplicationRepository.findByUserId(userId);
    }

    @Override
    public List<FDApplication> findByStatus(FDApplication.Status status) {
        return fdApplicationRepository.findByStatus(status);
    }

    @Override
    public List<FDApplication> findByApprovedBy(Integer approvedBy) {
        return fdApplicationRepository.findByApprovedBy(approvedBy);
    }

    @Override
    public List<FDApplication> findByUserIdAndStatus(Integer userId, FDApplication.Status status) {
        return fdApplicationRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    public List<FDApplication> findByMinAmount(BigDecimal minAmount) {
        return fdApplicationRepository.findByAmountGreaterThanEqual(minAmount);
    }

    @Override
    public Long countPendingApplications() {
        return fdApplicationRepository.countByStatus(FDApplication.Status.PENDING);
    }

    @Override
    public List<FDApplication> findAll() {
        return fdApplicationRepository.findAll();
    }

    @Override
    public void deleteById(Integer fdAppId) {
        fdApplicationRepository.deleteById(fdAppId);
    }

    @Override
    public FDApplication update(FDApplication fdApplication) {
        return fdApplicationRepository.save(fdApplication);
    }
}
