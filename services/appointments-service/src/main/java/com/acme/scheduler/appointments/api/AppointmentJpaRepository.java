package com.acme.scheduler.appointments.api;

import com.acme.scheduler.appointments.domain.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {}
