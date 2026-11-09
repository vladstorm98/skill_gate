package com.skill_gate.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Main service responsible for handling all interactions with the Telegram Bot.
 * Uses Long Polling to receive updates from Telegram.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramBot bot;

    /**
     * Sends a simple text message to the user.
     *
     * @param chatId ID of the chat
     * @param text   message text
     */
    public void sendMessage(Long chatId, String text) {
        var request = createSendMessage(chatId, text);
        execute(chatId, request);
    }

    /**
     * Sends a text message with inline keyboard markup.
     *
     * @param chatId ID of the chat
     * @param text   message text
     * @param markup inline keyboard markup
     */
    public void sendMessageWithMarkup(Long chatId, String text, InlineKeyboardMarkup markup) {
        var request = createSendMessage(chatId, text)
                .replyMarkup(markup);
        execute(chatId, request);
    }

    /**
     * Sends a text message with a Reply Keyboard markup.
     *
     * <p>Used for special keyboards such as contact request, location request,
     * or custom reply keyboards.</p>
     *
     * @param chatId       the ID of the chat to send the message to
     * @param text         the message text
     * @param replyKeyboard the reply keyboard markup to attach
     */
    public void sendMessageWithReplyKeyboard(long chatId, String text, ReplyKeyboardMarkup replyKeyboard) {
        var request = createSendMessage(chatId, text)
                .replyMarkup(replyKeyboard);

        execute(chatId, request);
    }

    public void editMessage(Long chatId, @NonNull Integer messageId, String text) {
        var edit = new EditMessageText(chatId, messageId, text);

        execute(edit);
    }

    public void editMessageReplyMarkup(Long chatId, Integer messageId, InlineKeyboardMarkup markup) {
        var edit = new EditMessageReplyMarkup(chatId, messageId)
                .replyMarkup(markup);
        execute(edit);
    }

    public void answerCallbackQuery(String callbackQueryId) {
        var answer = new AnswerCallbackQuery(callbackQueryId);
        execute(answer);
    }

    /**
     * Executes a SendMessage request and logs errors if the message was not sent successfully.
     *
     * @param request the SendMessage request to execute
     */
    private void execute(Long chatId, SendMessage request) {
        var response = bot.execute(request);

        if (!response.isOk()) {
            log.error("Failed to send message to chat {}. Error: {}",
                    chatId, response.description());
        }
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(BaseRequest<T, R> request) {
        try {
            bot.execute(request);
        } catch (Exception e) {
            log.error("Failed to execute Telegram request: {}", request.getClass().getSimpleName(), e);
        }

        return  null;
    }

    /**
     * Creates a basic SendMessage request.
     *
     * @param chatId the ID of the chat
     * @param text   the message text
     * @return configured SendMessage object
     */
    private SendMessage createSendMessage(Long chatId, String text) {
        return new SendMessage(chatId, text);
    }
}
