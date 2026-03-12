package com.acme.scheduler.appointments.notifications.dto;

import java.util.UUID;

public record AppointmentBookedEvent(
        UUID appointmentId,
        Contact contact,
        String startTime,
        String endTime,
        String timeZone
) {}
