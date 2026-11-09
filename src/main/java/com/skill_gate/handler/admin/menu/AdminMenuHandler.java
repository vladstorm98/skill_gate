package com.skill_gate.handler.admin.menu;

import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.telegram.TelegramSender;
import com.skill_gate.handler.state.StateManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMenuHandler {

    private final TelegramSender sender;
    private final StateManager stateManager;
    private final AdminMenuKeyboard menuKeyboard;

    public void showAdminMenu(DbUser admin) {
        var text = "🛠 Admin Panel:";

        if (hasAccess(admin)) {
            stateManager.change(admin, UserState.ADMIN_MAIN_MENU);

            var keyboard = admin.getRole() == UserRole.SUPER_ADMIN ?
                    menuKeyboard.prepareSuperAdminMenuKeyboard() : menuKeyboard.prepareAdminMenuKeyboard();

            sender.send(admin, text, keyboard);
        } else {
            sender.send(admin, "⛔ Access denied");
        }
    }

    private boolean hasAccess(DbUser user) {
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN;
    }
}
