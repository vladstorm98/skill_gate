package com.skill_gate.handler.admin.position;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.cache.LessonDraftService;
import com.skill_gate.config.BotConstants.Position;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.admin.AdminButton;
import com.skill_gate.handler.admin.AdminKeyboard;
import com.skill_gate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.handler.admin.position.AdminPositionButton.*;

@Component
@RequiredArgsConstructor
public class AdminPositionKeyboard extends AdminKeyboard {

    private final PositionService positionService;
    private final LessonDraftService lessonDraftService;

    public InlineKeyboardMarkup preparePositionsKeyboard(List<DbPosition> positions, int page, int totalPages) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(newPosition());
        positions.forEach(position -> markup.addRow(view(position)));
        addPagination(markup, page, totalPages, Position.PAGE);
        return markup;
    }

    public InlineKeyboardMarkup prepareViewPositionKeyboard(Long positionId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(edit(positionId), delete(positionId));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareEditPositionKeyboard() {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(AdminButton.editPositionName(), AdminButton.editPositionDescription());
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareSelectPositionKeyboard(DbUser admin) {
        var markup = new InlineKeyboardMarkup();
        if (admin.getCurrentState() != UserState.SUPER_ADMIN_WAITING_NEW_LESSON_POSITION) {
            markup.addRow(AdminButton.chooseAllPositions());
        }
        positionService.getAllPositions()
                .forEach(position -> markup.addRow(AdminButton.choosePosition(position)));
        return withMenuButton(markup);
    }

    public InlineKeyboardMarkup prepareMultiSelectPositionKeyboard(DbUser user) {
        var selectedIds = lessonDraftService.get(user).positionIds();
        var rows = positionService.getAllPositions().stream()
                .map(p -> new InlineKeyboardButton[]{
                        AdminButton.toggleNewLessonPosition(p, selectedIds.contains(p.getId()))
                })
                .toArray(InlineKeyboardButton[][]::new);

        return withMenuButton(new InlineKeyboardMarkup(rows), AdminButton.confirmNewLessonPositions());
    }

    public InlineKeyboardMarkup prepareDeleteConfirmation(Long positionId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(confirmDelete(positionId), rejectDelete(positionId));
        return markup;
    }

    private void addPagination(InlineKeyboardMarkup markup, int page, int totalPages, String prefix) {
        for (var row : preparePaginationKeyboard(page, totalPages, prefix).inlineKeyboard()) {
            markup.addRow(row);
        }
    }
}
