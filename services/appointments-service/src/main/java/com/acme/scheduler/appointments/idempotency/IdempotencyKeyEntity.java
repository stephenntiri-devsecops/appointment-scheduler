package com.acme.scheduler.appointments.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "idempotency_keys",
    indexes = {
        @Index(name = "idx_idempotency_tenant", columnList = "tenant_id")
    }
)
public class IdempotencyKeyEntity {

  @Id
  @Column(name = "idem_key", nullable = false, length = 128)
  public String key;

  @Column(name = "tenant_id", nullable = false, length = 64)
  public String tenantId;

  @Column(name = "request_hash", nullable = true, length = 64)
  public String requestHash;

  @Column(name = "appointment_id", nullable = true)
  public UUID appointmentId;

  @Column(name = "created_at", nullable = false)
  public Instant createdAt;

  @Column(name = "status_code")
  public Integer statusCode;

  @Column(name = "response_body", columnDefinition = "text")
  public String responseBody;
}
