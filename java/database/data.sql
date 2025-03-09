BEGIN TRANSACTION;

-- DATA SEEDING

-- Insert default users
INSERT INTO users (username, full_name, email, password_hash, phone, role, activated)
VALUES
    ('johndoe', 'John Doe', 'user@servease.com', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', '1234567890', 'CUSTOMER', TRUE),
    ('adminuser', 'Admin User', 'admin@servease.com', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', '0987654321', 'ADMIN', TRUE)
    RETURNING user_id;

-- Insert default services
INSERT INTO services (name, description, category, base_price)
VALUES
    ('Plumbing', 'Fixing leaks, installing pipes, and more', 'HOME_REPAIR', 50.00),
    ('Electrical', 'Wiring, installations, and repairs', 'HOME_REPAIR', 75.00),
    ('HVAC', 'Heating and cooling system repairs', 'HOME_SERVICES', 100.00)
    RETURNING service_id;

-- Insert service providers (mapping users to services)
INSERT INTO service_providers (user_id, service_id, experience_years, hourly_rate, status)
VALUES
    ((SELECT user_id FROM users WHERE username = 'adminuser'), (SELECT service_id FROM services WHERE name = 'Plumbing'), 5, 60.00, 'ACTIVE'),
    ((SELECT user_id FROM users WHERE username = 'adminuser'), (SELECT service_id FROM services WHERE name = 'Electrical'), 7, 80.00, 'ACTIVE')
    RETURNING provider_id;

-- Insert sample bookings
INSERT INTO bookings (customer_id, provider_id, service_id, status, appointment_time, total_cost)
VALUES
    ((SELECT user_id FROM users WHERE username = 'johndoe'),
     (SELECT provider_id FROM service_providers WHERE service_id = (SELECT service_id FROM services WHERE name = 'Plumbing')),
     (SELECT service_id FROM services WHERE name = 'Plumbing'),
     'CONFIRMED', '2025-03-10 10:00:00', 60.00),

    ((SELECT user_id FROM users WHERE username = 'johndoe'),
     (SELECT provider_id FROM service_providers WHERE service_id = (SELECT service_id FROM services WHERE name = 'Electrical')),
     (SELECT service_id FROM services WHERE name = 'Electrical'),
     'PENDING', '2025-03-11 14:00:00', 80.00)
    RETURNING booking_id;

-- Insert sample payments
INSERT INTO payments (booking_id, amount, payment_method, payment_status, transaction_id)
VALUES
    ((SELECT booking_id FROM bookings WHERE status = 'CONFIRMED'), 60.00, 'CREDIT_CARD', 'COMPLETED', 'TXN123456789'),
    ((SELECT booking_id FROM bookings WHERE status = 'PENDING'), 80.00, 'PAYPAL', 'PENDING', 'TXN987654321');

-- Insert sample reviews
INSERT INTO reviews (booking_id, customer_id, provider_id, rating, comment)
VALUES
    ((SELECT booking_id FROM bookings WHERE status = 'CONFIRMED'),
     (SELECT user_id FROM users WHERE username = 'johndoe'),
     (SELECT provider_id FROM service_providers WHERE service_id = (SELECT service_id FROM services WHERE name = 'Plumbing')),
     5, 'Great service! Fixed my leak quickly.');

-- Insert provider availability
INSERT INTO availability (provider_id, available_day, start_time, end_time)
VALUES
    ((SELECT provider_id FROM service_providers WHERE service_id = (SELECT service_id FROM services WHERE name = 'Plumbing')), 'MONDAY', '08:00:00', '16:00:00'),
    ((SELECT provider_id FROM service_providers WHERE service_id = (SELECT service_id FROM services WHERE name = 'Electrical')), 'TUESDAY', '10:00:00', '18:00:00');

-- Insert sample messages
INSERT INTO messages (sender_id, receiver_id, booking_id, message, is_read)
VALUES
    ((SELECT user_id FROM users WHERE username = 'johndoe'),
     (SELECT user_id FROM users WHERE username = 'adminuser'),
     (SELECT booking_id FROM bookings WHERE status = 'CONFIRMED'),
     'Hey, can we reschedule?', FALSE),

    ((SELECT user_id FROM users WHERE username = 'adminuser'),
     (SELECT user_id FROM users WHERE username = 'johndoe'),
     (SELECT booking_id FROM bookings WHERE status = 'CONFIRMED'),
     'Sure, what time works for you?', TRUE);

COMMIT TRANSACTION;
