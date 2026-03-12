package com.acme.scheduler.appointments.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEventEntity, UUID> {

  @Query(value = "select * from outbox_events where published_at is null order by created_at asc limit :limit", nativeQuery = true)
  List<OutboxEventEntity> findUnpublished(@Param("limit") int limit);
}
