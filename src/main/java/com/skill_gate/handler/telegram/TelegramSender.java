package com.skill_gate.handler.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.skill_gate.entity.DbUser;
import com.skill_gate.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramSender {

    private final BotService botService;

    public void send(DbUser user, String text) {
        botService.sendMessage(user.getTelegramId(), text);
    }

    public void send(DbUser user, String text, InlineKeyboardMarkup markup) {
        botService.sendMessageWithMarkup(user.getTelegramId(), text, markup);
    }

    public void send(DbUser user, String text, ReplyKeyboardMarkup reply) {
        botService.sendMessageWithReplyKeyboard(user.getTelegramId(), text, reply);
    }
}
