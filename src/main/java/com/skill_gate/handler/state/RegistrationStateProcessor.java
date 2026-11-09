package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.RegistrationHandler;
import com.skill_gate.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStateProcessor extends AbstractStateProcessor {

    private final RegistrationHandler registrationHandler;

    public RegistrationStateProcessor(BotService botService,  RegistrationHandler registrationHandler) {
        super(botService);
        this.registrationHandler = registrationHandler;
    }

    @Override
    public void process(DbUser user, Message message) {
        var state = user.getCurrentState();

        if (state == UserState.REGISTRATION_WAITING_FULL_NAME) {
            registrationHandler.handleFullName(user, message);
        } else if (state == UserState.REGISTRATION_WAITING_PHONE) {
            registrationHandler.handlePhone(user, message);
        } else {
            sendUnknownCommand(user);
        }
    }

    @Override
    public void process(DbUser user, CallbackQuery callback) {
        var data = callback.data();
        if (data == null || data.isBlank()) {
            sendUnknownCommand(user);
            return;
        }

        if (data.startsWith("choose_position:")) {
            registrationHandler.handlePositionChoice(user, data);
        }
    }
}
