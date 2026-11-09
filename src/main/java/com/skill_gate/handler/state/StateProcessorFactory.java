package com.skill_gate.handler.state;

import com.skill_gate.entity.UserState;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StateProcessorFactory {

    private final Map<UserState, StateProcessor> processors;

    public StateProcessorFactory(
            RegistrationStateProcessor registration,
            OnboardingStateProcessor onboarding,
            AdminStateProcessor admin,
            DefaultStateProcessor defaultProcessor) {

        this.processors = Map.ofEntries(
                Map.entry(UserState.REGISTRATION_WAITING_FULL_NAME, registration),
                Map.entry(UserState.REGISTRATION_WAITING_PHONE, registration),
                Map.entry(UserState.REGISTRATION_WAITING_POSITION, registration),
                Map.entry(UserState.REGISTRATION_WAITING_APPROVAL, registration),

                Map.entry(UserState.ONBOARDING_WAITING_VIDEO, onboarding),
                Map.entry(UserState.ONBOARDING_WAITING_CHECK, onboarding),

                Map.entry(UserState.ADMIN_MAIN_MENU, admin),
                Map.entry(UserState.ADMIN_WAITING_NEW_ROLE, admin),
                Map.entry(UserState.ADMIN_WAITING_NEW_STATUS, admin),
                Map.entry(UserState.ADMIN_WAITING_NEW_POSITION, admin),
                Map.entry(UserState.ADMIN_SELECTING_USER_FOR_EDIT, admin),

                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_LESSON_POSITION, admin),
                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_LESSON_TITLE, admin),
                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_LESSON_VIDEO_URL, admin),
                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_LESSON_SUMMARY, admin),

                Map.entry(UserState.SUPER_ADMIN_SELECTING_LESSON_POSITION, admin),
                Map.entry(UserState.SUPER_ADMIN_SELECTING_LESSON, admin),
                Map.entry(UserState.SUPER_ADMIN_SELECTING_LESSON_FIELD, admin),

                Map.entry(UserState.SUPER_ADMIN_EDITING_LESSON_TITLE, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_LESSON_NUMBER, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_LESSON_VIDEO_URL, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_LESSON_SUMMARY, admin),

                Map.entry(UserState.SUPER_ADMIN_EDITING_POSITION_NAME, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_POSITION_DESCRIPTION, admin),

                Map.entry(UserState.SUPER_ADMIN_LESSON_DELETE_CONFIRM, admin),

                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_ANSWER_TEXT, admin),
                Map.entry(UserState.SUPER_ADMIN_WAITING_NEW_QUESTION_TEXT, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_QUESTION_TEXT, admin),
                Map.entry(UserState.SUPER_ADMIN_EDITING_QUESTION_NUMBER, admin),

                Map.entry(UserState.NONE, defaultProcessor),
                Map.entry(UserState.KNOWLEDGE_BASE_NAVIGATION, defaultProcessor)
        );
    }

    public StateProcessor getProcessor(UserState state) {
        return processors.getOrDefault(state, processors.get(UserState.NONE));
    }
}
