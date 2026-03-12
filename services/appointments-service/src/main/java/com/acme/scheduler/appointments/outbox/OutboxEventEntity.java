package com.acme.scheduler.appointments.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {
  @Id
  public UUID id;

  @Column(name="aggregate_type", nullable=false)
  public String aggregateType;

  @Column(name="aggregate_id", nullable=false)
  public UUID aggregateId;

  @Column(name="event_type", nullable=false)
  public String eventType;

  @Column(name="payload_json", nullable=false, columnDefinition="text")
  public String payloadJson;

  @Column(name="created_at", nullable=false)
  public Instant createdAt;

  @Column(name="published_at")
  public Instant publishedAt;
}
