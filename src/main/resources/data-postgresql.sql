INSERT INTO booking_status (status_name) VALUES
('WAITING'),
('APPROVED'),
('REJECTED'),
('CANCELED')
ON CONFLICT (status_name) DO NOTHING;