package com.skill_gate.handler.admin.position;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.state.StateManager;
import com.skill_gate.cache.PositionDraftService;
import com.skill_gate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;

@Component
@RequiredArgsConstructor
public class AdminPositionHandler {

    private final StateManager stateManager;
    private final PositionService positionService;
    private final AdminPositionSender positionSender;
    private final PositionDraftService positionDraftService;

    public void showPositions(DbUser admin, Integer currentPage) {
        var positionPage = positionService.getPage(currentPage, PAGE_SIZE);

        positionSender.sendPositions(admin, currentPage, positionPage);
    }

    public void showNewPosition(DbUser admin) {
        stateManager.change(admin, UserState.ADMIN_WAITING_NEW_POSITION);

        positionSender.sendNewPositionName(admin);
    }

    public void handleNewPosition(DbUser admin, Message message) {
        if (message.text() == null || message.text().isBlank()) {
            positionSender.sendNewPositionName(admin);
            return;
        }
        var name = message.text().trim();

        positionService.createPosition(name);
        positionSender.sendNewPositionCreated(admin, name);
    }

    public void showSelectPosition(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_SELECTING_LESSON_POSITION);

        positionSender.sendSelectPosition(admin);
    }

    public void handleViewPosition(DbUser admin, Long positionId) {
        var position = positionService.getPosition(positionId);

        positionSender.sendViewPosition(admin, position);
    }

    public void handleEditPosition(DbUser admin, Long positionId) {
        var position = positionService.getPosition(positionId);

        positionDraftService.save(admin, positionId);

        positionSender.sendEditPosition(admin, position);
    }

    public void showEditPositionName(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_POSITION_NAME);

        positionSender.sendEditPositionName(admin);
    }

    public void handleEditPositionName(DbUser admin, Message message) {
        var positionId = positionDraftService.get(admin);

        if (positionId == null) {
            stateManager.change(admin, UserState.ADMIN_MAIN_MENU);
            showPositions(admin, 1);
            return;
        }

        if (message.text() == null || message.text().isBlank()) {
            positionSender.sendEditPositionName(admin);
            return;
        }
        positionService.updateName(positionId, message.text().trim());
        positionDraftService.remove(admin);

        positionSender.sendFieldUpdated(admin, "Name");
        stateManager.change(admin, UserState.ADMIN_MAIN_MENU);
        handleViewPosition(admin, positionId);
    }

    public void showEditPositionDescription(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_POSITION_DESCRIPTION);

        positionSender.sendEditPositionDescription(admin);

    }

    public void handleEditPositionDescription(DbUser admin, Message message) {
        var positionId = positionDraftService.get(admin);

        if (positionId == null) {
            stateManager.change(admin, UserState.ADMIN_MAIN_MENU);
            showPositions(admin, 1);
            return;
        }

        if (message.text() == null || message.text().isBlank()) {
            positionSender.sendEditPositionDescription(admin);
            return;
        }
        positionService.updateDescription(positionId, message.text().trim());
        positionDraftService.remove(admin);

        positionSender.sendFieldUpdated(admin, "Description");
        stateManager.change(admin, UserState.ADMIN_MAIN_MENU);
        handleViewPosition(admin, positionId);
    }

    public void confirmDeletePosition(DbUser admin, Long positionId) {
        var position = positionService.getPosition(positionId);

        positionSender.sendDeletePositionConfirmation(admin, position);
    }

    public void deletePosition(DbUser admin, CallbackQuery callback) {
        var data = callback.data();
        var isConfirm = data.startsWith("confirm_delete_position:");
        var positionId = Long.parseLong(data.split(":")[1]);

        var messageId = getMessageId(callback);
        var messageText = getMessageText(callback);

        if (isConfirm) {
            positionService.delete(positionId);
        }

        positionSender.sendDeletePositionResult(admin, messageId, messageText, isConfirm);
    }

    private Integer getMessageId(CallbackQuery callback) {
        if (callback.maybeInaccessibleMessage() instanceof Message) {
            return callback.maybeInaccessibleMessage().messageId();
        }
        return null;
    }

    private String getMessageText(CallbackQuery callback) {
        if (callback.maybeInaccessibleMessage() instanceof Message message) {
            return message.text();
        }
        return null;
    }
}
