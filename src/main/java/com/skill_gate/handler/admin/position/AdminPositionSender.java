package com.skill_gate.handler.admin.position;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.telegram.TelegramEditor;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminPositionSender {

    private final TelegramSender sender;
    private final TelegramEditor editor;
    private final AdminPositionKeyboard keyboard;

    public void sendPositions (DbUser admin, Integer currentPage, Page<DbPosition> positionPage) {
        sender.send(admin, "📚 Positions:", keyboard.preparePositionsKeyboard(
                        positionPage.getContent(), currentPage, positionPage.getTotalPages()
        ));
    }

    public void sendNewPositionName(DbUser admin) {
        sender.send(admin, "Enter lesson name:");
    }

    public void sendNewPositionCreated(DbUser admin, String title) {
        sender.send(admin, "Position \"%s\" created successfully".formatted(title),
                keyboard.withMenuButton(new InlineKeyboardMarkup()));
    }
    public void sendSelectPosition(DbUser admin) {
        sender.send(admin, "Select position:",
                keyboard.prepareSelectPositionKeyboard(admin));
    }

    public void sendViewPosition(DbUser admin, DbPosition position) {
        var text = """
            💼 %s
            
            📝 Description: %s
            """
                .formatted(
                        position.getName(),
                        position.getDescription() != null ? position.getDescription() : "—"
                );

        sender.send(admin, text, keyboard.prepareViewPositionKeyboard(position.getId()));
    }

    public void sendEditPosition(DbUser admin, DbPosition position) {
        sender.send(admin, "What do you want to edit?\n\nPosition: %s".formatted(position.getName()),
                keyboard.prepareEditPositionKeyboard());
    }

    public void sendEditPositionName(DbUser admin) {
        sender.send(admin, "Enter new name:");
    }

    public void sendEditPositionDescription(DbUser admin) {
        sender.send(admin, "Enter new description:");
    }

    public void sendFieldUpdated(DbUser admin, String fieldName) {
        sender.send(admin, "%s updated.".formatted(fieldName));
    }

    public void sendDeletePositionConfirmation(DbUser admin, DbPosition position) {
        var text = "⚠️ Delete position %s?".formatted(position.getName());

        var markup = new InlineKeyboardMarkup()
                .addRow(
                        new InlineKeyboardButton("✅ Delete")
                                .callbackData("confirm_delete_position:" + position.getId()),
                        new InlineKeyboardButton("❌ Cancel")
                                .callbackData("reject_delete_position:" + position.getId())
                );

        sender.send(admin, text, markup);
    }

    public void sendDeletePositionResult(DbUser admin, Integer messageId, String messageText, boolean isConfirm) {
        if (messageId == null) return;

        var editedText = isConfirm ?
                messageText + "\n\n✅ DELETED" : messageText + "\n\n❌ CANCELED";

        editor.edit(admin, messageId, editedText);
    }
}
