package com.skill_gate.handler.employee.lesson;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbUser;
import com.skill_gate.exception.LessonNotFoundException;
import com.skill_gate.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;

/**
 * Read-only lesson browser for employees (teachers) after onboarding.
 */
@Component
@RequiredArgsConstructor
public class EmployeeLessonHandler {

    private final LessonService lessonService;
    private final EmployeeLessonSender lessonSender;

    public void showLessons(DbUser user, int page) {
        if (user.getPosition() == null) {
            lessonSender.sendNoPosition(user);
            return;
        }

        var safePage = Math.max(page, 1);
        Page<DbLesson> lessonPage;
        try {
            lessonPage = lessonService.getPageByPosition(user.getPosition(), safePage, PAGE_SIZE);
        } catch (LessonNotFoundException e) {
            lessonPage = new PageImpl<>(List.of(), PageRequest.of(0, PAGE_SIZE), 0);
        }
        lessonSender.sendLessonList(user, lessonPage, safePage);
    }

    public void handleViewLesson(DbUser user, Long lessonId) {
        if (user.getPosition() == null) {
            lessonSender.sendNoPosition(user);
            return;
        }

        var lesson = lessonService.getLesson(lessonId);
        if (!belongsToPosition(lesson, user) || !Boolean.TRUE.equals(lesson.getIsActive())) {
            lessonSender.sendLessonNotAllowed(user);
            return;
        }

        lessonSender.sendLessonView(user, lesson, 1);
    }

    private boolean belongsToPosition(DbLesson lesson, DbUser user) {
        if (lesson.getPositions() == null || user.getPosition() == null) {
            return false;
        }
        var positionId = user.getPosition().getId();
        return lesson.getPositions().stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(positionId));
    }
}
