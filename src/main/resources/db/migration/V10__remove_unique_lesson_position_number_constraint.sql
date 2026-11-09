-- V11__remove_unique_lesson_position_number_constraint.sql

-- 1. Удаляем уникальное ограничение
ALTER TABLE lessons
    DROP CONSTRAINT IF EXISTS uc_lessons_position_order;

CREATE INDEX idx_lessons_position_number ON lessons (position_id, number);

-- 2. Добавляем комментарий
COMMENT ON TABLE lessons
    IS 'V11: Removed unique constraint on (position_id, number). Multiple lessons with same number are now allowed.';
