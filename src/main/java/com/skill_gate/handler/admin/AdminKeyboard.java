package com.skill_gate.handler.admin;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Employee;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.config.BotConstants.Position;
import com.skill_gate.handler.admin.employee.AdminEmployeeButton;
import com.skill_gate.handler.admin.lesson.AdminLessonButton;
import com.skill_gate.handler.admin.position.AdminPositionButton;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class AdminKeyboard {

    protected InlineKeyboardMarkup preparePaginationKeyboard(int currentPage, int totalPages, String prefix) {
        var markup = new InlineKeyboardMarkup();

        if (currentPage > 1 && currentPage < totalPages) {
            markup.addRow(AdminButton.previousPage(currentPage, prefix), AdminButton.nextPage(currentPage, prefix));
        } else if (currentPage > 1) {
            markup.addRow(AdminButton.previousPage(currentPage, prefix));
        } else if (currentPage < totalPages) {
            markup.addRow(AdminButton.nextPage(currentPage, prefix));
        }

        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup withMenuButton(InlineKeyboardMarkup markup) {
        return markup.addRow(AdminButton.menu());
    }

    public InlineKeyboardMarkup withMenuButton(InlineKeyboardMarkup markup, InlineKeyboardButton extra) {
        return markup.addRow(AdminButton.menu(), extra);
    }
}
