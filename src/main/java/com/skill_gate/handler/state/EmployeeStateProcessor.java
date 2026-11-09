package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.config.BotConstants.Knowledge;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.employee.lesson.EmployeeLessonHandler;
import com.skill_gate.handler.employee.menu.EmployeeMenuHandler;
import com.skill_gate.handler.employee.progress.EmployeeProgressHandler;
import com.skill_gate.service.BotService;
import com.skill_gate.util.CallbackIds;
import org.springframework.stereotype.Component;

/**
 * Routes messages/callbacks for employees in the knowledge-base panel
 * ({@link com.skill_gate.entity.UserState#KNOWLEDGE_BASE_NAVIGATION}).
 */
@Component
public class EmployeeStateProcessor extends AbstractStateProcessor {

    private final EmployeeMenuHandler menuHandler;
    private final EmployeeLessonHandler lessonHandler;
    private final EmployeeProgressHandler progressHandler;

    public EmployeeStateProcessor(
            BotService botService,
            EmployeeMenuHandler menuHandler,
            EmployeeLessonHandler lessonHandler,
            EmployeeProgressHandler progressHandler
    ) {
        super(botService);
        this.menuHandler = menuHandler;
        this.lessonHandler = lessonHandler;
        this.progressHandler = progressHandler;
    }

    @Override
    public void process(DbUser user, Message message) {
        // Text in knowledge base is not used — guide to buttons
        sendUseButtonsMessage(user);
    }

    @Override
    public void process(DbUser user, CallbackQuery callback) {
        var data = callback.data();
        if (data == null || data.isBlank()) {
            sendUnknownCommand(user);
            return;
        }

        if (data.equals(Knowledge.MENU)) {
            menuHandler.showMainMenu(user);
        } else if (data.startsWith(Knowledge.LESSONS_PAGE)) {
            lessonHandler.showLessons(user, CallbackIds.pageAfterPrefix(data, Knowledge.LESSONS_PAGE));
        } else if (data.startsWith(Knowledge.VIEW_LESSON)) {
            lessonHandler.handleViewLesson(user, CallbackIds.colonId(data, 1));
        } else if (data.equals(Knowledge.PROGRESS)) {
            progressHandler.showProgress(user);
        } else {
            sendUnknownCommand(user);
        }
    }
}
