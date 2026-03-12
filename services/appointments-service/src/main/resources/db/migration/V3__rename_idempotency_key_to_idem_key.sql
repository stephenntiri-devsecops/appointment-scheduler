-- Rename column to match JPA mapping (Hibernate expects idem_key)
ALTER TABLE idempotency_keys
  RENAME COLUMN idempotency_key TO idem_key;
