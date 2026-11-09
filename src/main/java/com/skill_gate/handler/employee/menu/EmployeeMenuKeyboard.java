package com.skill_gate.handler.employee.menu;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import static com.skill_gate.handler.employee.menu.EmployeeMenuButton.lessons;
import static com.skill_gate.handler.employee.menu.EmployeeMenuButton.progress;

@Component
public class EmployeeMenuKeyboard {

    public InlineKeyboardMarkup prepareMainMenu() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(lessons());
        markup.addRow(progress());
        return markup;
    }
}
