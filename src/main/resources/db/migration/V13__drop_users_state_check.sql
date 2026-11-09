-- V13__drop_users_state_check.sql

ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_state;
