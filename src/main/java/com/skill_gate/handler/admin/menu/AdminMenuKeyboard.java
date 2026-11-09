package com.skill_gate.handler.admin.menu;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import static com.skill_gate.handler.admin.menu.AdminMenuButton.*;
import static com.skill_gate.handler.admin.lesson.AdminLessonButton.newLesson;

@Component
public class AdminMenuKeyboard {

    public InlineKeyboardMarkup prepareAdminMenuKeyboard() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(employees(), positions());
        markup.addRow(lessons());
        return markup;
    }

    public InlineKeyboardMarkup prepareSuperAdminMenuKeyboard() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(employees(), positions());
        markup.addRow(lessons(), newLesson());
        return markup;
    }
}
