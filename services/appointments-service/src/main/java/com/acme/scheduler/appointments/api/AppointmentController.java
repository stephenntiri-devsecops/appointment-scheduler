package com.acme.scheduler.appointments.api;

import com.acme.scheduler.appointments.domain.AppointmentEntity;
import com.acme.scheduler.appointments.domain.AppointmentStatus;
import com.acme.scheduler.appointments.payments.StripePaymentsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@Validated
public class AppointmentController {

  private final AppointmentJpaRepository repo;
  private final StripePaymentsService stripe;

  public AppointmentController(AppointmentJpaRepository repo, StripePaymentsService stripe) {
    this.repo = repo;
    this.stripe = stripe;
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateAppointmentRequest r) {
    AppointmentEntity e = new AppointmentEntity();
    e.id = UUID.randomUUID();
    e.tenantId = r.tenantId();
    e.clinicId = r.clinicId();
    e.providerId = r.providerId();
    e.patientId = r.patientId();
    e.startTimeUtc = Instant.parse(r.startTime());
    e.endTimeUtc = Instant.parse(r.endTime());
    e.timeZone = r.timeZone();
    e.contactEmail = r.contact().email();
    e.contactPhone = r.contact().phone();
    e.status = AppointmentStatus.BOOKED;

    repo.save(e);

    return ResponseEntity.status(201).body(Map.of(
        "id", e.id.toString(),
        "status", e.status.name()
    ));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> get(@PathVariable("id") UUID id) {
    AppointmentEntity e = repo.findById(id).orElse(null);
    if (e == null) return ResponseEntity.notFound().build();
    return ResponseEntity.ok(toDto(e));
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<Map<String, Object>> cancel(@PathVariable("id") UUID id) {
    AppointmentEntity e = repo.findById(id).orElse(null);
    if (e == null) return ResponseEntity.notFound().build();

    if (e.status != AppointmentStatus.CANCELED) {
      e.status = AppointmentStatus.CANCELED;
      repo.save(e);
    }
    return ResponseEntity.ok(toDto(e));
  }

  @PostMapping("/{id}/reschedule")
  public ResponseEntity<Map<String, Object>> reschedule(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody RescheduleRequest r) {
    AppointmentEntity e = repo.findById(id).orElse(null);
    if (e == null) return ResponseEntity.notFound().build();

    e.startTimeUtc = Instant.parse(r.startTime());
    e.endTimeUtc = Instant.parse(r.endTime());
    e.timeZone = r.timeZone();
    e.status = AppointmentStatus.RESCHEDULED;
    repo.save(e);

    return ResponseEntity.ok(toDto(e));
  }

  private static Map<String, Object> toDto(AppointmentEntity e) {
    // LinkedHashMap avoids Map.of() limits and keeps order readable
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", e.id != null ? e.id.toString() : null);
    m.put("tenantId", e.tenantId);
    m.put("clinicId", e.clinicId);
    m.put("providerId", e.providerId);
    m.put("patientId", e.patientId);
    m.put("startTime", e.startTimeUtc != null ? e.startTimeUtc.toString() : null);
    m.put("endTime", e.endTimeUtc != null ? e.endTimeUtc.toString() : null);
    m.put("timeZone", e.timeZone);
    m.put("contactEmail", e.contactEmail);
    m.put("contactPhone", e.contactPhone);
    m.put("status", e.status != null ? e.status.name() : null);
    return m;
  }

  // ----- DTOs (kept here to reduce file count + avoid syntax issues) -----

  public record Contact(@NotBlank String email, @NotBlank String phone) {}

  public record CreateAppointmentRequest(
      @NotBlank String tenantId,
      @NotBlank String clinicId,
      @NotBlank String providerId,
      @NotBlank String patientId,
      @NotBlank String startTime,
      @NotBlank String endTime,
      @NotBlank String timeZone,
      @NotNull @Valid Contact contact
  ) {}

  public record RescheduleRequest(
      @NotBlank String startTime,
      @NotBlank String endTime,
      @NotBlank String timeZone
  ) {}

  public record PaymentIntentRequest(
      @Positive long amountCents,
      @NotBlank String currency,
      String customerEmail
  ) {}
}
