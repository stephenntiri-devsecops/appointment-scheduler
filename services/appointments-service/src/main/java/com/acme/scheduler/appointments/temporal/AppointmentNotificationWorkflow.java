package com.acme.scheduler.appointments.temporal;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface AppointmentNotificationWorkflow {
  @WorkflowMethod
  void run(AppointmentNotificationInput input);

  @SignalMethod
  void reschedule(AppointmentNotificationInput input);

  @SignalMethod
  void cancel(String reason);
}
