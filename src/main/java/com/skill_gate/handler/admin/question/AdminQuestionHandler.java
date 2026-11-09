package com.skill_gate.handler.admin.question;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.cache.CheckDraftService;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.exception.QuestionNotFoundException;
import com.skill_gate.handler.state.StateManager;
import com.skill_gate.service.CheckService;
import com.skill_gate.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;
import static com.skill_gate.config.BotConstants.Question;

@Component
@RequiredArgsConstructor
public class AdminQuestionHandler {

    private final QuestionService questionService;
    private final CheckService checkService;
    private final CheckDraftService draftService;
    private final StateManager stateManager;
    private final AdminQuestionSender questionSender;

    public void showQuestions(DbUser admin, Long checkId, Integer page) {
        if (page == null || page < 1) page = 1;
        draftService.saveCheckId(admin, checkId);

        var check = checkService.getCheck(checkId);
        try {
            var questionPage = questionService.getPage(check, page, PAGE_SIZE);
            questionSender.sendQuestions(admin, checkId, questionPage, page);
        } catch (QuestionNotFoundException e) {
            questionSender.sendEmpty(admin);
        }
    }

    public void createQuestion(DbUser admin, Long checkId) {
        var check = checkService.getCheck(checkId);
        var question = questionService.createQuestion(check);
        draftService.saveQuestionId(admin, question.getId());
        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_QUESTION_TEXT);
        questionSender.sendEditText(admin);
    }

    public void handleViewQuestion(DbUser admin, Long questionId) {
        var question = questionService.getQuestion(questionId);
        draftService.saveQuestionId(admin, questionId);

        var options = questionService.getAnswerOptions(question);
        questionSender.sendViewQuestion(admin, question, options);
    }

    public void handleEditQuestion(DbUser admin, Long questionId) {
        var question = questionService.getQuestion(questionId);
        draftService.saveQuestionId(admin, questionId);
        questionSender.sendEditQuestion(admin, question);
    }

    public void showEditText(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_QUESTION_TEXT);
        questionSender.sendEditText(admin);
    }

    public void handleEditText(DbUser admin, Message message) {
        var questionId = draftService.getQuestionId(admin);
        if (message.text() == null || message.text().isBlank()) {
            questionSender.sendEditText(admin);
            return;
        }
        questionService.updateText(questionId, message.text().trim());
        questionSender.sendFieldUpdated(admin, "Text");
        handleViewQuestion(admin, questionId);
    }

    public void showEditNumber(DbUser admin) {
        stateManager.change(admin, UserState.SUPER_ADMIN_EDITING_QUESTION_NUMBER);
        questionSender.sendEditNumber(admin);
    }

    public void handleEditNumber(DbUser admin, Message message) {
        var questionId = draftService.getQuestionId(admin);
        if (message.text() == null || message.text().isBlank()) {
            questionSender.sendEditNumber(admin);
            return;
        }
        long number;
        try {
            number = Long.parseLong(message.text().trim());
        } catch (NumberFormatException e) {
            questionSender.sendEditNumber(admin);
            return;
        }
        questionService.updateNumber(questionId, number);
        questionSender.sendFieldUpdated(admin, "Number");
        handleViewQuestion(admin, questionId);
    }

    public void handleNewQuestionText(DbUser admin, Message message) {
        var questionId = draftService.getQuestionId(admin);
        if (questionId == null) {
            questionSender.sendEmpty(admin);
            return;
        }

        if (message.text() == null || message.text().isBlank()) {
            questionSender.sendEmpty(admin);
            return;
        }
        questionService.updateText(questionId, message.text().trim());
        questionSender.sendFieldUpdated(admin, "Text");

        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_ANSWER_TEXT);
        questionSender.sendNewAnswerText(admin);
    }

    public void showNewAnswer(DbUser admin, Long questionId) {
        draftService.saveQuestionId(admin, questionId);
        stateManager.change(admin, UserState.SUPER_ADMIN_WAITING_NEW_ANSWER_TEXT);
        questionSender.sendNewAnswerText(admin);
    }

    public void handleNewAnswerText(DbUser admin, Message message) {
        var questionId = draftService.getQuestionId(admin);
        var question = questionService.getQuestion(questionId);
        if (message.text() == null || message.text().isBlank()) {
            questionSender.sendNewAnswerText(admin);
            return;
        }
        questionService.createAnswerOption(question, message.text().trim(), false);
        questionSender.sendFieldUpdated(admin, "Answer");
        handleViewQuestion(admin, questionId);
    }

    public void toggleAnswer(DbUser admin, Long optionId) {
        questionService.toggleCorrect(optionId);
        var option = questionService.getAnswerOption(optionId);
        handleViewQuestion(admin, option.getQuestion().getId());
    }

    public void deleteAnswer(DbUser admin, Long optionId) {
        var option = questionService.getAnswerOption(optionId);
        var questionId = option.getQuestion().getId();
        questionService.deleteAnswerOption(optionId);
        handleViewQuestion(admin, questionId);
    }

    public void confirmDeleteQuestion(DbUser admin, Long questionId) {
        questionSender.sendDeleteConfirmation(admin, questionService.getQuestion(questionId));
    }

    public void deleteQuestion(DbUser admin, CallbackQuery callback) {
        var data = callback.data();
        var confirmed = data.startsWith(Question.CONFIRM_DELETE);
        var questionId = Long.parseLong(data.split(":")[1]);
        Long checkId = null;

        if (confirmed) {
            var question = questionService.getQuestion(questionId);
            checkId = question.getCheck().getId();
            questionService.deleteQuestion(questionId);
        }

        questionSender.sendDeleteResult(admin, callback, confirmed);
        if (checkId != null) {
            showQuestions(admin, checkId, 1);
        }
    }
}
