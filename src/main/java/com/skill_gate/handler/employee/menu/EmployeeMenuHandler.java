package com.skill_gate.handler.employee.menu;

import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserState;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.handler.state.StateManager;
import com.skill_gate.handler.telegram.TelegramSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Main menu for employees (teachers) who completed onboarding — knowledge base panel.
 */
@Component
@RequiredArgsConstructor
public class EmployeeMenuHandler {

    private final TelegramSender sender;
    private final StateManager stateManager;
    private final EmployeeMenuKeyboard menuKeyboard;

    public void showMainMenu(DbUser user) {
        if (!hasAccess(user)) {
            sender.send(user, "⛔ Knowledge base is available after onboarding is completed.");
            return;
        }

        stateManager.change(user, UserState.KNOWLEDGE_BASE_NAVIGATION);

        var positionName = user.getPosition() != null ? user.getPosition().getName() : "—";
        var text = """
                📚 Knowledge base
                
                Position: %s
                Choose a section:""".formatted(positionName);

        sender.send(user, text, menuKeyboard.prepareMainMenu());
    }

    private boolean hasAccess(DbUser user) {
        return user.getStatus() == UserStatus.COMPLETED
                && user.getRole() == UserRole.EMPLOYEE;
    }
}
