package com.skill_gate.exception;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.skill_gate.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotExceptionHandler {

    private final BotService botService;

    @Value("${telegram.bot.developer-chat-id}")
    private Long developerChatId;

    /**
     * Handles exceptions that occur during Telegram bot update processing.
     * Logs the error and sends a detailed notification to the developer.
     *
     * @param update the Telegram update that caused the exception
     * @param e      the exception that was thrown
     */
    public void handle(Update update, Exception e) {
        log.error("Error processing update: {}", update, e);
        notifyDeveloper("Error processing update", update, e);
    }

    /**
     * Handles exceptions that occur during regular message processing.
     * Logs the error with message context and notifies the developer.
     *
     * @param message the message that caused the exception
     * @param e       the exception that was thrown
     */
    public void handle(Message message, Exception e) {
        var chatId = message != null ? message.chat().id() : null;
        var text = message != null && message.text() != null ? message.text() : "N/A";

        log.error("Error processing message from chat {}: {}", chatId, text, e);
        notifyDeveloper("Error processing message", message, e);
    }

    /**
     * Handles exceptions that occur during callback query processing.
     * Logs the error with callback data and notifies the developer.
     *
     * @param callback the callback query that caused the exception
     * @param e        the exception that was thrown
     */
    public void handle(CallbackQuery callback, Exception e) {
        var data = callback != null ? callback.data() : "N/A";
        var chatId = callback != null && callback.maybeInaccessibleMessage() != null ?
                callback.maybeInaccessibleMessage().chat().id() : null;

        log.error("Error processing callback data from chat '{}': {}", chatId, data, e);
        notifyDeveloper("Error processing callback", callback, e);
    }

    /**
     * Sends a detailed error notification to the developer via Telegram.
     *
     * @param title   short description of the error type
     * @param context the context object (Update, Message or CallbackQuery)
     * @param e       the exception that occurred
     */
    private void notifyDeveloper(String title, Object context, Exception e) {
        if (developerChatId == null || developerChatId <= 0) {
            return; // Developer chat ID not configured
        }

        try {
            var message = "🚨 *Bot Error Alert*\n\n" +
                    "📌 " + title + "\n\n" +
                    "🆔 Context: `" + truncate(String.valueOf(context), 1000) + "`\n\n" +
                    "❌ Exception: `" + e.getClass().getSimpleName() + "`\n" +
                    "📝 Message: " + (e.getMessage() != null ? e.getMessage() : "No message provided");

            botService.sendMessage(developerChatId, message);
        } catch (Exception ex) {
            log.warn("Failed to send error notification to developer", ex);
        }
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
