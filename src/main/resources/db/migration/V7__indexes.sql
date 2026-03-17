-- V7: Indexes

-- Concerts
CREATE INDEX idx_concerts_name ON concerts(name);
CREATE INDEX idx_concerts_artist ON concerts(artist);
CREATE INDEX idx_concerts_datetime ON concerts(datetime);
CREATE INDEX idx_concerts_status ON concerts(status);

-- Bookings
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_idempotency_key ON bookings(idempotency_key);
CREATE INDEX idx_bookings_locked_until ON bookings(locked_until) WHERE status = 'PENDING';

-- Ticket Categories
CREATE INDEX idx_ticket_categories_concert_id ON ticket_categories(concert_id);

-- Ledger
CREATE INDEX idx_ledger_booking_id ON ledger(booking_id);
CREATE INDEX idx_ledger_user_id ON ledger(user_id);
CREATE INDEX idx_ledger_created_at ON ledger(created_at);
