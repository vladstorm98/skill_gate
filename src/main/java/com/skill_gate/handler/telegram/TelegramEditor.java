package com.skill_gate.handler.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.entity.DbUser;
import com.skill_gate.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramEditor {

    private final BotService botService;

    public void edit(DbUser user, Integer messageId, String text) {
        botService.editMessage(user.getTelegramId(), messageId, text);
    }

    public void edit(DbUser user, Integer messageId, InlineKeyboardMarkup markup) {
        botService.editMessageReplyMarkup(user.getTelegramId(), messageId, markup);
    }
}
