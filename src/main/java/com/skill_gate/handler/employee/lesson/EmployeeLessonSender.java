package com.skill_gate.handler.employee.lesson;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeLessonSender {

    private final TelegramSender sender;
    private final EmployeeLessonKeyboard keyboard;

    public void sendLessonList(DbUser user, Page<DbLesson> page, int currentPage) {
        if (page.isEmpty()) {
            sender.send(user, "📚 No lessons found for your position yet.", keyboard.prepareLessonList(page, currentPage));
            return;
        }

        var text = "📚 Lessons for your position (page %d/%d):".formatted(
                currentPage, Math.max(page.getTotalPages(), 1));
        sender.send(user, text, keyboard.prepareLessonList(page, currentPage));
    }

    public void sendLessonView(DbUser user, DbLesson lesson, int returnPage) {
        var title = lesson.getTitle() != null ? lesson.getTitle() : "—";
        var summary = lesson.getSummary() != null && !lesson.getSummary().isBlank()
                ? lesson.getSummary()
                : "No summary";
        var video = lesson.getVideoUrl() != null && !lesson.getVideoUrl().isBlank()
                ? lesson.getVideoUrl()
                : "—";

        var text = """
                📖 Lesson %s: %s
                
                📝 Summary:
                %s
                
                🎥 Video:
                %s""".formatted(lesson.getNumber(), title, summary, video);

        sender.send(user, text, keyboard.prepareLessonView(returnPage));
    }

    public void sendNoPosition(DbUser user) {
        sender.send(user, "⚠️ Position is not assigned. Contact an administrator.");
    }

    public void sendLessonNotAllowed(DbUser user) {
        sender.send(user, "⛔ This lesson is not available for your position.");
    }
}
