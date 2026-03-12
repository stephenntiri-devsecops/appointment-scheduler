package com.acme.scheduler.appointments.service;

import com.acme.scheduler.appointments.api.CreateAppointmentRequest;
import com.acme.scheduler.appointments.domain.AppointmentEntity;
import com.acme.scheduler.appointments.api.AppointmentJpaRepository;
import com.acme.scheduler.appointments.domain.AppointmentStatus;
import com.acme.scheduler.appointments.idempotency.IdempotencyKeyEntity;
import com.acme.scheduler.appointments.idempotency.IdempotencyKeyRepository;
import com.acme.scheduler.appointments.idempotency.IdempotencyResult;
import com.acme.scheduler.appointments.outbox.OutboxEventEntity;
import com.acme.scheduler.appointments.outbox.OutboxRepository;
import com.acme.scheduler.appointments.temporal.AppointmentNotificationInput;
import com.acme.scheduler.appointments.temporal.AppointmentNotificationWorkflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class AppointmentService {

  private final AppointmentJpaRepository repo;
  private final OutboxRepository outbox;
  private final ObjectMapper om;
  private final WorkflowClient workflowClient;
  private final String taskQueue;
  private final IdempotencyKeyRepository idemRepo;

  public AppointmentService(
      AppointmentJpaRepository repo,
      OutboxRepository outbox,
      ObjectMapper om,
      WorkflowClient workflowClient,
      IdempotencyKeyRepository idemRepo,
      @Value("${app.temporal.taskQueue}") String taskQueue
  ) {
    this.repo = repo;
    this.outbox = outbox;
    this.om = om;
    this.workflowClient = workflowClient;
    this.taskQueue = taskQueue;
    this.idemRepo = idemRepo;
  }

  private static String sha256(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      for (byte b : hash) sb.append(String.format("%02x", b));
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transactional
  public IdempotencyResult createAppointment(String idempotencyKey, CreateAppointmentRequest req) {

    final String reqJson;
    try {
      reqJson = om.writeValueAsString(req);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    final String reqHash = sha256(reqJson);

    // Replay if same key already used
    var existing = idemRepo.findById(idempotencyKey).orElse(null);
    if (existing != null) {
      if (existing.requestHash != null && !existing.requestHash.equals(reqHash)) {
        // semantically better than 400
        throw new IllegalStateException("IDEMPOTENCY_CONFLICT: Idempotency-Key reuse with different request payload.");
      }
      int code = existing.statusCode != null ? existing.statusCode : 201;
      String body = existing.responseBody != null ? existing.responseBody : "{\"id\":\"" + existing.appointmentId + "\",\"status\":\"BOOKED\"}";
      return new IdempotencyResult(code, body);
    }

    // New appointment
    UUID id = UUID.randomUUID();

    AppointmentEntity e = new AppointmentEntity();
    e.id = id;
    e.tenantId = req.tenantId();
    e.clinicId = req.clinicId();
    e.providerId = req.providerId();
    e.patientId = req.patientId();
    e.startTimeUtc = req.startTime();
    e.endTimeUtc = req.endTime();
    e.timeZone = req.timeZone();
    e.status = AppointmentStatus.BOOKED;
    e.contactEmail = req.contact() != null ? req.contact().email() : null;
    e.contactPhone = req.contact() != null ? req.contact().phone() : null;

    try {
      repo.saveAndFlush(e);
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalStateException("SLOT_TAKEN: Slot already booked for provider at this start time.");
    }

    // Build canonical response
    final int statusCode = 201;
    final String responseBody;
    try {
      responseBody = om.writeValueAsString(Map.of(
          "id", id.toString(),
          "status", e.status.name()
      ));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    // Persist idempotency result
    IdempotencyKeyEntity ik = new IdempotencyKeyEntity();
    ik.key = idempotencyKey;
    ik.tenantId = req.tenantId();
    ik.requestHash = reqHash;
    ik.appointmentId = id;
    ik.createdAt = Instant.now();
    ik.statusCode = statusCode;
    ik.responseBody = responseBody;
    idemRepo.save(ik);

    // Outbox
    OutboxEventEntity oe = new OutboxEventEntity();
    oe.id = UUID.randomUUID();
    oe.aggregateType = "Appointment";
    oe.aggregateId = id;
    oe.eventType = "AppointmentBooked";
    oe.createdAt = Instant.now();
    oe.payloadJson = reqJson;
    outbox.save(oe);

    // Temporal workflow start (idempotent by workflowId)
    AppointmentNotificationInput wfInput = new AppointmentNotificationInput(
        id.toString(),
        req.tenantId(),
        req.patientId(),
        req.startTime().toString(),
        req.endTime().toString(),
        req.timeZone(),
        e.contactEmail,
        e.contactPhone
    );

    AppointmentNotificationWorkflow wf = workflowClient.newWorkflowStub(
        AppointmentNotificationWorkflow.class,
        WorkflowOptions.newBuilder()
            .setTaskQueue(taskQueue)
            .setWorkflowId("appointment-" + id)
            .build()
    );
    WorkflowClient.start(wf::run, wfInput);

    return new IdempotencyResult(statusCode, responseBody);
  }

  // ---- B) Read / cancel / reschedule (called by controller below) ----

  public AppointmentEntity getOrThrow(UUID id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("NOT_FOUND: Appointment not found."));
  }

  @Transactional
  public AppointmentEntity cancel(UUID id) {
    AppointmentEntity e = getOrThrow(id);
    if (e.status == AppointmentStatus.CANCELED) return e;
    if (e.status != AppointmentStatus.BOOKED && e.status != AppointmentStatus.RESCHEDULED) {
      throw new IllegalStateException("INVALID_STATE: Only BOOKED/RESCHEDULED appointments can be cancelled.");
    }
    e.status = AppointmentStatus.CANCELED;
    return repo.save(e);
  }

  @Transactional
  public AppointmentEntity reschedule(UUID id, Instant newStartUtc, Instant newEndUtc, String timeZone) {
    AppointmentEntity e = getOrThrow(id);

    if (e.status != AppointmentStatus.BOOKED && e.status != AppointmentStatus.RESCHEDULED) {
      throw new IllegalStateException("INVALID_STATE: Only BOOKED/RESCHEDULED appointments can be rescheduled.");
    }
    if (newStartUtc == null || newEndUtc == null || !newEndUtc.isAfter(newStartUtc)) {
      throw new IllegalArgumentException("BAD_REQUEST: endTime must be after startTime.");
    }

    e.startTimeUtc = newStartUtc;
    e.endTimeUtc = newEndUtc;
    if (timeZone != null && !timeZone.isBlank()) e.timeZone = timeZone;
    e.status = AppointmentStatus.RESCHEDULED;

    try {
      return repo.saveAndFlush(e);
    } catch (DataIntegrityViolationException ex) {
      throw new IllegalStateException("SLOT_TAKEN: Slot already booked for provider at this start time.");
    }
  }
}
