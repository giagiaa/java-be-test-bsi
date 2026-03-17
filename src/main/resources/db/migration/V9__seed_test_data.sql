-- V9: Seed Sample Concert Data
INSERT INTO concerts (id, name, artist, venue, datetime, capacity, status, created_by)
VALUES ('c0000000-0000-0000-0000-000000000001', 'Java Jazz Festival', 'Various Artists', 'JIExpo Kemayoran', '2026-05-20T19:00:00+07:00', 5000, 'UPCOMING', '00000000-0000-0000-0000-000000000001');

INSERT INTO ticket_categories (id, concert_id, name, base_price, capacity, available_stock)
VALUES 
('f0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001', 'VIP', 1500000, 100, 100),
('f0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001', 'Festival', 750000, 1000, 1000);
