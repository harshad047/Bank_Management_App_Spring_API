package com.tss.bank.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tss.bank.dto.request.FixedDepositRequest;
import com.tss.bank.dto.response.FixedDepositResponse;
import com.tss.bank.entity.Account;
import com.tss.bank.entity.FixedDeposit;
import com.tss.bank.exception.AccountApiException;
import com.tss.bank.exception.FixedDepositApiException;
import com.tss.bank.repository.AccountRepository;
import com.tss.bank.repository.FixedDepositRepository;
import com.tss.bank.service.FixedDepositService;
import com.tss.bank.service.MappingService;

@Service
@Transactional
public class FixedDepositServiceImpl implements FixedDepositService {

    @Autowired
    private FixedDepositRepository fixedDepositRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private MappingService mappingService;
    
    // Interest rates based on tenure (in months)
    private static final BigDecimal RATE_6_TO_12_MONTHS = new BigDecimal("6.5");
    private static final BigDecimal RATE_12_TO_24_MONTHS = new BigDecimal("7.0");
    private static final BigDecimal RATE_24_TO_36_MONTHS = new BigDecimal("7.5");
    private static final BigDecimal RATE_ABOVE_36_MONTHS = new BigDecimal("8.0");
    
    private static final BigDecimal MINIMUM_FD_AMOUNT = new BigDecimal("1000");
    private static final Integer MINIMUM_TENURE = 6;
    private static final Integer MAXIMUM_TENURE = 120;

    @Override
    public FixedDepositResponse createFixedDeposit(FixedDepositRequest request) {
        // Validate account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountApiException("Account not found with ID: " + request.getAccountId()));
        
        // Validate minimum amount
        if (request.getAmount().compareTo(MINIMUM_FD_AMOUNT) < 0) {
            throw new FixedDepositApiException("Minimum FD amount is " + MINIMUM_FD_AMOUNT);
        }
        
        // Validate tenure
        if (request.getTenureMonths() < MINIMUM_TENURE || request.getTenureMonths() > MAXIMUM_TENURE) {
            throw new FixedDepositApiException("FD tenure must be between " + MINIMUM_TENURE + " and " + MAXIMUM_TENURE + " months");
        }
        
        // Check if account has sufficient balance
        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new FixedDepositApiException("Insufficient balance in account");
        }
        
        // Debit amount from account
        account.setBalance(account.getBalance().subtract(request.getAmount()));
        accountRepository.save(account);
        
        // Create FD
        FixedDeposit fixedDeposit = new FixedDeposit();
        fixedDeposit.setAccount(account);
        fixedDeposit.setUser(account.getUser());
        fixedDeposit.setAmount(request.getAmount());
        fixedDeposit.setTenureMonths(request.getTenureMonths());
        fixedDeposit.setInterestRate(calculateInterestRate(request.getTenureMonths()));
        fixedDeposit.setStartDate(new Date());
        fixedDeposit.setMaturityDate(calculateMaturityDate(fixedDeposit.getStartDate(), request.getTenureMonths()));
        fixedDeposit.setMaturityAmount(calculateMaturityAmount(request.getAmount(), fixedDeposit.getInterestRate(), request.getTenureMonths()));
        fixedDeposit.setStatus(FixedDeposit.Status.ACTIVE);
        fixedDeposit.setCreatedAt(new Date());
        fixedDeposit.setUpdatedAt(new Date());
        
        FixedDeposit savedFD = fixedDepositRepository.save(fixedDeposit);
        return mappingService.map(savedFD, FixedDepositResponse.class);
    }

    @Override
    public FixedDepositResponse getFixedDepositDetails(Integer fdId) {
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new FixedDepositApiException("Fixed Deposit not found with ID: " + fdId));
        return mappingService.map(fixedDeposit, FixedDepositResponse.class);
    }

    @Override
    public List<FixedDepositResponse> getAccountFixedDeposits(Integer accountId) {
        List<FixedDeposit> fixedDeposits = fixedDepositRepository.findByAccountAccountId(accountId);
        return fixedDeposits.stream()
                .map(fd -> mappingService.map(fd, FixedDepositResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<FixedDepositResponse> getUserFixedDeposits(Integer userId) {
        List<FixedDeposit> fixedDeposits = fixedDepositRepository.findByAccountUserUserId(userId);
        return fixedDeposits.stream()
                .map(fd -> mappingService.map(fd, FixedDepositResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public FixedDepositResponse prematureWithdrawal(Integer fdId, String reason) {
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new FixedDepositApiException("Fixed Deposit not found with ID: " + fdId));
        
        if (fixedDeposit.getStatus() != FixedDeposit.Status.ACTIVE) {
            throw new FixedDepositApiException("Fixed Deposit is not active");
        }
        
        // Calculate premature withdrawal amount (reduced interest rate)
        BigDecimal prematureAmount = calculatePrematureAmount(fixedDeposit);
        
        // Credit amount back to account
        Account account = fixedDeposit.getAccount();
        account.setBalance(account.getBalance().add(prematureAmount));
        accountRepository.save(account);
        
        // Update FD status
        fixedDeposit.setStatus(FixedDeposit.Status.EARLY_CLOSE);
        fixedDeposit.setMaturityAmount(prematureAmount);
        fixedDeposit.setMaturityDate(new Date());
        fixedDeposit.setUpdatedAt(new Date());
        
        FixedDeposit updatedFD = fixedDepositRepository.save(fixedDeposit);
        return mappingService.map(updatedFD, FixedDepositResponse.class);
    }

    @Override
    public FixedDepositResponse matureFixedDeposit(Integer fdId) {
        FixedDeposit fixedDeposit = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new FixedDepositApiException("Fixed Deposit not found with ID: " + fdId));
        
        if (fixedDeposit.getStatus() != FixedDeposit.Status.ACTIVE) {
            throw new FixedDepositApiException("Fixed Deposit is not active");
        }
        
        if (new Date().before(fixedDeposit.getMaturityDate())) {
            throw new FixedDepositApiException("Fixed Deposit has not yet matured");
        }
        
        // Credit maturity amount to account
        Account account = fixedDeposit.getAccount();
        account.setBalance(account.getBalance().add(fixedDeposit.getMaturityAmount()));
        accountRepository.save(account);
        
        // Update FD status
        fixedDeposit.setStatus(FixedDeposit.Status.MATURED);
        fixedDeposit.setUpdatedAt(new Date());
        
        FixedDeposit updatedFD = fixedDepositRepository.save(fixedDeposit);
        return mappingService.map(updatedFD, FixedDepositResponse.class);
    }

    @Override
    public BigDecimal calculateInterestRate(Integer tenureMonths) {
        if (tenureMonths >= 6 && tenureMonths < 12) {
            return RATE_6_TO_12_MONTHS;
        } else if (tenureMonths >= 12 && tenureMonths < 24) {
            return RATE_12_TO_24_MONTHS;
        } else if (tenureMonths >= 24 && tenureMonths < 36) {
            return RATE_24_TO_36_MONTHS;
        } else {
            return RATE_ABOVE_36_MONTHS;
        }
    }

    @Override
    public BigDecimal calculateMaturityAmount(BigDecimal principal, BigDecimal interestRate, Integer tenureMonths) {
        // Simple interest calculation: A = P + (P * R * T) / 100
        // Where T is in years
        BigDecimal timeInYears = new BigDecimal(tenureMonths).divide(new BigDecimal("12"), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal interest = principal.multiply(interestRate).multiply(timeInYears).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        return principal.add(interest);
    }

    @Override
    public Date calculateMaturityDate(Date startDate, Integer tenureMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, tenureMonths);
        return calendar.getTime();
    }

    @Override
    public List<FixedDepositResponse> getMaturedDeposits() {
        List<FixedDeposit> maturedDeposits = fixedDepositRepository.findMaturedDeposits(new Date());
        return maturedDeposits.stream()
                .map(fd -> mappingService.map(fd, FixedDepositResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void processMaturedDeposits() {
        List<FixedDeposit> maturedDeposits = fixedDepositRepository.findMaturedDeposits(new Date());
        
        for (FixedDeposit fd : maturedDeposits) {
            try {
                matureFixedDeposit(fd.getFdId());
            } catch (Exception e) {
                // Log error but continue processing other FDs
                System.err.println("Error processing matured FD " + fd.getFdId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getTotalActiveDeposits(Integer accountId) {
        BigDecimal total = fixedDepositRepository.getTotalActiveDepositsByAccount(accountId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Page<FixedDepositResponse> findAllFixedDeposits(Pageable pageable) {
        Page<FixedDeposit> fdPage = fixedDepositRepository.findAll(pageable);
        List<FixedDepositResponse> responses = fdPage.getContent().stream()
                .map(fd -> mappingService.map(fd, FixedDepositResponse.class))
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, fdPage.getTotalElements());
    }

    @Override
    public long getTotalFixedDepositCount() {
        return fixedDepositRepository.count();
    }

    @Override
    public long getActiveFixedDepositCount() {
        return fixedDepositRepository.countByStatus(FixedDeposit.Status.ACTIVE);
    }

    @Override
    public long getMaturedFixedDepositCount() {
        return fixedDepositRepository.countByStatus(FixedDeposit.Status.MATURED);
    }

    // Entity-based operations (for backward compatibility)
    @Override
    public FixedDeposit save(FixedDeposit fixedDeposit) {
        return fixedDepositRepository.save(fixedDeposit);
    }

    @Override
    public Optional<FixedDeposit> findById(Integer fdId) {
        return fixedDepositRepository.findById(fdId);
    }

    @Override
    public List<FixedDeposit> findByUserId(Integer userId) {
        return fixedDepositRepository.findByAccountUserUserId(userId);
    }

    @Override
    public List<FixedDeposit> findByAccountId(Integer accountId) {
        return fixedDepositRepository.findByAccountAccountId(accountId);
    }

    @Override
    public List<FixedDeposit> findByStatus(FixedDeposit.Status status) {
        return fixedDepositRepository.findByStatus(status);
    }

    @Override
    public List<FixedDeposit> findByUserIdAndStatus(Integer userId, FixedDeposit.Status status) {
        return fixedDepositRepository.findByAccountUserUserId(userId).stream()
                .filter(fd -> fd.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<FixedDeposit> findMaturedDeposits(Date date) {
        return fixedDepositRepository.findMaturedDeposits(date);
    }

    @Override
    public BigDecimal getTotalAmountByUserAndStatus(Integer userId, FixedDeposit.Status status) {
        return findByUserIdAndStatus(userId, status).stream()
                .map(FixedDeposit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<FixedDeposit> findAll() {
        return fixedDepositRepository.findAll();
    }

    @Override
    public void deleteById(Integer fdId) {
        fixedDepositRepository.deleteById(fdId);
    }

    @Override
    public FixedDeposit update(FixedDeposit fixedDeposit) {
        fixedDeposit.setUpdatedAt(new Date());
        return fixedDepositRepository.save(fixedDeposit);
    }

    private BigDecimal calculatePrematureAmount(FixedDeposit fixedDeposit) {
        // For premature withdrawal, apply penalty (reduce interest rate by 1%)
        BigDecimal penaltyRate = fixedDeposit.getInterestRate().subtract(BigDecimal.ONE);
        if (penaltyRate.compareTo(BigDecimal.ZERO) < 0) {
            penaltyRate = BigDecimal.ZERO;
        }
        
        // Calculate months elapsed
        long daysDiff = (new Date().getTime() - fixedDeposit.getStartDate().getTime()) / (1000 * 60 * 60 * 24);
        int monthsElapsed = (int) (daysDiff / 30); // Approximate
        
        return calculateMaturityAmount(fixedDeposit.getAmount(), penaltyRate, monthsElapsed);
    }
}
