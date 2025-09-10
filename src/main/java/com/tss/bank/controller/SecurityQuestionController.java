package com.tss.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.entity.SecurityQuestion;
import com.tss.bank.service.SecurityQuestionService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/security-questions")
@CrossOrigin(origins = "*")
public class SecurityQuestionController {

    @Autowired
    private SecurityQuestionService securityQuestionService;

    // Main Operations
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SecurityQuestion>> createSecurityQuestion(@Valid @RequestBody SecurityQuestion securityQuestion) {
        SecurityQuestion savedQuestion = securityQuestionService.save(securityQuestion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Security question created successfully", savedQuestion));
    }

    @PutMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SecurityQuestion>> updateSecurityQuestion(
            @PathVariable Integer questionId,
            @Valid @RequestBody SecurityQuestion securityQuestion) {
        securityQuestion.setQuestionId(questionId);
        SecurityQuestion updatedQuestion = securityQuestionService.update(securityQuestion);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security question updated successfully", updatedQuestion));
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSecurityQuestion(@PathVariable Integer questionId) {
        securityQuestionService.deleteById(questionId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security question deleted successfully", "Question removed"));
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SecurityQuestion>> getSecurityQuestionById(@PathVariable Integer questionId) {
        Optional<SecurityQuestion> question = securityQuestionService.findById(questionId);
        if (question.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Security question retrieved successfully", question.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // Query Operations
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SecurityQuestion>>> getAllSecurityQuestions() {
        List<SecurityQuestion> questions = securityQuestionService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "All security questions retrieved successfully", questions));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SecurityQuestion>>> searchSecurityQuestions(@RequestParam String keyword) {
        List<SecurityQuestion> questions = securityQuestionService.findByQuestionContaining(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security questions search completed", questions));
    }

    // Statistics
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalQuestionCount() {
        Long count = securityQuestionService.countAllQuestions();
        return ResponseEntity.ok(new ApiResponse<>(true, "Total question count retrieved successfully", count));
    }
}
