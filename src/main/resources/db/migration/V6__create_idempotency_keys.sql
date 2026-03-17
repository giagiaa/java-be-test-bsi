-- V6: Idempotency Keys

CREATE TABLE idempotency_keys (
    key            VARCHAR(255) PRIMARY KEY,
    response_body  TEXT,
    status_code    INTEGER,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for cleanup
CREATE INDEX idx_idempotency_keys_created_at ON idempotency_keys(created_at);
