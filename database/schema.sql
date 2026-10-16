CREATE DATABASE IF NOT EXISTS pharmacy_management
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pharmacy_management;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    password_hash CHAR(64) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'PHARMACIST',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medicines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(80) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    expiry_date DATE NOT NULL,
    low_stock_level INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_medicine_price CHECK (price >= 0),
    CONSTRAINT chk_medicine_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_low_stock CHECK (low_stock_level >= 0)
);

-- Demo login: admin / admin123
INSERT INTO users (username, password_hash, role)
SELECT 'admin',
       '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
       'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);