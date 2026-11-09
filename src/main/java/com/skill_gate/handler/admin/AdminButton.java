package com.skill_gate.handler.admin;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.config.BotConstants.Position;
import com.skill_gate.entity.DbPosition;

public class AdminButton {

    protected AdminButton() {}

    public static InlineKeyboardButton menu() {
        return button("🔙 Menu", BotConstants.ADMIN_MENU);
    }

    public static InlineKeyboardButton previousPage(int currentPage, String prefix) {
        return button("⬅️ Previous", prefix + (currentPage - 1));
    }

    public static InlineKeyboardButton nextPage(int currentPage, String prefix) {
        return button("Next ➡️", prefix + (currentPage + 1));
    }

    public static InlineKeyboardButton back(String callbackData) {
        return button("🔙 Back", callbackData);
    }

    public static InlineKeyboardButton any(String callbackData) {
        return button("Any", callbackData);
    }

    // Position shared

    public static InlineKeyboardButton chooseAllPositions() {
        return button("All Positions", Position.CHOOSE + "0");
    }

    public static InlineKeyboardButton choosePosition(DbPosition position) {
        return button(position.getName(), Position.CHOOSE + position.getId());
    }

    public static InlineKeyboardButton editPositionName() {
        return button("✏️ Name", Position.EDIT_NAME);
    }

    public static InlineKeyboardButton editPositionDescription() {
        return button("📝 Description", Position.EDIT_DESCRIPTION);
    }

    // Lesson shared

    public static InlineKeyboardButton toggleNewLessonPosition(DbPosition position, boolean selected) {
        return button((selected ? "✅ " : "⬜ ") + position.getName(),
                Lesson.TOGGLE_NEW_POSITION + position.getId());
    }

    public static InlineKeyboardButton confirmNewLessonPositions() {
        return button("✅ Confirm", Lesson.CONFIRM_NEW_POSITIONS);
    }

    public static InlineKeyboardButton editLessonTitle() {
        return button("✏️ Title", Lesson.EDIT_TITLE);
    }

    public static InlineKeyboardButton editLessonNumber() {
        return button("📝 Number", Lesson.EDIT_NUMBER);
    }

    public static InlineKeyboardButton editLessonVideoUrl() {
        return button("🎥 Video URL", Lesson.EDIT_VIDEO_URL);
    }

    public static InlineKeyboardButton editLessonSummary() {
        return button("📝 Summary", Lesson.EDIT_SUMMARY);
    }

    protected static InlineKeyboardButton button(String text, String callbackData) {
        return new InlineKeyboardButton(text).callbackData(callbackData);
    }
}
