package com.skill_gate.handler.router;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.state.StateProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackRouter {

    private final StateProcessorFactory stateProcessorFactory;

    public void routeCallback(DbUser user, CallbackQuery callback) {
            var processor = stateProcessorFactory.getProcessor(user.getCurrentState());
            processor.process(user, callback);
    }
}
