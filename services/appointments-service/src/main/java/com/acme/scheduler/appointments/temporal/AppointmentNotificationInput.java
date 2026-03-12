package com.acme.scheduler.appointments.temporal;

import java.time.Instant;

public record AppointmentNotificationInput(
  String appointmentId,
  String tenantId,
  String patientId,
  String startTimeUtc,
  String endTimeUtc,
  String timeZone,
  String email,
  String phone
) {}
