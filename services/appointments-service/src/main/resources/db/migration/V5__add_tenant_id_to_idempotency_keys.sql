ALTER TABLE idempotency_keys
  ADD COLUMN tenant_id varchar(100);

-- Optional index if you query by tenant + idem key
CREATE INDEX IF NOT EXISTS idx_idempotency_keys_tenant_id
  ON idempotency_keys(tenant_id);

-- Optional: enforce uniqueness per tenant (only if your app expects this)
-- DROP INDEX IF EXISTS idempotency_keys_idempotency_key_key;
-- CREATE UNIQUE INDEX IF NOT EXISTS uq_idempotency_keys_tenant_id_idem_key
--   ON idempotency_keys(tenant_id, idem_key);
