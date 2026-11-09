package com.skill_gate.handler.admin.question;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Question;
import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbQuestion;
import com.skill_gate.handler.admin.AdminButton;

public final class AdminQuestionButton extends AdminButton {

    private AdminQuestionButton() {}

    public static InlineKeyboardButton view(DbQuestion question) {
        var text = question.getNumber() + ". " + shortText(question.getText());
        return button(text, Question.VIEW + question.getId());
    }

    public static InlineKeyboardButton newQuestion(Long checkId) {
        return button("➕ New Question", Question.NEW + checkId);
    }

    public static InlineKeyboardButton edit(Long questionId) {
        return button("✏️ Edit", Question.EDIT + questionId);
    }

    public static InlineKeyboardButton editText() {
        return button("✏️ Text", Question.EDIT_TEXT);
    }

    public static InlineKeyboardButton editNumber() {
        return button("📝 Number", Question.EDIT_NUMBER);
    }

    public static InlineKeyboardButton delete(Long questionId) {
        return button("🗑️ Delete", Question.DELETE + questionId);
    }

    public static InlineKeyboardButton confirmDelete(Long questionId) {
        return button("✅ Delete", Question.CONFIRM_DELETE + questionId);
    }

    public static InlineKeyboardButton rejectDelete(Long questionId) {
        return button("❌ Cancel", Question.REJECT_DELETE + questionId);
    }

    public static InlineKeyboardButton toggleAnswer(DbAnswerOption option) {
        var mark = option.getIsCorrect() ? "✅ " : "⬜ ";
        return button(mark + option.getText(), Question.TOGGLE_ANSWER + option.getId());
    }

    public static InlineKeyboardButton newAnswer(Long questionId) {
        return button("➕ Answer", Question.NEW_ANSWER + questionId);
    }

    public static InlineKeyboardButton deleteAnswer(Long optionId) {
        return button("🗑", Question.DELETE_ANSWER + optionId);
    }

    private static String shortText(String text) {
        if (text == null) return "—";
        return text.length() > 40 ? text.substring(0, 40) + "…" : text;
    }
}
