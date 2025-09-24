-- PostgreSQL Database Initialization Script
-- This script creates the database and user for the Dynamic Consent application

-- Create database (run as postgres superuser)
-- CREATE DATABASE dynamicconsent;

-- Create user and grant privileges
-- CREATE USER dynamicconsent WITH PASSWORD 'your_secure_password_here';
-- GRANT ALL PRIVILEGES ON DATABASE dynamicconsent TO dynamicconsent;

-- Connect to the database and set up schema
-- \c dynamicconsent;

-- Grant schema privilegesgit
-- GRANT ALL ON SCHEMA public TO dynamicconsent;
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO dynamicconsent;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO dynamicconsent;

-- Set default privileges for future objects
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO dynamicconsent;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO dynamicconsent;

-- Note: The actual table creation will be handled by JPA/Hibernate
-- This script is for manual database setup if needed
