package com.taskmanager.service;

import com.taskmanager.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for sending email notifications and scheduled reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserService userService;
    private final ReportService reportService;

    /**
     * Sends an email to a recipient.
     *
     * @param to      recipient email
     * @param subject email subject
     * @param body    email body
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("taskmanager@example.com");

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    /**
     * Sends a report email to a user.
     *
     * @param user    the user
     * @param subject the email subject
     */
    public void sendReportEmail(User user, String subject) {
        try {
            String reportText = reportService.generateTextReport(user);
            sendEmail(user.getEmail(), subject, reportText);
        } catch (Exception e) {
            log.error("Failed to send report email to user: {}", user.getUsername(), e);
        }
    }

    /**
     * Daily report job - runs at 6 PM every day.
     * Cron expression: 0 0 18 * * ?
     */
    @Scheduled(cron = "${app.scheduler.daily-report-cron:0 0 18 * * ?}")
    public void sendDailyReports() {
        log.info("Starting daily report job");
        List<User> users = userService.findAll();

        for (User user : users) {
            if (user.getEnabled()) {
                sendReportEmail(user, "Daily Task Report - " + java.time.LocalDate.now());
            }
        }

        log.info("Daily report job completed. Sent reports to {} users", users.size());
    }

    /**
     * Weekly report job - runs at 6 PM every Friday.
     * Cron expression: 0 0 18 ? * FRI
     */
    @Scheduled(cron = "${app.scheduler.weekly-report-cron:0 0 18 ? * FRI}")
    public void sendWeeklyReports() {
        log.info("Starting weekly report job");
        List<User> users = userService.findAll();

        for (User user : users) {
            if (user.getEnabled()) {
                sendReportEmail(user, "Weekly Task Report - Week of " + java.time.LocalDate.now());
            }
        }

        log.info("Weekly report job completed. Sent reports to {} users", users.size());
    }
}