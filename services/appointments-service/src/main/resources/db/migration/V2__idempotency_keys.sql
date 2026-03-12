CREATE TABLE IF NOT EXISTS idempotency_keys (
  id BIGSERIAL PRIMARY KEY,
  idempotency_key VARCHAR(255) NOT NULL UNIQUE,
  request_hash VARCHAR(255),
  response_body TEXT,
  status_code INT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  expires_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_idempotency_keys_created_at
  ON idempotency_keys (created_at);
