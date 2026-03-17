-- V4: Bookings

CREATE TYPE booking_status AS ENUM ('PENDING', 'CONFIRMED', 'PAID', 'DELIVERED', 'CANCELLED', 'REFUNDED');

CREATE TABLE bookings (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          UUID NOT NULL REFERENCES users(id),
    concert_id       UUID NOT NULL REFERENCES concerts(id),
    category_id      UUID NOT NULL REFERENCES ticket_categories(id),
    quantity         INTEGER NOT NULL CHECK (quantity > 0),
    unit_price       NUMERIC(15, 2) NOT NULL,
    total_price      NUMERIC(15, 2) NOT NULL,
    status           booking_status NOT NULL DEFAULT 'PENDING',
    idempotency_key  VARCHAR(255) NOT NULL UNIQUE,
    locked_until     TIMESTAMPTZ,
    confirmed_at     TIMESTAMPTZ,
    paid_at          TIMESTAMPTZ,
    cancelled_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
