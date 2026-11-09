package com.skill_gate.cache;

import com.skill_gate.entity.DbUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckDraftService {

    private static final String CHECK_PREFIX = "check-edit:";
    private static final String QUESTION_PREFIX = "question-edit:";
    private static final String LESSON_PREFIX = "check-lesson:";

    private final DraftStore draftStore;

    public void saveCheckId(DbUser admin, Long checkId) {
        draftStore.save(CHECK_PREFIX, admin, String.valueOf(checkId));
    }

    public Long getCheckId(DbUser admin) {
        String v = draftStore.get(CHECK_PREFIX, admin, String.class);
        return v == null ? null : Long.valueOf(v);
    }

    public void saveQuestionId(DbUser admin, Long questionId) {
        draftStore.save(QUESTION_PREFIX, admin, String.valueOf(questionId));
    }

    public Long getQuestionId(DbUser admin) {
        String v = draftStore.get(QUESTION_PREFIX, admin, String.class);
        return v == null ? null : Long.valueOf(v);
    }

    public void saveLessonId(DbUser admin, Long lessonId) {
        draftStore.save(LESSON_PREFIX, admin, String.valueOf(lessonId));
    }

    public Long getLessonId(DbUser admin) {
        String v = draftStore.get(LESSON_PREFIX, admin, String.class);
        return v == null ? null : Long.valueOf(v);
    }
}
