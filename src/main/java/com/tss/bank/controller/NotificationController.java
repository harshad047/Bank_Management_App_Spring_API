package com.tss.bank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tss.bank.dto.response.ApiResponse;
import com.tss.bank.service.NotificationService;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Send Notifications
    @PostMapping("/send/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendUserNotification(
            @PathVariable Integer userId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {
        notificationService.sendUserNotification(userId, title, message, type);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User notification sent successfully", "Notification delivered"));
    }

    @PostMapping("/send/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendAdminNotification(
            @PathVariable Integer adminId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {
        notificationService.sendAdminNotification(adminId, title, message, type);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Admin notification sent successfully", "Notification delivered"));
    }

    @PostMapping("/send/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendBroadcastNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type,
            @RequestParam String targetAudience) {
        notificationService.sendBroadcastNotification(title, message, type, targetAudience);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Broadcast notification sent successfully", "Notification delivered"));
    }

    // Get Notifications
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUnreadNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Unread notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReadNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getReadNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Read notifications retrieved successfully", notifications));
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAdminNotifications(@PathVariable Integer adminId) {
        List<Map<String, Object>> notifications = notificationService.getAdminNotifications(adminId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin notifications retrieved successfully", notifications));
    }

    // Notification Management
    @PostMapping("/{notificationId}/mark-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read", "Status updated"));
    }

    @PostMapping("/{notificationId}/mark-unread")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> markAsUnread(@PathVariable Integer notificationId) {
        notificationService.markAsUnread(notificationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as unread", "Status updated"));
    }

    @PostMapping("/user/{userId}/mark-all-read")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@PathVariable Integer userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "All notifications marked as read", "Status updated"));
    }

    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Integer notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification deleted successfully", "Notification removed"));
    }

    @DeleteMapping("/user/{userId}/clear-all")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> clearAllNotifications(@PathVariable Integer userId) {
        notificationService.clearAllNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "All notifications cleared", "Notifications removed"));
    }

    // Type-based Queries
    @GetMapping("/user/{userId}/type/{type}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotificationsByType(
            @PathVariable Integer userId,
            @PathVariable String type) {
        List<Map<String, Object>> notifications = notificationService.getNotificationsByType(userId, type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications by type retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/alerts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAlertNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getAlertNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Alert notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/warnings")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWarningNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getWarningNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Warning notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInfoNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getInfoNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Info notifications retrieved successfully", notifications));
    }

    // Date-based Queries
    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotificationsByDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> notifications = notificationService.getNotificationsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications by date range retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/today")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTodayNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getTodayNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Today's notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/this-week")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getThisWeekNotifications(@PathVariable Integer userId) {
        List<Map<String, Object>> notifications = notificationService.getThisWeekNotifications(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "This week's notifications retrieved successfully", notifications));
    }

    // Statistics
    @GetMapping("/user/{userId}/counts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getNotificationCounts(@PathVariable Integer userId) {
        Map<String, Long> counts = notificationService.getNotificationCounts(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification counts retrieved successfully", counts));
    }

    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount(@PathVariable Integer userId) {
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Unread notification count retrieved successfully", count));
    }

    // System Notifications
    @PostMapping("/system/transaction-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendTransactionAlert(
            @RequestParam Integer userId,
            @RequestParam String transactionType,
            @RequestParam String amount) {
        notificationService.sendTransactionAlert(userId, transactionType, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction alert sent successfully", "Alert delivered"));
    }

    @PostMapping("/system/security-alert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendSecurityAlert(
            @RequestParam Integer userId,
            @RequestParam String alertType,
            @RequestParam String details) {
        notificationService.sendSecurityAlert(userId, alertType, details);
        return ResponseEntity.ok(new ApiResponse<>(true, "Security alert sent successfully", "Alert delivered"));
    }

    @PostMapping("/system/account-status-change")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendAccountStatusChangeNotification(
            @RequestParam Integer userId,
            @RequestParam String oldStatus,
            @RequestParam String newStatus) {
        notificationService.sendAccountStatusChangeNotification(userId, oldStatus, newStatus);
        return ResponseEntity.ok(new ApiResponse<>(true, "Account status change notification sent", "Notification delivered"));
    }

    @PostMapping("/system/payment-reminder")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendPaymentReminder(
            @RequestParam Integer userId,
            @RequestParam String paymentType,
            @RequestParam String dueDate,
            @RequestParam String amount) {
        notificationService.sendPaymentReminder(userId, paymentType, dueDate, amount);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment reminder sent successfully", "Reminder delivered"));
    }

    // Admin Operations
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getAllNotifications(Pageable pageable) {
        Page<Map<String, Object>> notifications = notificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "All notifications retrieved successfully", notifications));
    }

    @GetMapping("/system/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemNotificationStats() {
        Map<String, Object> stats = notificationService.getSystemNotificationStats();
        return ResponseEntity.ok(new ApiResponse<>(true, "System notification stats retrieved successfully", stats));
    }

    // Notification Templates
    @GetMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotificationTemplates() {
        List<Map<String, Object>> templates = notificationService.getNotificationTemplates();
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification templates retrieved successfully", templates));
    }

    @PostMapping("/templates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> createNotificationTemplate(
            @RequestParam String name,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String type) {
        notificationService.createNotificationTemplate(name, title, content, type);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Notification template created successfully", "Template created"));
    }

    // Notification Preferences
    @GetMapping("/user/{userId}/preferences")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserNotificationPreferences(@PathVariable Integer userId) {
        Map<String, Object> preferences = notificationService.getUserNotificationPreferences(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User notification preferences retrieved successfully", preferences));
    }

    @PutMapping("/user/{userId}/preferences")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateUserNotificationPreferences(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> preferences) {
        notificationService.updateUserNotificationPreferences(userId, preferences);
        return ResponseEntity.ok(new ApiResponse<>(true, "User notification preferences updated successfully", "Preferences updated"));
    }

    // Bulk Operations
    @PostMapping("/bulk/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> sendBulkNotifications(
            @RequestBody List<Integer> userIds,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String type) {
        notificationService.sendBulkNotifications(userIds, title, message, type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bulk notifications sent successfully", "Notifications delivered"));
    }

    @PostMapping("/cleanup/{daysOld}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> cleanupOldNotifications(@PathVariable Integer daysOld) {
        notificationService.cleanupOldNotifications(daysOld);
        return ResponseEntity.ok(new ApiResponse<>(true, "Old notifications cleaned up successfully", "Cleanup completed"));
    }
}
