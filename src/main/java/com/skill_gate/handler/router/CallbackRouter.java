package com.skill_gate.handler.router;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.handler.state.OnboardingStateProcessor;
import com.skill_gate.handler.state.StateProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CallbackRouter {

    private static final String VIDEO_WATCHED_PREFIX = "video_watched:";
    private static final String CHECK_ANSWER_PREFIX = "check_answer:";

    private final StateProcessorFactory stateProcessorFactory;
    private final OnboardingStateProcessor onboardingProcessor;

    public void routeCallback(DbUser user, CallbackQuery callback) {
        if (isOnboardingCallback(user, callback)) {
            onboardingProcessor.process(user, callback);
            return;
        }

        var processor = stateProcessorFactory.getProcessor(user.getCurrentState());
        processor.process(user, callback);
    }

    private boolean isOnboardingCallback(DbUser user, CallbackQuery callback) {
        var data = callback.data();

        return user.getStatus() == UserStatus.ONBOARDING
                && data != null
                && (data.startsWith(VIDEO_WATCHED_PREFIX) || data.startsWith(CHECK_ANSWER_PREFIX));
    }
}
