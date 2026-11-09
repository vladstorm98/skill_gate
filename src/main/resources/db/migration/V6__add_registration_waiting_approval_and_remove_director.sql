-- V6__add_registration_waiting_approval_and_remove_director.sql

UPDATE users
SET role = 'ADMIN'
WHERE role = 'DIRECTOR';

ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_status;

ALTER TABLE users
    ADD CONSTRAINT chk_users_status CHECK (
        (status = 'NEW'
            AND current_state IN ('NONE',
                                  'REGISTRATION_WAITING_FULL_NAME',
                                  'REGISTRATION_WAITING_PHONE',
                                  'REGISTRATION_WAITING_POSITION',
                                  'REGISTRATION_WAITING_APPROVAL'))
            OR
        (status = 'ONBOARDING'
            AND current_state IN ('NONE',
                                  'ONBOARDING_WAITING_VIDEO',
                                  'ONBOARDING_WAITING_CHECK'))
            OR
        (status = 'COMPLETED' AND current_state = 'NONE')
            OR
        (status = 'BLOCKED' AND current_state = 'NONE')
            OR
        (current_state LIKE 'ADMIN_%')
        );

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'V6: Added REGISTRATION_WAITING_APPROVAL, removed DIRECTOR role';
