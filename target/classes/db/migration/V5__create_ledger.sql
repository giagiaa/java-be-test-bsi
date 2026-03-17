-- V5: Ledger

CREATE TABLE ledger (
    ledger_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id     UUID NOT NULL REFERENCES bookings(id),
    user_id        UUID NOT NULL REFERENCES users(id),
    amount         NUMERIC(15, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status         VARCHAR(50) NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Trigger to prevent UPDATE and DELETE on ledger
CREATE OR REPLACE FUNCTION protect_ledger_immutability()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Updates or deletes are not allowed on the ledger table.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_protect_ledger_immutability
BEFORE UPDATE OR DELETE ON ledger
FOR EACH ROW EXECUTE FUNCTION protect_ledger_immutability();
