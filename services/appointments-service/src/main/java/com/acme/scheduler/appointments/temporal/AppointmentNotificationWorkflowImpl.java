package com.acme.scheduler.appointments.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class AppointmentNotificationWorkflowImpl implements AppointmentNotificationWorkflow {

  private final AppointmentNotificationActivities activities =
      Workflow.newActivityStub(
          AppointmentNotificationActivities.class,
          ActivityOptions.newBuilder()
              .setStartToCloseTimeout(Duration.ofSeconds(10))
              .setRetryOptions(
                  RetryOptions.newBuilder()
                      .setInitialInterval(Duration.ofSeconds(2))
                      .setBackoffCoefficient(2.0)
                      .setMaximumInterval(Duration.ofMinutes(1))
                      .setMaximumAttempts(10)
                      .build()
              )
              .build()
      );

  // basic workflow state
  private boolean cancelled = false;

  @Override
  public void run(AppointmentNotificationInput input) {
    // Initial “appointment booked” notification
    if (!cancelled) {
      activities.sendAppointmentBooked(input);
    }

    // Keep workflow alive briefly so it can accept signals (reschedule/cancel)
    // You can tune this window; for now 1 hour is fine for dev.
    Workflow.sleep(Duration.ofHours(1));
  }

  @Override
  public void reschedule(AppointmentNotificationInput input) {
    // For now: treat reschedule as “send another booked-style notification”
    // Later you can add a dedicated endpoint/event type like /appointment-rescheduled.
    if (!cancelled) {
      activities.sendAppointmentBooked(input);
    }
  }

  @Override
  public void cancel(String reason) {
    cancelled = true;
    Workflow.getLogger(AppointmentNotificationWorkflowImpl.class)
        .info("Cancel signal received. reason={}", reason);
  }
}
