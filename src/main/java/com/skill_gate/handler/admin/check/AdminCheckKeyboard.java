package com.skill_gate.handler.admin.check;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Check;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.handler.admin.AdminKeyboard;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.handler.admin.check.AdminCheckButton.*;

@Component
public class AdminCheckKeyboard extends AdminKeyboard {

    public InlineKeyboardMarkup prepareChecksKeyboard(Long lessonId, List<DbCheck> checks,
                                                      int page, int totalPages) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(newCheck(lessonId));
        checks.forEach(check -> markup.addRow(view(check)));
        addPagination(markup, page, totalPages, Check.PAGE + lessonId + "_");
        markup.addRow(back(Lesson.VIEW + lessonId));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareViewCheckKeyboard(Long checkId, Long lessonId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(com.skill_gate.handler.admin.question.AdminQuestionButton.newQuestion(checkId));
        markup.addRow(delete(checkId));
        markup.addRow(back(Check.PAGE + lessonId + "_1"));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareDeleteConfirmation(Long checkId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(confirmDelete(checkId), rejectDelete(checkId));
        return markup;
    }

    public InlineKeyboardMarkup prepareEmptyChecksKeyboard(Long lessonId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(AdminCheckButton.newCheck(lessonId));
        return withMenuButton(markup, back(Lesson.VIEW + lessonId));
    }

    private void addPagination(InlineKeyboardMarkup markup, int page, int totalPages, String prefix) {
        for (var row : preparePaginationKeyboard(page, totalPages, prefix).inlineKeyboard()) {
            markup.addRow(row);
        }
    }
}
