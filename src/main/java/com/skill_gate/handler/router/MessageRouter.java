package com.skill_gate.handler.router;

import com.pengrad.telegrambot.model.Message;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.OnboardingHandler;
import com.skill_gate.handler.RegistrationHandler;
import com.skill_gate.handler.admin.menu.AdminMenuHandler;
import com.skill_gate.handler.employee.menu.EmployeeMenuHandler;
import com.skill_gate.handler.state.StateProcessorFactory;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageRouter {

    private final StateProcessorFactory stateProcessorFactory;
    private final RegistrationHandler registrationHandler;
    private final OnboardingHandler onboardingHandler;
    private final AdminMenuHandler adminMenuHandler;
    private final EmployeeMenuHandler employeeMenuHandler;
    private final TelegramSender sender;

    public boolean routeStartCommand(DbUser user) {
        if (isAdmin(user)) {
            adminMenuHandler.showAdminMenu(user);
            return true;
        }

        switch (user.getStatus()) {
            case NEW:
                if (user.getCurrentState() == UserState.REGISTRATION_WAITING_FULL_NAME ||
                        user.getCurrentState() == UserState.NONE) {
                    registrationHandler.startRegistration(user);
                }
                else {
                    sender.send(user, """
                            ✅ Your application has already been submitted and is under review.
                            Please wait for administrator approval""");
                }
                break;

            case ONBOARDING:
                onboardingHandler.startOrContinueOnboarding(user);
                break;

            case COMPLETED:
                employeeMenuHandler.showMainMenu(user);
                break;

            case BLOCKED:
                sender.send(user, "❌ Your account has been rejected.");
                break;

            default:
                sender.send(user, "Unknown account status.");
                break;
        }

        return true;
    }

    public void routeMessage(DbUser user, Message message) {
        var processor = stateProcessorFactory.getProcessor(user.getCurrentState());
        processor.process(user, message);
    }

    private boolean isAdmin(DbUser user) {
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN;
    }
}
