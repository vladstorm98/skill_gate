
package com.skill_gate.handler.admin.lesson;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.admin.position.AdminPositionKeyboard;
import com.skill_gate.handler.telegram.TelegramEditor;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLessonSender {

    private final TelegramSender sender;
    private final TelegramEditor editor;
    private final AdminLessonKeyboard lessonKeyboard;
    private final AdminPositionKeyboard positionKeyboard;

    public void sendLessons(DbUser admin, Long positionId, Page<DbLesson> lessonPage,
                            Integer currentPage, String positionTitle) {
        sender.send(admin, "📚 Lessons for %s:".formatted(positionTitle),
                lessonKeyboard.prepareLessonsKeyboard(
                        positionId, lessonPage.getContent(), currentPage, lessonPage.getTotalPages()
                )
        );
    }

    public void sendNewLessonPositions(DbUser admin) {
        sender.send(admin, "Select position:", positionKeyboard.prepareMultiSelectPositionKeyboard(admin));
    }

    public void refreshNewLessonPositions(DbUser admin, Integer messageId) {
        editor.edit(admin, messageId, positionKeyboard.prepareMultiSelectPositionKeyboard(admin));
    }

    public void sendPositionNotSelected(DbUser admin) {
        sender.send(admin, "Please select at least one position.");
    }

    public void sendNewLessonTitle(DbUser admin) {
        sender.send(admin, "Enter lesson name:");
    }

    public void sendNewLessonVideoUrl(DbUser admin) {
        sender.send(admin, "Video URL:");
    }

    public void sendNewLessonSummary(DbUser admin) {
        sender.send(admin, "Summary:");
    }

    public void sendNewLessonCreated(DbUser admin, String title) {
        sender.send(admin, "Lesson \"%s\" created successfully".formatted(title),
                lessonKeyboard.withMenuButton(new InlineKeyboardMarkup()));
    }

    public void sendViewLesson(DbUser admin, DbLesson lesson) {
        var text = """
            📚 %d: %s
            
            🎥 Video URL:
            %s
            
            📝 Summary:
            %s
            """
                .formatted(
                        lesson.getNumber(),
                        lesson.getTitle(),
                        lesson.getVideoUrl() != null ? lesson.getVideoUrl() : "—",
                        lesson.getSummary() != null ? lesson.getSummary() : "—"
                );

        sender.send(admin, text, lessonKeyboard.prepareViewLessonKeyboard(lesson.getId()));
    }

    public void sendEditLesson(DbUser admin, DbLesson lesson) {
        sender.send(admin, "What do you want to edit?\n\n№%d. %s".formatted(lesson.getNumber(), lesson.getTitle()),
                lessonKeyboard.prepareEditLessonKeyboard());
    }

    public void sendEditLessonTitle(DbUser admin) {
        sender.send(admin, "Enter new title:");
    }

    public void sendEditLessonNumber(DbUser admin) {
        sender.send(admin, "Enter new number:");
    }

    public void sendEditLessonVideoUrl(DbUser admin) {
        sender.send(admin, "Enter new video url:");
    }

    public void sendEditLessonSummary(DbUser admin) {
        sender.send(admin, "Enter new summary:");
    }

    public void sendFieldUpdated(DbUser admin, String fieldName) {
        sender.send(admin, "%s updated.".formatted(fieldName));
    }

    public void sendDeleteLessonConfirmation(DbUser admin, DbLesson lesson) {
        var text = """
            ⚠️ Delete lesson?
        
            Lesson #%d
            Title: %s
            """.formatted(lesson.getNumber(), lesson.getTitle());

        var markup = new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton("✅ Delete")
                                .callbackData("confirm_delete_lesson:" + lesson.getId()),
                        new InlineKeyboardButton("❌ Cancel")
                                .callbackData("reject_delete_lesson:" + lesson.getId())
                );

        sender.send(admin, text, markup);
    }

    public void sendDeleteLessonResult(DbUser admin, Integer messageId, String messageText, boolean isConfirm) {
        if (messageId == null) {
            return;
        }

        var editedText = isConfirm ?
                messageText + "\n\n✅ DELETED" : messageText + "\n\n❌ CANCELED";

        editor.edit(admin, messageId, editedText);
    }
}
