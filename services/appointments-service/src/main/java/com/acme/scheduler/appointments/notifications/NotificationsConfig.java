package com.acme.scheduler.appointments.notifications;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class NotificationsConfig {

  @Bean
  public RestClient notificationsRestClient(
      RestClient.Builder builder,
      @Value("${notifications.base-url}") String baseUrl,
      @Value("${notifications.connect-timeout-ms:1500}") int connectTimeoutMs,
      @Value("${notifications.read-timeout-ms:2500}") int readTimeoutMs
  ) {
    var rf = new SimpleClientHttpRequestFactory();
    rf.setConnectTimeout(connectTimeoutMs);
    rf.setReadTimeout(readTimeoutMs);

    return builder
        .baseUrl(baseUrl)
        .requestFactory(rf)
        .build();
  }

  @Bean
  public NotificationsClient notificationsClient(RestClient notificationsRestClient) {
    return new HttpNotificationsClient(notificationsRestClient);
  }
}
