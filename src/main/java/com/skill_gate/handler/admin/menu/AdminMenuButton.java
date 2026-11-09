package com.skill_gate.handler.admin.menu;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants;
import com.skill_gate.handler.admin.AdminButton;

public class AdminMenuButton extends AdminButton {

    private AdminMenuButton() {}

    public static InlineKeyboardButton employees() {
        return button("👥 Employees", BotConstants.Employee.PAGE_DEFAULT);
    }

    public static InlineKeyboardButton positions() {
        return button("💼 Positions", BotConstants.Position.PAGE + "1");
    }

    public static InlineKeyboardButton lessons() {
        return AdminButton.button("📚 Lessons", BotConstants.Lesson.VIEW_LIST);
    }
}
