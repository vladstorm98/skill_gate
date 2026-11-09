-- V10__add_super_admin_waiting_position_to_constraint.sql

-- Исправляем существующие записи (на всякий случай)
UPDATE users
SET current_state = 'SUPER_ADMIN_MAIN_MENU'
WHERE current_state = 'SUPER_ADMIN_WAITING_POSITION'
  AND status = 'COMPLETED';

-- Удаляем старое ограничение
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_state;

-- Новое ограничение с поддержкой SUPER_ADMIN_WAITING_POSITION
ALTER TABLE users
    ADD CONSTRAINT chk_users_state CHECK (
        (status = 'NEW' AND current_state IN ('NONE', 'REGISTRATION_WAITING_FULL_NAME',
                                              'REGISTRATION_WAITING_PHONE', 'REGISTRATION_WAITING_POSITION', 'REGISTRATION_WAITING_APPROVAL'))
            OR
        (status = 'ONBOARDING' AND current_state IN ('NONE', 'ONBOARDING_WAITING_VIDEO', 'ONBOARDING_WAITING_CHECK'))
            OR
        (status = 'COMPLETED' AND current_state = 'NONE')
            OR
        (status = 'BLOCKED' AND current_state = 'NONE')
            OR
        (current_state LIKE 'ADMIN_%')
            OR
        (current_state LIKE 'SUPER_ADMIN_%')   -- разрешаем все SUPER_ADMIN_*
        );

COMMENT ON CONSTRAINT chk_users_state ON users
    IS 'V10: Added SUPER_ADMIN_WAITING_POSITION support';
