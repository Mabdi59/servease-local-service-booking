BEGIN TRANSACTION;

-- USERS TABLE (Customers, Service Providers, Admins)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       role VARCHAR(20) CHECK (role IN ('CUSTOMER', 'SERVICE_PROVIDER', 'ADMIN')) NOT NULL,
                       activated BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SERVICES TABLE (List of services offered)
DROP TABLE IF EXISTS services;
CREATE TABLE services (
                          service_id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          category VARCHAR(50) NOT NULL,
                          base_price DECIMAL(10,2) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SERVICE PROVIDER TABLE (Mapping providers to services)
DROP TABLE IF EXISTS service_providers;
CREATE TABLE service_providers (
                                   provider_id SERIAL PRIMARY KEY,
                                   user_id INT NOT NULL,
                                   service_id INT NOT NULL,
                                   experience_years INT CHECK (experience_years >= 0),
                                   hourly_rate DECIMAL(10,2) NOT NULL,
                                   status VARCHAR(20) CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING')) DEFAULT 'PENDING',
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                   FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- BOOKINGS TABLE (Service requests from customers)
DROP TABLE IF EXISTS bookings;
CREATE TABLE bookings (
                          booking_id SERIAL PRIMARY KEY,
                          customer_id INT NOT NULL,
                          provider_id INT NOT NULL,
                          service_id INT NOT NULL,
                          status VARCHAR(20) CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED')) DEFAULT 'PENDING',
                          appointment_time TIMESTAMP NOT NULL,
                          total_cost DECIMAL(10,2),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (provider_id) REFERENCES service_providers(provider_id) ON DELETE CASCADE,
                          FOREIGN KEY (service_id) REFERENCES services(service_id) ON DELETE CASCADE
);

-- PAYMENTS TABLE (Tracking payments for services)
DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
                          payment_id SERIAL PRIMARY KEY,
                          booking_id INT NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          payment_method VARCHAR(50) CHECK (payment_method IN ('CREDIT_CARD', 'PAYPAL', 'CASH', 'BANK_TRANSFER')) NOT NULL,
                          payment_status VARCHAR(20) CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')) DEFAULT 'PENDING',
                          transaction_id VARCHAR(100) UNIQUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- REVIEWS TABLE (Customers reviewing service providers)
DROP TABLE IF EXISTS reviews;
CREATE TABLE reviews (
                         review_id SERIAL PRIMARY KEY,
                         booking_id INT NOT NULL,
                         customer_id INT NOT NULL,
                         provider_id INT NOT NULL,
                         rating INT CHECK (rating BETWEEN 1 AND 5) NOT NULL,
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE,
                         FOREIGN KEY (customer_id) REFERENCES users(user_id) ON DELETE CASCADE,
                         FOREIGN KEY (provider_id) REFERENCES service_providers(provider_id) ON DELETE CASCADE
);

-- AVAILABILITY TABLE (Service provider schedules)
DROP TABLE IF EXISTS availability;
CREATE TABLE availability (
                              availability_id SERIAL PRIMARY KEY,
                              provider_id INT NOT NULL,
                              available_day VARCHAR(20) CHECK (available_day IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')) NOT NULL,
                              start_time TIME NOT NULL,
                              end_time TIME NOT NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (provider_id) REFERENCES service_providers(provider_id) ON DELETE CASCADE
);

-- MESSAGES TABLE (Customer-Service Provider Chat)
DROP TABLE IF EXISTS messages;
CREATE TABLE messages (
                          message_id SERIAL PRIMARY KEY,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          booking_id INT,
                          message TEXT NOT NULL,
                          is_read BOOLEAN DEFAULT FALSE,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (receiver_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE SET NULL
);

COMMIT TRANSACTION;
