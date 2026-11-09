package com.skill_gate.handler.state;

import com.skill_gate.entity.DbUser;
import com.skill_gate.service.BotService;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class AbstractStateProcessor implements StateProcessor {

    protected final BotService botService;

    protected void sendUnknownCommand(DbUser user) {
         botService.sendMessage(user.getTelegramId(), "Unknown command. Use /start or /admin");
     }

     protected void sendUseButtonsMessage(DbUser user) {
         botService.sendMessage(user.getTelegramId(), "Please use the buttons to continue the process");
     }
}
