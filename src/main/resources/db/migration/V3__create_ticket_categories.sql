-- V3: Ticket Categories

CREATE TABLE ticket_categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    concert_id      UUID NOT NULL REFERENCES concerts(id) ON DELETE CASCADE,
    name            VARCHAR(100) NOT NULL,
    base_price      NUMERIC(15, 2) NOT NULL CHECK (base_price >= 0),
    capacity        INTEGER NOT NULL CHECK (capacity > 0),
    available_stock INTEGER NOT NULL CHECK (available_stock >= 0),
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ticket_cat_concert FOREIGN KEY (concert_id) REFERENCES concerts(id),
    CONSTRAINT chk_stock_lte_capacity CHECK (available_stock <= capacity)
);
