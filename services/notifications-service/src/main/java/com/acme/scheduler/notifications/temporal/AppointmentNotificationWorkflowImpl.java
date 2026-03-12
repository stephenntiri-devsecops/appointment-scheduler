package com.acme.scheduler.notifications.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class AppointmentNotificationWorkflowImpl implements AppointmentNotificationWorkflow {

  private volatile AppointmentNotificationInput current;
  private volatile boolean canceled = false;

  private final NotificationActivities activities = Workflow.newActivityStub(
    NotificationActivities.class,
    ActivityOptions.newBuilder()
      .setStartToCloseTimeout(Duration.ofSeconds(30))
      .setRetryOptions(RetryOptions.newBuilder()
        .setInitialInterval(Duration.ofSeconds(1))
        .setBackoffCoefficient(2.0)
        .setMaximumInterval(Duration.ofMinutes(2))
        .setMaximumAttempts(10)
        .build())
      .build()
  );

  @Override
  public void run(AppointmentNotificationInput input) {
    this.current = input;

    // 1) Confirmation now
    activities.sendConfirmation(current);

    // 2) Reminders (example): 24h and 2h before start
    scheduleReminder(Duration.ofHours(24), "T-24H");
    scheduleReminder(Duration.ofHours(2), "T-2H");

    // 3) Follow-up: 2h after appointment start (example)
    if (!canceled) {
      // Workflow.sleep(Duration.between(Workflow.now(), current.startTimeUtc().plus(Duration.ofHours(2))));
      Workflow.sleep(Duration.between(
    java.time.Instant.ofEpochMilli(Workflow.currentTimeMillis()),
    current.startTimeUtc().plus(Duration.ofHours(2))
));
       if (!canceled) activities.sendFollowUp(current);
    }
  }

  private void scheduleReminder(Duration before, String type) {
    if (canceled) return;
    // Duration wait = Duration.between(Workflow.now(), current.startTimeUtc().minus(before));
    Duration wait = Duration.between(
    java.time.Instant.ofEpochMilli(Workflow.currentTimeMillis()),
    current.startTimeUtc().minus(before)
);
    if (wait.isNegative() || wait.isZero()) return;
    Workflow.sleep(wait);
    if (!canceled) activities.sendReminder(current, type);
  }

  @Override
  public void reschedule(AppointmentNotificationInput input) {
    this.current = input;
    // Production: cancel pending timers and re-plan; easiest is to use Workflow.newTimer and cancellation scopes.
  }

  @Override
  public void cancel(String reason) {
    this.canceled = true;
    activities.recordAttempt(current.appointmentId(), "CANCEL", "OK", reason);
  }
}
