package com.skill_gate.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.service.BotService;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Handler responsible for approving or rejecting new employee registration requests
 * by administrators.
 * <p>
 * This class processes admin decisions on pending registrations and notifies
 * both the administrator and the applicant accordingly.
 */
@Component
@RequiredArgsConstructor
public class ApprovalHandler {

    private final BotService botService;
    private final UserService userService;

    public void approve(Long userTelegramId, CallbackQuery callback) {
        approveUser(userTelegramId);
        editMessage(callback);

        var userText = """
                        🎉 Congratulations! Your application has been approved.
                    
                        The onboarding process is starting now. Use /start to continue.""";

        sendMessage(userTelegramId, userText);
    }

    private void editMessage(CallbackQuery callback) {
        if (callback.maybeInaccessibleMessage() instanceof Message msg) {
            var chatId = msg.chat().id();
            var messageId = callback.maybeInaccessibleMessage().messageId();
            var updatedText = callback.data().startsWith("approve:") ?
                    msg.text() + "\n\n✅ APPROVED" : msg.text() + "\n\n❌ REJECTED";

            botService.editMessage(chatId, messageId, updatedText);
        }
    }

    public void reject(Long userTelegramId, CallbackQuery callback) {
        rejectUser(userTelegramId);
        editMessage(callback);

        var userText = "Unfortunately, your application was rejected.";
        sendMessage(userTelegramId, userText);
    }

    /**
     * Approves the user in the database.
     *
     * @param telegramId Telegram ID of the user to approve
     */
    private void approveUser(Long telegramId) {
        userService.approveUser(telegramId);
    }

    /**
     * Rejects the user in the database.
     *
     * @param telegramId Telegram ID of the user to reject
     */
    private void rejectUser(Long telegramId) {
        userService.rejectUser(telegramId);
    }

    /**
     * Sends a message to a user via the bot service.
     *
     * @param chatId Telegram chat ID
     * @param text   message text
     */
    private void sendMessage(Long chatId, String text) {
        botService.sendMessage(chatId, text);
    }

    /**
     * Retrieves the full name of a user.
     *
     * @param userTelegramId Telegram ID of the user
     * @return user's full name
     */
    private String getFullName(Long userTelegramId) {
        return getUser(userTelegramId).getFullName();
    }

    /**
     * Fetches a user entity by ID.
     *
     * @param userTelegramId Telegram ID of the user
     * @return {@link DbUser} entity
     */
    private DbUser getUser(Long userTelegramId) {
        return userService.getUser(userTelegramId);
    }
}
