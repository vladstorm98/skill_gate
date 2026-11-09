-- V15__increase_lesson_text_limits.sql

ALTER TABLE lessons
    ALTER COLUMN summary TYPE VARCHAR(10000);

COMMENT ON COLUMN lessons.summary IS 'Up to 10000 characters';
