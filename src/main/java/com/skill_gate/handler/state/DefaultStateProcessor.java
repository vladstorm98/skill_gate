package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class DefaultStateProcessor extends AbstractStateProcessor {

    public DefaultStateProcessor(BotService botService) {
        super(botService);
    }

    @Override
    public void process(DbUser user, Message message) {
        sendUnknownCommand(user);
    }

    @Override
    public void process(DbUser user, CallbackQuery callback) {}
}
