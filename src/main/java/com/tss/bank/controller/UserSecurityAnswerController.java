package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.entity.UserSecurityAnswer;
import com.tss.bank.service.UserSecurityAnswerService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user-security-answers")
@CrossOrigin(origins = "*")
public class UserSecurityAnswerController {

    @Autowired
    private UserSecurityAnswerService userSecurityAnswerService;

    // Main Operations
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserSecurityAnswer>> createUserSecurityAnswer(@Valid @RequestBody UserSecurityAnswer userSecurityAnswer) {
        UserSecurityAnswer savedAnswer = userSecurityAnswerService.save(userSecurityAnswer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User security answer created successfully", savedAnswer));
    }

    @PutMapping("/{answerId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserSecurityAnswer>> updateUserSecurityAnswer(
            @PathVariable Integer answerId,
            @Valid @RequestBody UserSecurityAnswer userSecurityAnswer) {
        userSecurityAnswer.setAnswerId(answerId);
        UserSecurityAnswer updatedAnswer = userSecurityAnswerService.update(userSecurityAnswer);
        return ResponseEntity.ok(new ApiResponse<>(true, "User security answer updated successfully", updatedAnswer));
    }

    @DeleteMapping("/{answerId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUserSecurityAnswer(@PathVariable Integer answerId) {
        userSecurityAnswerService.deleteById(answerId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User security answer deleted successfully", "Answer removed"));
    }

    @GetMapping("/{answerId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserSecurityAnswer>> getUserSecurityAnswerById(@PathVariable Integer answerId) {
        Optional<UserSecurityAnswer> answer = userSecurityAnswerService.findById(answerId);
        if (answer.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "User security answer retrieved successfully", answer.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // Query Operations
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSecurityAnswer>>> getUserSecurityAnswersByUserId(@PathVariable Integer userId) {
        List<UserSecurityAnswer> answers = userSecurityAnswerService.findByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User security answers retrieved successfully", answers));
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSecurityAnswer>>> getUserSecurityAnswersByQuestionId(@PathVariable Integer questionId) {
        List<UserSecurityAnswer> answers = userSecurityAnswerService.findByQuestionId(questionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security answers by question retrieved successfully", answers));
    }

    @GetMapping("/user/{userId}/question/{questionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserSecurityAnswer>> getUserSecurityAnswerByUserAndQuestion(
            @PathVariable Integer userId,
            @PathVariable Integer questionId) {
        Optional<UserSecurityAnswer> answer = userSecurityAnswerService.findByUserIdAndQuestionId(userId, questionId);
        if (answer.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "User security answer found", answer.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUserSecurityAnswerCount(@PathVariable Integer userId) {
        Long count = userSecurityAnswerService.countByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User security answer count retrieved successfully", count));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserSecurityAnswer>>> getAllUserSecurityAnswers() {
        List<UserSecurityAnswer> answers = userSecurityAnswerService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "All user security answers retrieved successfully", answers));
    }
}
