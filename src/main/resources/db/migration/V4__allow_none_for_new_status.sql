-- V4__Allow_none_for_new_status.sql

-- 1. Исправляем существующие проблемные записи
UPDATE users
SET current_state = 'REGISTRATION_WAITING_FULL_NAME'
WHERE status = 'NEW' AND current_state = 'NONE';

-- 2. Удаляем текущее ограничение
ALTER TABLE users
    DROP CONSTRAINT IF EXISTS chk_users_status;

-- 3. Новое ограничение с разрешением NONE для NEW
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
        (status = 'COMPLETED' AND current_state = 'NONE')
            OR
        (status = 'BLOCKED' AND current_state = 'NONE')
        );

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'V4: Allowed NONE for NEW status';
