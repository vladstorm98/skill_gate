package com.skill_gate.handler.admin.check;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.skill_gate.cache.CheckDraftService;
import com.skill_gate.entity.DbUser;
import com.skill_gate.exception.CheckNotFoundException;
import com.skill_gate.handler.admin.question.AdminQuestionHandler;
import com.skill_gate.service.CheckService;
import com.skill_gate.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;

@Component
@RequiredArgsConstructor
public class AdminCheckHandler {

    private final CheckService checkService;
    private final LessonService lessonService;
    private final CheckDraftService draftService;
    private final AdminCheckSender checkSender;
    private final AdminQuestionHandler questionHandler;

    public void showChecks(DbUser admin, Long lessonId, Integer page) {
        if (page == null || page < 1) {
            page = 1;
        }
        draftService.saveLessonId(admin, lessonId);

        var lesson = lessonService.getLesson(lessonId);
        try {
            var checkPage = checkService.getPage(lesson, page, PAGE_SIZE);
            checkSender.sendChecks(admin, lessonId, lesson.getTitle(), checkPage, page);
        } catch (CheckNotFoundException e) {
            checkSender.sendEmpty(admin, lessonId); // ← с lessonId
        }
    }

    public void createCheck(DbUser admin, Long lessonId) {
        var lesson = lessonService.getLesson(lessonId);
        var check = checkService.createCheck(lesson);
        checkSender.sendCreated(admin, check);
        handleViewCheck(admin, check.getId());
    }

    public void handleViewCheck(DbUser admin, Long checkId) {
        var check = checkService.getCheck(checkId);
        draftService.saveCheckId(admin, checkId);
        checkSender.sendViewCheck(admin, check);
        questionHandler.showQuestions(admin, checkId, 1);
    }

    public void confirmDeleteCheck(DbUser admin, Long checkId) {
        checkSender.sendDeleteConfirmation(admin, checkService.getCheck(checkId));
    }

    public void deleteCheck(DbUser admin, CallbackQuery callback) {
        var data = callback.data();
        var confirmed = data.startsWith(com.skill_gate.config.BotConstants.Check.CONFIRM_DELETE);
        var checkId = Long.parseLong(data.split(":")[1]);
        Long lessonId = null;

        if (confirmed) {
            var check = checkService.getCheck(checkId);
            lessonId = check.getLesson().getId();
            checkService.deleteCheck(checkId);
        }

        checkSender.sendDeleteResult(admin, callback, confirmed);
        if (lessonId != null) {
            showChecks(admin, lessonId, 1);
        }
    }
}
