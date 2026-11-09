-- V1__initial_schema.sql

-- =============================================
-- Positions
-- =============================================
CREATE TABLE positions
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        TEXT                                   NOT NULL,
    description TEXT,

    CONSTRAINT uc_positions_name UNIQUE (name)
);

-- =============================================
-- Users
-- =============================================
CREATE TABLE users
(
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    telegram_id           BIGINT                                 NOT NULL,
    full_name             TEXT,
    phone                 TEXT,
    position_id           BIGINT,
    role                  TEXT DEFAULT 'EMPLOYEE'                NOT NULL,
    status                TEXT DEFAULT 'NEW'                     NOT NULL,
    current_state         TEXT DEFAULT 'NONE'                    NOT NULL,
    current_lesson_number BIGINT DEFAULT 1,
    onboarding_progress   DECIMAL(5,2) DEFAULT 0.00,
    created_at            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uc_users_telegram_id UNIQUE (telegram_id),
    CONSTRAINT uc_users_phone UNIQUE (phone),
    CONSTRAINT fk_users_on_position FOREIGN KEY (position_id)
        REFERENCES positions (id) ON DELETE SET NULL,

    CONSTRAINT chk_users_role CHECK (role IN (
        'DIRECTOR',
        'SUPER_ADMIN',
        'ADMIN',
        'EMPLOYEE'
    )),
    CONSTRAINT chk_users_status CHECK (status IN (
        'NEW',
        'REGISTERED'
        'PENDING_APPROVAL',
        'ONBOARDING',
        'COMPLETED',
        'BLOCKED'
    )),
    CONSTRAINT chk_users_state CHECK (current_state IN (
        'NONE',
        'REGISTRATION_WAITING_FULL_NAME',
        'REGISTRATION_WAITING_PHONE',
        'REGISTRATION_WAITING_POSITION',
        'ONBOARDING_WAITING_VIDEO',
        'ONBOARDING_WAITING_CHECK',
        'KNOWLEDGE_BASE_NAVIGATION',
        'ADMIN_MAIN_MENU',
        'ADMIN_SELECTING_USER_FOR_EDIT',
        'ADMIN_WAITING_NEW_ROLE',
        'ADMIN_WAITING_NEW_STATUS',
        'ADMIN_WAITING_NEW_POSITION'
    ))
);

CREATE INDEX idx_users_telegram_id ON users (telegram_id);
CREATE INDEX idx_users_status      ON users (status);
CREATE INDEX idx_users_position    ON users (position_id);
CREATE INDEX idx_users_phone       ON users (phone);

-- =============================================
-- Onboarding Lessons
-- =============================================
CREATE TABLE lessons
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    position_id  BIGINT                                 NOT NULL,
    number       BIGINT                                 NOT NULL,
    title        TEXT                                   NOT NULL,
    video_url    TEXT,                                                  -- telegram file_id или youtube/video ссылка
    summary      TEXT,                                                  -- текстовая выжимка после успешного теста
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uc_lessons_position_order UNIQUE (position_id, number),
    CONSTRAINT uc_lessons_title UNIQUE (title),
    CONSTRAINT fk_lessons_on_position FOREIGN KEY (position_id)
        REFERENCES positions (id) ON DELETE CASCADE
);

CREATE INDEX idx_lessons_position ON lessons(position_id);

-- =============================================
-- Knowledge checks, Questions and Answers
-- =============================================
CREATE TABLE checks
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    lesson_id  BIGINT                                 NOT NULL,
    version    BIGINT DEFAULT 1,

    CONSTRAINT fk_checks_on_lesson FOREIGN KEY (lesson_id)
        REFERENCES lessons (id) ON DELETE CASCADE
);

CREATE TABLE questions
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    check_id     BIGINT                                 NOT NULL,
    text         TEXT                                   NOT NULL,
    number       BIGINT                                 NOT NULL,
    explanation  TEXT,                                                  -- объяснение правильного ответа

    CONSTRAINT uc_questions_check_number UNIQUE (check_id, number),
    CONSTRAINT fk_questions_on_check FOREIGN KEY (check_id)
        REFERENCES checks (id) ON DELETE CASCADE
);

CREATE TABLE answer_options
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    question_id BIGINT                                 NOT NULL,
    text        TEXT                                   NOT NULL,
    is_correct  BOOLEAN DEFAULT FALSE                  NOT NULL,

    CONSTRAINT fk_answer_options_on_question FOREIGN KEY (question_id)
        REFERENCES questions (id) ON DELETE CASCADE
);

-- =============================================
-- Progress & Results
-- =============================================
CREATE TABLE user_progress
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id              BIGINT                                 NOT NULL,
    lesson_id            BIGINT                                 NOT NULL,
    video_watched        BOOLEAN DEFAULT FALSE,
    check_attempt_count  BIGINT DEFAULT 0,
    last_check_score     DECIMAL(5,2),
    is_completed         BOOLEAN DEFAULT FALSE,
    completed_at         TIMESTAMP WITH TIME ZONE,
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uc_user_progress_user_lesson UNIQUE (user_id, lesson_id),
    CONSTRAINT fk_user_progress_on_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_progress_on_lessons FOREIGN KEY (lesson_id)
        REFERENCES lessons (id) ON DELETE CASCADE
);

CREATE TABLE check_results
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT                                 NOT NULL,
    check_id        BIGINT                                 NOT NULL,
    score           DECIMAL(5,2)                           NOT NULL,
    passed          BOOLEAN                                NOT NULL,
    attempt_number  BIGINT                                 NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_check_results_on_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_check_results_on_check FOREIGN KEY (check_id)
        REFERENCES checks (id) ON DELETE CASCADE
);

-- =============================================
-- Auto-update for updated_at
-- =============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trg_users_update_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_lessons_update_updated_at
    BEFORE UPDATE ON lessons
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_user_progress_update_updated_at
    BEFORE UPDATE ON user_progress
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
