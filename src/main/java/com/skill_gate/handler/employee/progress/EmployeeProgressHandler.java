package com.skill_gate.handler.employee.progress;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.employee.EmployeeButton;
import com.skill_gate.handler.telegram.TelegramSender;
import com.skill_gate.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeProgressHandler {

    private final UserProgressService userProgressService;
    private final TelegramSender sender;

    public void showProgress(DbUser user) {
        var percent = userProgressService.calculateOverallProgress(user);
        var stored = user.getOnboardingProgress() != null
                ? user.getOnboardingProgress().doubleValue()
                : percent;

        var positionName = user.getPosition() != null ? user.getPosition().getName() : "—";
        var text = """
                📊 Your progress
                
                Position: %s
                Onboarding: %.0f%%
                Lessons completed: %.0f%%""".formatted(positionName, stored, percent);

        var markup = new InlineKeyboardMarkup();
        markup.addRow(EmployeeButton.menu());
        sender.send(user, text, markup);
    }
}
