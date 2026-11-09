package com.skill_gate.handler.employee.menu;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants;
import com.skill_gate.handler.employee.EmployeeButton;

public final class EmployeeMenuButton extends EmployeeButton {

    private EmployeeMenuButton() {}

    public static InlineKeyboardButton lessons() {
        return button("📚 My lessons", BotConstants.Knowledge.LESSONS_PAGE + "1");
    }

    public static InlineKeyboardButton progress() {
        return button("📊 My progress", BotConstants.Knowledge.PROGRESS);
    }
}
