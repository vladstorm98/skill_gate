-- V12__drop_status_state_bindings.sql

-- Удаляем все CHECK-ограничения, которые связывают status и current_state
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_status;
ALTER TABLE users DROP CONSTRAINT IF EXISTS chk_users_state;

-- Опционально: оставляем только простые проверки на допустимые значения
ALTER TABLE users
    ADD CONSTRAINT chk_users_status
        CHECK (status IN ('NEW', 'ONBOARDING', 'COMPLETED', 'BLOCKED'));

ALTER TABLE users
    ADD CONSTRAINT chk_users_state
        CHECK (current_state IN (
                                 'NONE',
                                 'REGISTRATION_WAITING_FULL_NAME',
                                 'REGISTRATION_WAITING_PHONE',
                                 'REGISTRATION_WAITING_POSITION',
                                 'REGISTRATION_WAITING_APPROVAL',
                                 'ONBOARDING_WAITING_VIDEO',
                                 'ONBOARDING_WAITING_CHECK',
                                 'KNOWLEDGE_BASE_NAVIGATION',
                                 'ADMIN_MAIN_MENU',
                                 'SELECTING_USER_FOR_EDIT',
                                 'WAITING_NEW_ROLE',
                                 'WAITING_NEW_STATUS',
                                 'WAITING_NEW_POSITION',
                                 'SUPER_ADMIN_MAIN_MENU',
                                 'SUPER_ADMIN_WAITING_NEW_LESSON_POSITION',
                                 'SUPER_ADMIN_SELECTING_LESSON_POSITION',
                                 'SUPER_ADMIN_SELECTING_LESSON',
                                 'SUPER_ADMIN_SELECTING_LESSON_FIELD',
                                 'SUPER_ADMIN_EDITING_LESSON_TITLE',
                                 'SUPER_ADMIN_EDITING_LESSON_VIDEO_URL',
                                 'SUPER_ADMIN_EDITING_LESSON_SUMMARY',
                                 'SUPER_ADMIN_EDITING_LESSON_NUMBER',
                                 'SUPER_ADMIN_LESSON_DELETE_CONFIRM'
            ));

COMMENT ON CONSTRAINT chk_users_status ON users
    IS 'Allowed user statuses only, no binding to state';

COMMENT ON CONSTRAINT chk_users_state ON users
    IS 'Allowed user states only, no binding to status';
