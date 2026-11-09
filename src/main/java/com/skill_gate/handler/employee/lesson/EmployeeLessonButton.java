package com.skill_gate.handler.employee.lesson;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Knowledge;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.handler.employee.EmployeeButton;

public final class EmployeeLessonButton extends EmployeeButton {

    private EmployeeLessonButton() {}

    public static InlineKeyboardButton view(DbLesson lesson) {
        var title = lesson.getTitle() != null ? lesson.getTitle() : "Lesson";
        return button(lesson.getNumber() + ". " + title, Knowledge.VIEW_LESSON + lesson.getId());
    }

    public static InlineKeyboardButton backToList(int page) {
        return back(Knowledge.LESSONS_PAGE + page);
    }
}
