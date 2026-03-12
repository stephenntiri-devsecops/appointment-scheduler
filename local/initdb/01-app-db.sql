CREATE USER scheduler WITH PASSWORD 'scheduler';
CREATE DATABASE scheduler OWNER scheduler;
GRANT ALL PRIVILEGES ON DATABASE scheduler TO scheduler;
