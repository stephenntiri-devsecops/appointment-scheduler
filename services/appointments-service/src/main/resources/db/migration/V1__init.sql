-- V1__init.sql

CREATE TABLE IF NOT EXISTS appointments (
  id              UUID PRIMARY KEY,
  tenant_id       TEXT NOT NULL,
  clinic_id       TEXT NOT NULL,
  provider_id     TEXT NOT NULL,
  patient_id      TEXT NOT NULL,
  start_time_utc  TIMESTAMPTZ NOT NULL,
  end_time_utc    TIMESTAMPTZ NOT NULL,
  time_zone       TEXT NOT NULL,
  status          TEXT NOT NULL,
  contact_email   TEXT,
  contact_phone   TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  version         BIGINT NOT NULL DEFAULT 0
);

-- Simple protection for discrete slot booking:
-- If you use arbitrary ranges, prefer Postgres EXCLUDE constraints with tsrange/tstzrange.
CREATE UNIQUE INDEX IF NOT EXISTS uq_provider_start ON appointments(provider_id, start_time_utc)
  WHERE status IN ('BOOKED','RESCHEDULED');

CREATE TABLE IF NOT EXISTS outbox_events (
  id              UUID PRIMARY KEY,
  aggregate_type  TEXT NOT NULL,
  aggregate_id    UUID NOT NULL,
  event_type      TEXT NOT NULL,
  payload_json    TEXT NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  published_at    TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS audit_log (
  id          UUID PRIMARY KEY,
  tenant_id   TEXT NOT NULL,
  actor       TEXT NOT NULL,
  action      TEXT NOT NULL,
  target_id   TEXT,
  details     TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
