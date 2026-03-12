package com.acme.scheduler.notifications.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationsController {

    private static final Logger log = LoggerFactory.getLogger(NotificationsController.class);

    @PostMapping("/appointment-booked")
    public ResponseEntity<?> appointmentBooked(@RequestBody AppointmentBookedEvent event) {
        // For now: just log. Later: send email/SMS
        log.info("Received AppointmentBookedEvent: appointmentId={}, email={}, phone={}, startTime={}, endTime={}, tz={}",
                event.appointmentId(),
                event.contact() != null ? event.contact().email() : null,
                event.contact() != null ? event.contact().phone() : null,
                event.startTime(),
                event.endTime(),
                event.timeZone()
        );

        return ResponseEntity.ok().build();
    }
}
