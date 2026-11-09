package com.skill_gate.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.skill_gate.entity.DbUser;
import com.skill_gate.exception.BotExceptionHandler;
import com.skill_gate.exception.BusinessException;
import com.skill_gate.handler.router.CallbackRouter;
import com.skill_gate.handler.router.MessageRouter;
import com.skill_gate.handler.telegram.TelegramSender;
import com.skill_gate.service.BotService;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotUpdateHandler {

    private static final int MAX_TELEGRAM_MESSAGE_LENGTH = 4000;

    private final BotExceptionHandler botExceptionHandler;
    private final UserService userService;
    private final MessageRouter messageRouter;
    private final CallbackRouter callbackRouter;
    private final BotService botService;
    private final TelegramSender sender;

    /**
     * Main method for processing incoming updates from Telegram.
     * Routes the update to the appropriate handler based on its type.
     *
     * @param update Update object received from Telegram
     */
    public void processUpdate(Update update) {
        if (update.message() != null) {
            handleMessage(update.message());
        } else if (update.callbackQuery() != null) {
            handleCallback(update.callbackQuery());
        }
    }

    private void handleMessage(Message message) {
        var user = getOrCreate(message);

        try {
            if (!isStartCommand(user, message)) {
                processMessage(user, message);
            }

        } catch (BusinessException e) {
            log.warn("Business rule violated while processing message '{}': {}", message.text(), e.getMessage());
            sender.send(user, "⚠️ " + truncate(e.getMessage()));

        } catch (Exception e) {
            botExceptionHandler.handle(message, e);
            sender.send(user, "⚠ An error occurred. We are already looking into it.");
        }
    }

    /**
     * Handles callback queries (inline button clicks).
     * Routes the action based on the callback data prefix.
     *
     * @param callback CallbackQuery from an inline button press
     */
    private void handleCallback(CallbackQuery callback) {
        var user = getOrCreate(callback);

        try {
            processCallback(user, callback);

        } catch (BusinessException e) {
                log.warn("Business rule violated while processing callback '{}': {}", callback.data(), e.getMessage());
            sender.send(user, "⚠️ " + truncate(e.getMessage()));

        } catch (Exception e) {
            botExceptionHandler.handle(callback, e);
            sender.send(user, "⚠ An error occurred. We are already looking into it.");

        } finally {
            botService.answerCallbackQuery(callback.id());
        }
    }

    private String truncate(String message) {
        if (message == null) {
            return "Unknown error";
        }
        return message.length() > MAX_TELEGRAM_MESSAGE_LENGTH
                ? message.substring(0, MAX_TELEGRAM_MESSAGE_LENGTH) + "..."
                : message;
    }

    private boolean isStartCommand(DbUser user, Message message) {
        var text = message.text();
        return text != null && "/start".equalsIgnoreCase(text.trim()) && messageRouter.routeStartCommand(user);
    }

    private void processMessage(DbUser user, Message message) {
        messageRouter.routeMessage(user, message);
    }

    private void processCallback(DbUser user, CallbackQuery callback) {
        callbackRouter.routeCallback(user, callback);
    }

    private DbUser getOrCreate(Message message) {
        return userService.getOrCreateUser(message.from().id());
    }

    private DbUser getOrCreate(CallbackQuery callback) {
        return userService.getOrCreateUser(callback.from().id());
    }
}
