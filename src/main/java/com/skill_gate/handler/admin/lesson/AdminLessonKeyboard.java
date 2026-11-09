package com.skill_gate.handler.admin.lesson;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.handler.admin.AdminButton;
import com.skill_gate.handler.admin.AdminKeyboard;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.handler.admin.lesson.AdminLessonButton.*;
import static com.skill_gate.handler.admin.check.AdminCheckButton.openChecks;

@Component
public class AdminLessonKeyboard extends AdminKeyboard {

    public InlineKeyboardMarkup prepareLessonsKeyboard(Long positionId, List<DbLesson> lessons,
                                                       int page, int totalPages) {
        var markup = new InlineKeyboardMarkup();
        lessons.forEach(lesson -> markup.addRow(view(lesson)));
        addPagination(markup, page, totalPages, pagePrefix(positionId));
        return markup;
    }

    public InlineKeyboardMarkup prepareViewLessonKeyboard(Long lessonId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(edit(lessonId), delete(lessonId));
        markup.addRow(openChecks(lessonId));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareEditLessonKeyboard() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(AdminButton.editLessonTitle(), AdminButton.editLessonNumber());
        markup.addRow(AdminButton.editLessonVideoUrl(), AdminButton.editLessonSummary());
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareDeleteConfirmation(Long lessonId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(confirmDelete(lessonId), rejectDelete(lessonId));
        return markup;
    }

    private String pagePrefix(Long positionId) {
        return Lesson.PAGE + (positionId == null ? 0 : positionId) + "_";
    }

    private void addPagination(InlineKeyboardMarkup markup, int page, int totalPages, String prefix) {
        for (var row : preparePaginationKeyboard(page, totalPages, prefix).inlineKeyboard()) {
            markup.addRow(row);
        }
    }
}
