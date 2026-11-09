-- V9__update_super_admin_lesson_states.sql

-- 1. Обновляем существующие записи пользователей (переводим старые состояния на новые)
UPDATE users
SET current_state = 'SUPER_ADMIN_MAIN_MENU'
WHERE current_state IN (
                        'SUPER_ADMIN_WAITING_POSITION',
                        'SUPER_ADMIN_LESSON_EDIT_SELECT_LESSON',
                        'SUPER_ADMIN_LESSON_EDIT_SELECT_FIELD',
                        'SUPER_ADMIN_LESSON_EDIT_WAIT_VALUE',
                        'SUPER_ADMIN_LESSON_DELETE_CONFIRM'
    );

-- 2. Удаляем старое ограничение
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_state;

-- 3. Новое ограничение с обновлёнными состояниями
ALTER TABLE users
    ADD CONSTRAINT chk_users_state CHECK (
        -- NEW (регистрация)
        (status = 'NEW' AND current_state IN ('NONE', 'REGISTRATION_WAITING_FULL_NAME',
                                              'REGISTRATION_WAITING_PHONE', 'REGISTRATION_WAITING_POSITION', 'REGISTRATION_WAITING_APPROVAL'))
            OR
            -- ONBOARDING
        (status = 'ONBOARDING' AND current_state IN ('NONE', 'ONBOARDING_WAITING_VIDEO', 'ONBOARDING_WAITING_CHECK'))
            OR
            -- COMPLETED
        (status = 'COMPLETED' AND current_state = 'NONE')
            OR
            -- BLOCKED
        (status = 'BLOCKED' AND current_state = 'NONE')
            OR
            -- Обычные админские состояния
        (current_state LIKE 'ADMIN_%')
            OR
            -- Super Admin состояния
        (current_state IN (
                           'SUPER_ADMIN_MAIN_MENU',
                           'SUPER_ADMIN_WAITING_NEW_LESSON_POSITION',   -- изменено
                           'SUPER_ADMIN_SELECTING_LESSON_POSITION',
                           'SUPER_ADMIN_SELECTING_LESSON',
                           'SUPER_ADMIN_SELECTING_LESSON_FIELD',
                           'SUPER_ADMIN_EDITING_LESSON_TITLE',
                           'SUPER_ADMIN_EDITING_LESSON_VIDEO_URL',
                           'SUPER_ADMIN_EDITING_LESSON_SUMMARY',
                           'SUPER_ADMIN_EDITING_LESSON_NUMBER',         -- новое
                           'SUPER_ADMIN_LESSON_DELETE_CONFIRM'          -- подтверждение удаления
            ))
            OR
        (current_state LIKE 'SUPER_ADMIN_%')   -- страховка
        );

COMMENT ON CONSTRAINT chk_users_state ON users
    IS 'V9: Updated Super Admin lesson states. Renamed WAITING_POSITION, added EDITING_LESSON_NUMBER';
