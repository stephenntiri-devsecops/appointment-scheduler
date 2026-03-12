package com.acme.scheduler.appointments.idempotency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {}
