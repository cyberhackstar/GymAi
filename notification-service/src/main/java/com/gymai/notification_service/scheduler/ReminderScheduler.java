package com.gymai.notification_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gymai.notification_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // Every day at 8 AM
    public void sendDailyReminder() {
        // In real use, get list from DB
        emailService.sendEmail("user@example.com", "Daily Workout Reminder",
            "Don't forget to do your workout today! 💪");
    }
}
