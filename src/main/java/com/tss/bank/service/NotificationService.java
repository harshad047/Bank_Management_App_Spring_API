package com.tss.bank.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    
    // Send Notifications
    void sendUserNotification(Integer userId, String title, String message, String type);
    void sendAdminNotification(Integer adminId, String title, String message, String type);
    void sendBroadcastNotification(String title, String message, String type, String targetAudience);
    
    // Get Notifications
    List<Map<String, Object>> getUserNotifications(Integer userId);
    List<Map<String, Object>> getUnreadNotifications(Integer userId);
    List<Map<String, Object>> getReadNotifications(Integer userId);
    List<Map<String, Object>> getAdminNotifications(Integer adminId);
    
    // Notification Management
    void markAsRead(Integer notificationId);
    void markAsUnread(Integer notificationId);
    void markAllAsRead(Integer userId);
    void deleteNotification(Integer notificationId);
    void clearAllNotifications(Integer userId);
    
    // Type-based Queries
    List<Map<String, Object>> getNotificationsByType(Integer userId, String type);
    List<Map<String, Object>> getAlertNotifications(Integer userId);
    List<Map<String, Object>> getWarningNotifications(Integer userId);
    List<Map<String, Object>> getInfoNotifications(Integer userId);
    
    // Date-based Queries
    List<Map<String, Object>> getNotificationsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);
    List<Map<String, Object>> getTodayNotifications(Integer userId);
    List<Map<String, Object>> getThisWeekNotifications(Integer userId);
    
    // Statistics
    Map<String, Long> getNotificationCounts(Integer userId);
    long getUnreadNotificationCount(Integer userId);
    
    // System Notifications
    void sendTransactionAlert(Integer userId, String transactionType, String amount);
    void sendSecurityAlert(Integer userId, String alertType, String details);
    void sendAccountStatusChangeNotification(Integer userId, String oldStatus, String newStatus);
    void sendPaymentReminder(Integer userId, String paymentType, String dueDate, String amount);
    
    // Admin Operations
    Page<Map<String, Object>> getAllNotifications(Pageable pageable);
    Map<String, Object> getSystemNotificationStats();
    
    // Notification Templates
    List<Map<String, Object>> getNotificationTemplates();
    void createNotificationTemplate(String name, String title, String content, String type);
    
    // Notification Preferences
    Map<String, Object> getUserNotificationPreferences(Integer userId);
    void updateUserNotificationPreferences(Integer userId, Map<String, Object> preferences);
    
    // Bulk Operations
    void sendBulkNotifications(List<Integer> userIds, String title, String message, String type);
    void cleanupOldNotifications(Integer daysOld);
}
