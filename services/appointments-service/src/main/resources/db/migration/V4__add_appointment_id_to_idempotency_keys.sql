-- Add appointment_id as UUID because appointments.id is UUID
ALTER TABLE idempotency_keys
  ADD COLUMN appointment_id uuid;

-- Optional FK
ALTER TABLE idempotency_keys
  ADD CONSTRAINT fk_idempotency_keys_appointment
  FOREIGN KEY (appointment_id)
  REFERENCES appointments(id)
  ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_idempotency_keys_appointment_id
  ON idempotency_keys(appointment_id);
