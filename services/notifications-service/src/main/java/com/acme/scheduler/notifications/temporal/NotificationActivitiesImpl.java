package com.acme.scheduler.notifications.temporal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Production:
 * - Implement providers (SES/Twilio/Pinpoint/FCM) behind adapters
 * - Add tenant-aware templates and quiet-hours enforcement
 * - Persist attempts to DB for audit
 */
@Component
public class NotificationActivitiesImpl implements NotificationActivities {
  private static final Logger log = LoggerFactory.getLogger(NotificationActivitiesImpl.class);

  @Override
  public void sendConfirmation(AppointmentNotificationInput input) {
    log.info("CONFIRM appointment={} email={} phone={}", input.appointmentId(), input.email(), input.phone());
    recordAttempt(input.appointmentId(), "CONFIRM", "OK", "demo");
  }

  @Override
  public void sendReminder(AppointmentNotificationInput input, String reminderType) {
    log.info("REMINDER({}) appointment={} startTimeUtc={}", reminderType, input.appointmentId(), input.startTimeUtc());
    recordAttempt(input.appointmentId(), "REMINDER-"+reminderType, "OK", "demo");
  }

  @Override
  public void sendFollowUp(AppointmentNotificationInput input) {
    log.info("FOLLOW_UP appointment={}", input.appointmentId());
    recordAttempt(input.appointmentId(), "FOLLOW_UP", "OK", "demo");
  }

  @Override
  public void recordAttempt(String appointmentId, String type, String status, String details) {
    log.info("ATTEMPT appointment={} type={} status={} details={}", appointmentId, type, status, details);
  }
}
