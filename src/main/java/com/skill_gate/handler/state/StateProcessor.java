package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;

public interface StateProcessor {
    void process(DbUser user, Message message);
    void process(DbUser user, CallbackQuery callback);
}
