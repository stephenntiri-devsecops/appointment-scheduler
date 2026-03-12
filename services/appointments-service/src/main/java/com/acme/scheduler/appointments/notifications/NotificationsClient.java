package com.acme.scheduler.appointments.notifications;

import com.acme.scheduler.appointments.notifications.dto.AppointmentBookedEvent;

public interface NotificationsClient {
  void appointmentBooked(AppointmentBookedEvent event);
}
