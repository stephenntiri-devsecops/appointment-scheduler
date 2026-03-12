package com.acme.scheduler.appointments.api;

import java.util.UUID;

public record AppointmentResponse(UUID id, String status) {}
