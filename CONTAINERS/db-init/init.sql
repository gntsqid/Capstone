-- Create the database
CREATE DATABASE IF NOT EXISTS <db name>;

-- Create the table
USE <db name>;

CREATE TABLE IF NOT EXISTS <table name> (
  machine_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  hostname VARCHAR(100) NOT NULL,
  online TINYINT(1) NOT NULL DEFAULT 0,
  parking_lot VARCHAR(50) NOT NULL DEFAULT 'unknown',
  parking_space VARCHAR(50) NOT NULL DEFAULT 'unknown',
  lng FLOAT DEFAULT NULL,
  lat FLOAT DEFAULT NULL,
  type VARCHAR(50) NOT NULL DEFAULT 'unknown',
  parking_space_available TINYINT(1) NOT NULL DEFAULT 0
);

-- Create user (read-only)
CREATE USER IF NOT EXISTS '<username>'@'%' IDENTIFIED BY '<super secret password>';
GRANT SELECT ON <db name>.* TO '<username>'@'%';

-- Create user (full access)
CREATE USER IF NOT EXISTS '<username>'@'%' IDENTIFIED BY '<super secret password>';
GRANT ALL PRIVILEGES ON <db name>.* TO '<username>'@'%';

FLUSH PRIVILEGES;

