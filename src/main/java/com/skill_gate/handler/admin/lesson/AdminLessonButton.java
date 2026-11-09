package com.skill_gate.handler.admin.lesson;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.handler.admin.AdminButton;

public final class AdminLessonButton extends AdminButton {

    private AdminLessonButton() {}

    public static InlineKeyboardButton list() {
        return back(Lesson.VIEW_LIST);
    }

    public static InlineKeyboardButton newLesson() {
        return button("➕ New Lesson", Lesson.NEW);
    }

    public static InlineKeyboardButton view(DbLesson lesson) {
        return button(lesson.getNumber() + ". " + lesson.getTitle(), Lesson.VIEW + lesson.getId());
    }

    public static InlineKeyboardButton edit(Long lessonId) {
        return button("✏️ Edit", Lesson.EDIT + lessonId);
    }

    public static InlineKeyboardButton delete(Long lessonId) {
        return button("🗑️ Delete", Lesson.DELETE + lessonId);
    }

    public static InlineKeyboardButton confirmDelete(Long lessonId) {
        return button("✅ Delete", Lesson.CONFIRM_DELETE + lessonId);
    }

    public static InlineKeyboardButton rejectDelete(Long lessonId) {
        return button("❌ Cancel", Lesson.REJECT_DELETE + lessonId);
    }
}
