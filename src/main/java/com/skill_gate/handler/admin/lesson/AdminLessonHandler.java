package com.skill_gate.handler.admin.lesson;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.dto.LessonDraftDto;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.handler.state.StateManager;
import com.skill_gate.cache.LessonDraftService;
import com.skill_gate.service.LessonService;
import com.skill_gate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;

@Component
@RequiredArgsConstructor
public class AdminLessonHandler {

    private final StateManager stateManager;
    private final AdminLessonSender lessonSender;
    private final LessonService lessonService;
    private final PositionService positionService;
    private final LessonDraftService lessonDraftService;

    public void showLessons(DbUser admin, Long positionId, Integer currentPage) {
        var lessonPage = positionId == 0L ?
                lessonService.getPage(currentPage, PAGE_SIZE) :
                lessonService.getPageByPosition(
                        positionService.getPosition(positionId), currentPage, PAGE_SIZE
                );

        var title = positionId == 0L ? "all positions" : positionService.getPosition(positionId).getName();
        lessonSender.sendLessons(admin, positionId, lessonPage, currentPage, title);
    }

    public void showNewLessonPositions(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_LESSON_POSITION);

        lessonDraftService.save(admin, LessonDraftDto.empty());

        lessonSender.sendNewLessonPositions(admin);
    }

    public void refreshNewLessonPositions(DbUser admin, CallbackQuery callback) {
        var positionId = Long.parseLong(callback.data().split(":")[1]);

        lessonDraftService.togglePosition(admin, positionId);

        lessonSender.refreshNewLessonPositions(admin, getMessageId(callback));
    }

    public void handleNewLessonPositions(DbUser admin) {
        var draft = lessonDraftService.get(admin);

        if (draft.positionIds().isEmpty()) {
            lessonSender.sendPositionNotSelected(admin);
            return;
        }

        var positions = draft.positionIds().stream()
                .map(positionService::getPosition)
                .collect(Collectors.toSet());

        var lesson = lessonService.createLesson(positions);

        lessonDraftService.save(admin, new LessonDraftDto(lesson.getId(), draft.positionIds()));

        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_LESSON_TITLE);
        lessonSender.sendNewLessonTitle(admin);
    }

    public void handleNewLessonTitle(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendNewLessonTitle(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateTitle(draft.id(), text);

        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_LESSON_VIDEO_URL);
        lessonSender.sendNewLessonVideoUrl(admin);
    }

    public void handleNewLessonVideoUrl(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendNewLessonVideoUrl(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateVideoUrl(draft.id(), text);

        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_LESSON_SUMMARY);
        lessonSender.sendNewLessonSummary(admin);
    }

    public void handleNewLessonSummary(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendNewLessonSummary(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateSummary(draft.id(), text);

        var lesson = lessonService.getLesson(draft.id());
        lessonDraftService.remove(admin);

        lessonSender.sendNewLessonCreated(admin, lesson.getTitle());
    }

    public void handleViewLesson(DbUser admin, Long lessonId) {
        var lesson = lessonService.getLesson(lessonId);

        lessonSender.sendViewLesson(admin, lesson);
    }

    public void handleEditLesson(DbUser admin, Long lessonId) {
        var lesson = lessonService.getLesson(lessonId);

        lessonDraftService.save(admin, new LessonDraftDto(lessonId, Set.of()));

        lessonSender.sendEditLesson(admin, lesson);
    }

    public void showEditLessonTitle(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_LESSON_TITLE);

        lessonSender.sendEditLessonTitle(admin);
    }

    public void handleEditLessonTitle(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendEditLessonTitle(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateTitle(draft.id(), text);

        lessonSender.sendFieldUpdated(admin, "Title");
        handleEditLesson(admin, draft.id());
    }

    public void showEditLessonNumber(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_LESSON_NUMBER);

        lessonSender.sendEditLessonNumber(admin);
    }

    public void handleEditLessonNumber(DbUser admin, Message message) {
        if (message.text() == null || message.text().isBlank()) {
            lessonSender.sendEditLessonNumber(admin);
            return;
        }
        long number;
        try {
            number = Long.parseLong(message.text().trim());
        } catch (NumberFormatException e) {
            lessonSender.sendEditLessonNumber(admin);
            return;
        }
        var draft = lessonDraftService.get(admin);

        lessonService.updateNumber(draft.id(), number);

        lessonSender.sendFieldUpdated(admin, "Lesson number");
        handleEditLesson(admin, draft.id());
    }

    public void showEditLessonVideoUrl(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_LESSON_VIDEO_URL);

        lessonSender.sendEditLessonVideoUrl(admin);
    }

    public void handleEditLessonVideoUrl(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendEditLessonVideoUrl(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateVideoUrl(draft.id(), text);

        lessonSender.sendFieldUpdated(admin, "Video URL");
        handleEditLesson(admin, draft.id());
    }

    public void showEditLessonSummary(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_LESSON_SUMMARY);

        lessonSender.sendEditLessonSummary(admin);
    }

    public void handleEditLessonSummary(DbUser admin, Message message) {
        var text = requireText(message, () -> lessonSender.sendEditLessonSummary(admin));
        if (text == null) return;

        var draft = lessonDraftService.get(admin);
        lessonService.updateSummary(draft.id(), text);

        lessonSender.sendFieldUpdated(admin, "Summary");
        handleEditLesson(admin, draft.id());
    }

    public void confirmDeleteLesson(DbUser admin, Long lessonId) {
        var lesson = lessonService.getLesson(lessonId);

        lessonSender.sendDeleteLessonConfirmation(admin, lesson);
    }

    public void deleteLesson(DbUser admin, CallbackQuery callback) {
        var data = callback.data();
        var isConfirm = data.startsWith("confirm_delete_lesson:");
        var lessonId = Long.parseLong(data.split(":")[1]);

        var messageId = getMessageId(callback);
        var messageText = getMessageText(callback);

        if (isConfirm) {
            lessonService.deleteLesson(lessonId);
        }

        lessonSender.sendDeleteLessonResult(admin, messageId, messageText, isConfirm);
    }

    private Integer getMessageId(CallbackQuery callback) {
        if (callback.maybeInaccessibleMessage() instanceof Message) {
            return callback.maybeInaccessibleMessage().messageId();
        }
        return null;
    }

    private String getMessageText(CallbackQuery callback) {
        if (callback.maybeInaccessibleMessage() instanceof Message message) {
            return message.text();
        }
        return null;
    }

    private String requireText(Message message, Runnable onEmpty) {
        if (message.text() == null || message.text().isBlank()) {
            onEmpty.run();
            return null;
        }
        return message.text().trim();
    }
}
