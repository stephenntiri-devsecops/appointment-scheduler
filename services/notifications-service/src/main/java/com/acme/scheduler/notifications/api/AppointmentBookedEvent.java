package com.acme.scheduler.notifications.api;

public record AppointmentBookedEvent(
        String appointmentId,     // <-- String, not UUID
        Contact contact,
        String startTime,
        String endTime,
        String timeZone
) {}
