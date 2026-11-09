package com.skill_gate.handler.employee;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants;

/**
 * Shared inline button helpers for the employee (teacher) knowledge-base panel.
 */
public class EmployeeButton {

    protected EmployeeButton() {}

    public static InlineKeyboardButton button(String text, String callbackData) {
        return new InlineKeyboardButton(text).callbackData(callbackData);
    }

    public static InlineKeyboardButton menu() {
        return button("🔙 Menu", BotConstants.Knowledge.MENU);
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
}
