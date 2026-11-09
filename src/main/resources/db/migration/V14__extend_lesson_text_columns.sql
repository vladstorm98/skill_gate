-- V14__extend_lesson_text_columns.sql

ALTER TABLE lessons
    ALTER COLUMN title TYPE VARCHAR(500),
    ALTER COLUMN video_url TYPE VARCHAR(1000),
    ALTER COLUMN summary TYPE TEXT;

COMMENT ON COLUMN lessons.summary IS 'V14: summary can be long text';
COMMENT ON COLUMN lessons.video_url IS 'V14: video URL up to 1000 chars';
COMMENT ON COLUMN lessons.title IS 'V14: title up to 500 chars';
