package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.OnboardingHandler;
import com.skill_gate.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class OnboardingStateProcessor extends AbstractStateProcessor  {

    private final OnboardingHandler onboardingHandler;

    public OnboardingStateProcessor(BotService botService, OnboardingHandler onboardingHandler) {
        super(botService);
        this.onboardingHandler = onboardingHandler;
    }

    @Override
    public void process(DbUser user, Message message) {
        var state = user.getCurrentState();

        if (state == UserState.ONBOARDING_WAITING_VIDEO) {
            onboardingHandler.startOrContinueOnboarding(user);
        } else if (state == UserState.ONBOARDING_WAITING_CHECK) {
            sendUseButtonsMessage(user);
        } else {
            sendUnknownCommand(user);
        }
    }

    @Override
    public void process(DbUser user, CallbackQuery callback) {
        var data = callback.data();

        if (data.startsWith("video_watched:")) {
            onboardingHandler.handleVideoWatched(callback, user);

        } else if (data.startsWith("check_answer:")) {
            onboardingHandler.handleAnswerFromUser(callback, user);
        }
    }
}
