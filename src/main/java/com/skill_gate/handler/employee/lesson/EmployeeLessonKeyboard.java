package com.skill_gate.handler.employee.lesson;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Knowledge;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.handler.employee.EmployeeButton;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLessonKeyboard {

    public InlineKeyboardMarkup prepareLessonList(Page<DbLesson> page, int currentPage) {
        var markup = new InlineKeyboardMarkup();

        for (var lesson : page.getContent()) {
            if (Boolean.TRUE.equals(lesson.getIsActive())) {
                markup.addRow(EmployeeLessonButton.view(lesson));
            }
        }

        if (page.getTotalPages() > 1) {
            if (currentPage > 1) {
                markup.addRow(EmployeeButton.previousPage(currentPage, Knowledge.LESSONS_PAGE));
            }
            if (currentPage < page.getTotalPages()) {
                markup.addRow(EmployeeButton.nextPage(currentPage, Knowledge.LESSONS_PAGE));
            }
        }

        markup.addRow(EmployeeButton.menu());
        return markup;
    }

    public InlineKeyboardMarkup prepareLessonView(int returnPage) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(EmployeeLessonButton.backToList(returnPage));
        markup.addRow(EmployeeButton.menu());
        return markup;
    }
}
