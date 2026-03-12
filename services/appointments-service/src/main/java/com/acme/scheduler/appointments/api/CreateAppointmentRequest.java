package com.acme.scheduler.appointments.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateAppointmentRequest(
        @NotBlank String tenantId,
        @NotBlank String clinicId,
        @NotBlank String providerId,
        @NotBlank String patientId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        @NotBlank String timeZone,
        @NotNull @Valid Contact contact
) {
    public record Contact(
            @NotBlank @Email String email,
            @NotBlank String phone
    ) {}
}
