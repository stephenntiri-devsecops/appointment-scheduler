package com.acme.scheduler.appointments.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class AppointmentEntity {

  @Id
  public UUID id;

  @Column(name="tenant_id", nullable=false)
  public String tenantId;

  @Column(name="clinic_id", nullable=false)
  public String clinicId;

  @Column(name="provider_id", nullable=false)
  public String providerId;

  @Column(name="patient_id", nullable=false)
  public String patientId;

  @Column(name="start_time_utc", nullable=false)
  public Instant startTimeUtc;

  @Column(name="end_time_utc", nullable=false)
  public Instant endTimeUtc;

  @Column(name="time_zone", nullable=false)
  public String timeZone;

  @Enumerated(EnumType.STRING)
  @Column(name="status", nullable=false)
  public AppointmentStatus status;

  @Column(name="contact_email")
  public String contactEmail;

  @Column(name="contact_phone")
  public String contactPhone;

  @Version
  public long version;
}
