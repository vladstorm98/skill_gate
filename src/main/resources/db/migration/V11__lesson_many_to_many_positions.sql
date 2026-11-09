-- V11__lesson_many_to_many_positions.sql

-- 1. Создаём связующую таблицу
CREATE TABLE IF NOT EXISTS lesson_positions (
                                                lesson_id   BIGINT NOT NULL,
                                                position_id BIGINT NOT NULL,
                                                PRIMARY KEY (lesson_id, position_id),
                                                CONSTRAINT fk_lesson_positions_lesson
                                                    FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
                                                CONSTRAINT fk_lesson_positions_position
                                                    FOREIGN KEY (position_id) REFERENCES positions(id) ON DELETE CASCADE
);

-- 2. Переносим существующие связи (если были)
INSERT INTO lesson_positions (lesson_id, position_id)
SELECT id, position_id
FROM lessons
WHERE position_id IS NOT NULL
ON CONFLICT DO NOTHING;

-- 3. Удаляем старый стол position_id (после переноса)
ALTER TABLE lessons DROP COLUMN IF EXISTS position_id;

-- 4. Индексы
CREATE INDEX IF NOT EXISTS idx_lesson_positions_lesson ON lesson_positions(lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_positions_position ON lesson_positions(position_id);

COMMENT ON TABLE lesson_positions IS 'V12: Many-to-Many between lessons and positions';
