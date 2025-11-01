package com.taskmanager.service;

import com.taskmanager.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmailService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Email Service Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserService userService;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private EmailService emailService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setEnabled(true);
    }

    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmail() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail("test@example.com", "Test Subject", "Test Body");

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly("test@example.com");
        assertThat(sentMessage.getSubject()).isEqualTo("Test Subject");
        assertThat(sentMessage.getText()).isEqualTo("Test Body");
        assertThat(sentMessage.getFrom()).isEqualTo("taskmanager@example.com");
    }

    @Test
    @DisplayName("Should handle email sending failure gracefully")
    void shouldHandleEmailSendingFailure() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Should not throw exception
        emailService.sendEmail("test@example.com", "Test", "Body");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send report email to user")
    void shouldSendReportEmail() {
        String reportText = "Report content";
        when(reportService.generateTextReport(testUser)).thenReturn(reportText);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendReportEmail(testUser, "Daily Report");

        verify(reportService).generateTextReport(testUser);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send daily reports to all enabled users")
    void shouldSendDailyReports() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setEnabled(true);

        List<User> users = Arrays.asList(testUser, user2);

        when(userService.findAll()).thenReturn(users);
        when(reportService.generateTextReport(any(User.class))).thenReturn("Report");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendDailyReports();

        verify(userService).findAll();
        verify(reportService, times(2)).generateTextReport(any(User.class));
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send weekly reports to all enabled users")
    void shouldSendWeeklyReports() {
        List<User> users = Arrays.asList(testUser);

        when(userService.findAll()).thenReturn(users);
        when(reportService.generateTextReport(testUser)).thenReturn("Report");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendWeeklyReports();

        verify(userService).findAll();
        verify(reportService).generateTextReport(testUser);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should not send reports to disabled users")
    void shouldNotSendReportsToDisabledUsers() {
        testUser.setEnabled(false);
        List<User> users = Arrays.asList(testUser);

        when(userService.findAll()).thenReturn(users);

        emailService.sendDailyReports();

        verify(userService).findAll();
        verify(reportService, never()).generateTextReport(any());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle report generation failure in scheduled job")
    void shouldHandleReportGenerationFailure() {
        List<User> users = Arrays.asList(testUser);

        when(userService.findAll()).thenReturn(users);
        when(reportService.generateTextReport(testUser)).thenThrow(new RuntimeException("Report error"));

        // Should not throw exception
        emailService.sendDailyReports();

        verify(userService).findAll();
        verify(reportService).generateTextReport(testUser);
    }
}