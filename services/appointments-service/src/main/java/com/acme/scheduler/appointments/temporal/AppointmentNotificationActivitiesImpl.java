package com.acme.scheduler.appointments.temporal;

import com.acme.scheduler.appointments.notifications.NotificationsClient;
import com.acme.scheduler.appointments.notifications.dto.AppointmentBookedEvent;
import com.acme.scheduler.appointments.notifications.dto.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AppointmentNotificationActivitiesImpl implements AppointmentNotificationActivities {

  private static final Logger log = LoggerFactory.getLogger(AppointmentNotificationActivitiesImpl.class);

  private final NotificationsClient notificationsClient;

  public AppointmentNotificationActivitiesImpl(NotificationsClient notificationsClient) {
    this.notificationsClient = notificationsClient;
  }

  @Override
  public void sendAppointmentBooked(AppointmentNotificationInput input) {
    var event = new AppointmentBookedEvent(
        UUID.fromString(input.appointmentId()),
        new Contact(input.email(), input.phone()),
        input.startTimeUtc(),
        input.endTimeUtc(),
        input.timeZone()
    );

    notificationsClient.appointmentBooked(event);

    log.info("Sent AppointmentBookedEvent to notifications-service: appointmentId={}", input.appointmentId());
  }
}
