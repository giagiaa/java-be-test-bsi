-- V2: Concerts

CREATE TYPE concert_status AS ENUM ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED');

CREATE TABLE concerts (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    artist      VARCHAR(255) NOT NULL,
    venue       VARCHAR(500) NOT NULL,
    datetime    TIMESTAMPTZ NOT NULL,
    timezone    VARCHAR(100) NOT NULL DEFAULT 'UTC',
    capacity    INTEGER NOT NULL CHECK (capacity > 0),
    status      concert_status NOT NULL DEFAULT 'UPCOMING',
    created_by  UUID REFERENCES users(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
