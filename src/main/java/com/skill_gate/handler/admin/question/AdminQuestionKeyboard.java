package com.skill_gate.handler.admin.question;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Check;
import com.skill_gate.config.BotConstants.Question;
import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbQuestion;
import com.skill_gate.handler.admin.AdminKeyboard;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.handler.admin.question.AdminQuestionButton.*;

@Component
public class AdminQuestionKeyboard extends AdminKeyboard {

    public InlineKeyboardMarkup prepareQuestionsKeyboard(Long checkId, List<DbQuestion> questions,
                                                         int page, int totalPages) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(newQuestion(checkId));
        questions.forEach(q -> markup.addRow(view(q)));
        addPagination(markup, page, totalPages, Question.PAGE + checkId + "_");
        markup.addRow(back(Check.VIEW + checkId));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareViewQuestionKeyboard(Long questionId, Long checkId,
                                                            List<DbAnswerOption> options) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(edit(questionId), delete(questionId));
        options.forEach(opt -> markup.addRow(toggleAnswer(opt), deleteAnswer(opt.getId())));
        markup.addRow(newAnswer(questionId));
        markup.addRow(back(Check.VIEW + checkId));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareEditQuestionKeyboard() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(editText(), editNumber());
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareDeleteConfirmation(Long questionId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(confirmDelete(questionId), rejectDelete(questionId));
        return markup;
    }

    private void addPagination(InlineKeyboardMarkup markup, int page, int totalPages, String prefix) {
        for (var row : preparePaginationKeyboard(page, totalPages, prefix).inlineKeyboard()) {
            markup.addRow(row);
        }
    }
}
