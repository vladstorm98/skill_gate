-- V7__add_super_admin_waiting_position.sql

-- Обновляем чек-констрейнт, добавляя новое состояние SUPER_ADMIN_WAITING_POSITION

ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_status;

ALTER TABLE users
    ADD CONSTRAINT chk_users_status CHECK (
        -- NEW (регистрация)
        (status = 'NEW'
            AND current_state IN ('NONE',
                                  'REGISTRATION_WAITING_FULL_NAME',
                                  'REGISTRATION_WAITING_PHONE',
                                  'REGISTRATION_WAITING_POSITION',
                                  'REGISTRATION_WAITING_APPROVAL'))
            OR
            -- ONBOARDING
        (status = 'ONBOARDING'
            AND current_state IN ('NONE',
                                  'ONBOARDING_WAITING_VIDEO',
                                  'ONBOARDING_WAITING_CHECK'))
            OR
            -- COMPLETED
        (status = 'COMPLETED' AND current_state = 'NONE')
            OR
            -- BLOCKED
        (status = 'BLOCKED' AND current_state = 'NONE')
            OR
            -- Админские состояния
        (current_state LIKE 'ADMIN_%')
            OR
            -- Super Admin состояния
        (current_state LIKE 'SUPER_ADMIN_%')
        );

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'V7: Added SUPER_ADMIN_WAITING_POSITION state';
