MERGE INTO booking_status (status_name)
KEY(status_name)
VALUES
('WAITING'),
('APPROVED'),
('REJECTED'),
('CANCELED');