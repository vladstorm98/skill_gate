-- V5__repair_admin_states.sql

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS chk_users_status;

ALTER TABLE users
    ADD CONSTRAINT chk_users_status CHECK (
        (status = 'NEW'
            AND current_state IN ('NONE',
                                  'REGISTRATION_WAITING_FULL_NAME',
                                  'REGISTRATION_WAITING_PHONE',
                                  'REGISTRATION_WAITING_POSITION'))
            OR
        (status = 'ONBOARDING'
            AND current_state IN ('NONE',
                                  'ONBOARDING_WAITING_VIDEO',
                                  'ONBOARDING_WAITING_CHECK'))
            OR
        status = 'COMPLETED'
            OR
        (status = 'BLOCKED' AND current_state = 'NONE')
        );

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'V4: Deleted states for COMPLETED';
