package com.acme.scheduler.appointments.notifications;

import com.acme.scheduler.appointments.notifications.dto.AppointmentBookedEvent;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class HttpNotificationsClient implements NotificationsClient {

  private final RestClient restClient;

  public HttpNotificationsClient(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public void appointmentBooked(AppointmentBookedEvent event) {
    restClient.post()
        .uri("/api/v1/notifications/appointment-booked")
        .contentType(MediaType.APPLICATION_JSON)
        .body(event)
        .retrieve()
        .toBodilessEntity();
  }
}
