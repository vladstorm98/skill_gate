INSERT INTO positions (name, description)
VALUES
    ('Java Developer', 'Backend development with Java and Spring'),
    ('QA Engineer', 'Manual and automation testing'),
    ('Project Manager', 'Project coordination and delivery');

INSERT INTO users (
    telegram_id,
    full_name,
    phone,
    role,
    status,
    current_state,
    current_lesson_number,
    onboarding_progress,
    position_id
)
VALUES
    (
        398466752,
        'Admin User',
        '+380991111111',
        'ADMIN',
        'COMPLETED',
        'NONE',
        1,
        0,
        1
    ),
    (
        222222222,
        'Pending Employee',
        '+380992222222',
        'EMPLOYEE',
        'NEW',
        'REGISTRATION_WAITING_PHONE',
        0,
        0,
        2
    ),
    (
        333333333,
        'New Employee',
        '+380993333333',
        'EMPLOYEE',
        'ONBOARDING',
        'ONBOARDING_WAITING_VIDEO',
        1,
        0,
        3
    );

INSERT INTO lessons (position_id, number, title, video_url, summary)
VALUES
    (3, 1, 'Lesson 1', 'http//bla-bla-bla.net', 'some summary')
