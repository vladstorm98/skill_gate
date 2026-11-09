-- V3__Fix_users_status_constraint.sql

UPDATE users
SET status = 'NEW',
    current_state = 'REGISTRATION_WAITING_FULL_NAME'
WHERE status = 'PENDING_APPROVAL';

UPDATE users
SET current_state = 'NONE'
WHERE status = 'COMPLETED' AND current_state != 'NONE';

UPDATE users
SET current_state = 'NONE'
WHERE status = 'BLOCKED' AND current_state != 'NONE';

ALTER TABLE users
    DROP CONSTRAINT IF EXISTS chk_users_status;

ALTER TABLE users
    ADD CONSTRAINT chk_users_status CHECK (
        (status = 'NEW' AND current_state IN ('REGISTRATION_WAITING_FULL_NAME',
                                              'REGISTRATION_WAITING_PHONE',
                                              'REGISTRATION_WAITING_POSITION'))
            OR

        (status IN ('ONBOARDING') AND current_state IN ('NONE',
                                                        'ONBOARDING_WAITING_VIDEO',
                                                        'ONBOARDING_WAITING_CHECK'))
            OR

        (status = 'COMPLETED' AND current_state = 'NONE')
            OR

        (status = 'BLOCKED' AND current_state = 'NONE')
    );

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'Final constraint: NEW instead of PENDING_APPROVAL, APPROVED and REGISTERED removed';
