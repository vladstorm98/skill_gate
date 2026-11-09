package com.skill_gate.handler.admin.check;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Check;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.handler.admin.AdminButton;

public final class AdminCheckButton extends AdminButton {

    private AdminCheckButton() {}

    public static InlineKeyboardButton view(DbCheck check) {
        return button("v." + check.getVersion(), Check.VIEW + check.getId());
    }

    public static InlineKeyboardButton newCheck(Long lessonId) {
        return button("➕ New Check", Check.NEW + lessonId);
    }

    public static InlineKeyboardButton delete(Long checkId) {
        return button("🗑️ Delete", Check.DELETE + checkId);
    }

    public static InlineKeyboardButton confirmDelete(Long checkId) {
        return button("✅ Delete", Check.CONFIRM_DELETE + checkId);
    }

    public static InlineKeyboardButton rejectDelete(Long checkId) {
        return button("❌ Cancel", Check.REJECT_DELETE + checkId);
    }

    public static InlineKeyboardButton openChecks(Long lessonId) {
        return button("📝 Checks", Check.PAGE + lessonId + "_1");
    }
}
