package com.acme.scheduler.notifications.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface NotificationActivities {
  @ActivityMethod
  void sendConfirmation(AppointmentNotificationInput input);

  @ActivityMethod
  void sendReminder(AppointmentNotificationInput input, String reminderType);

  @ActivityMethod
  void sendFollowUp(AppointmentNotificationInput input);

  @ActivityMethod
  void recordAttempt(String appointmentId, String type, String status, String details);
}
