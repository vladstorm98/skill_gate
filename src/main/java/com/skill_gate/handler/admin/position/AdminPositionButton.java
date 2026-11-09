package com.skill_gate.handler.admin.position;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Position;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.handler.admin.AdminButton;

public final class AdminPositionButton extends AdminButton {

    private AdminPositionButton() {}

    public static InlineKeyboardButton list() {
        return back(Position.PAGE + "1");
    }

    public static InlineKeyboardButton newPosition() {
        return button("➕ New Position", Position.NEW);
    }

    public static InlineKeyboardButton view(DbPosition position) {
        return button(position.getName(), Position.VIEW + position.getId());
    }

    public static InlineKeyboardButton edit(Long positionId) {
        return button("✏️ Edit", Position.EDIT + positionId);
    }

    public static InlineKeyboardButton delete(Long positionId) {
        return button("🗑️ Delete", Position.DELETE + positionId);
    }

    public static InlineKeyboardButton confirmDelete(Long positionId) {
        return button("✅ Delete", Position.CONFIRM_DELETE + positionId);
    }

    public static InlineKeyboardButton rejectDelete(Long positionId) {
        return button("❌ Cancel", Position.REJECT_DELETE + positionId);
    }
}
