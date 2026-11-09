package com.skill_gate.handler.state;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.skill_gate.config.BotConstants;
import com.skill_gate.config.BotConstants.Employee;
import com.skill_gate.config.BotConstants.Lesson;
import com.skill_gate.config.BotConstants.Position;
import com.skill_gate.config.BotConstants.Question;
import com.skill_gate.config.BotConstants.Check;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.handler.ApprovalHandler;
import com.skill_gate.handler.admin.menu.AdminMenuHandler;
import com.skill_gate.handler.admin.check.AdminCheckHandler;
import com.skill_gate.handler.admin.employee.AdminEmployeeHandler;
import com.skill_gate.handler.admin.lesson.AdminLessonHandler;
import com.skill_gate.handler.admin.position.AdminPositionHandler;
import com.skill_gate.handler.admin.question.AdminQuestionHandler;
import com.skill_gate.service.BotService;
import org.springframework.stereotype.Component;

@Component
public class AdminStateProcessor extends AbstractStateProcessor {

    private final AdminMenuHandler menuHandler;
    private final AdminEmployeeHandler employeeHandler;
    private final AdminPositionHandler positionHandler;
    private final AdminLessonHandler lessonHandler;
    private final ApprovalHandler approvalHandler;
    private final AdminQuestionHandler questionHandler;
    private final AdminCheckHandler checkHandler;

    public AdminStateProcessor(
            BotService botService,
            AdminMenuHandler menuHandler,
            AdminEmployeeHandler employeeHandler,
            AdminPositionHandler positionHandler,
            AdminLessonHandler lessonHandler,
            ApprovalHandler approvalHandler,
            AdminQuestionHandler questionHandler, AdminCheckHandler checkHandler
    ) {
        super(botService);
        this.menuHandler = menuHandler;
        this.employeeHandler = employeeHandler;
        this.positionHandler = positionHandler;
        this.lessonHandler = lessonHandler;
        this.approvalHandler = approvalHandler;
        this.questionHandler = questionHandler;
        this.checkHandler = checkHandler;
    }

    @Override
    public void process(DbUser admin, Message message) {
        switch (admin.getCurrentState()) {
            case ADMIN_WAITING_NEW_POSITION ->
                    positionHandler.handleNewPosition(admin, message);

            case SUPER_ADMIN_EDITING_POSITION_NAME ->
                    positionHandler.handleEditPositionName(admin, message);
            case SUPER_ADMIN_EDITING_POSITION_DESCRIPTION ->
                    positionHandler.handleEditPositionDescription(admin, message);

            case SUPER_ADMIN_WAITING_NEW_LESSON_TITLE ->
                    lessonHandler.handleNewLessonTitle(admin, message);
            case SUPER_ADMIN_WAITING_NEW_LESSON_VIDEO_URL ->
                    lessonHandler.handleNewLessonVideoUrl(admin, message);
            case SUPER_ADMIN_WAITING_NEW_LESSON_SUMMARY ->
                    lessonHandler.handleNewLessonSummary(admin, message);

            case SUPER_ADMIN_EDITING_LESSON_TITLE ->
                    lessonHandler.handleEditLessonTitle(admin, message);
            case SUPER_ADMIN_EDITING_LESSON_NUMBER ->
                    lessonHandler.handleEditLessonNumber(admin, message);
            case SUPER_ADMIN_EDITING_LESSON_VIDEO_URL ->
                    lessonHandler.handleEditLessonVideoUrl(admin, message);
            case SUPER_ADMIN_EDITING_LESSON_SUMMARY ->
                    lessonHandler.handleEditLessonSummary(admin, message);

            case SUPER_ADMIN_WAITING_NEW_QUESTION_TEXT ->
                    questionHandler.handleNewQuestionText(admin, message);
            case SUPER_ADMIN_EDITING_QUESTION_TEXT ->
                    questionHandler.handleEditText(admin, message);
            case SUPER_ADMIN_EDITING_QUESTION_NUMBER ->
                    questionHandler.handleEditNumber(admin, message);
            case SUPER_ADMIN_WAITING_NEW_ANSWER_TEXT ->
                    questionHandler.handleNewAnswerText(admin, message);

            default -> sendUnknownCommand(admin);
        }
    }

    @Override
    public void process(DbUser admin, CallbackQuery callback) {
        var data = callback.data();
        if (data == null || data.isBlank()) {
            sendUnknownCommand(admin);
            return;
        }

        if (data.startsWith("approve:")) {
            approvalHandler.approve(Long.parseLong(data.split(":")[1]), callback);
        } else if (data.startsWith("reject:")) {
            approvalHandler.reject(Long.parseLong(data.split(":")[1]), callback);

        } else if (data.equals(BotConstants.ADMIN_MENU)) {
            menuHandler.showAdminMenu(admin);

        } else if (data.startsWith(Position.PAGE)) {
            positionHandler.showPositions(admin, Integer.parseInt(data.substring(Position.PAGE.length())));
        } else if (data.equals(Position.NEW)) {
            positionHandler.showNewPosition(admin);
        } else if (data.startsWith(Position.VIEW)) {
            positionHandler.handleViewPosition(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Position.EDIT)) {
            positionHandler.handleEditPosition(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.equals(Position.EDIT_NAME)) {
            positionHandler.showEditPositionName(admin);
        } else if (data.equals(Position.EDIT_DESCRIPTION)) {
            positionHandler.showEditPositionDescription(admin);
        } else if (data.startsWith(Position.DELETE)) {
            positionHandler.confirmDeletePosition(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Position.CONFIRM_DELETE) || data.startsWith(Position.REJECT_DELETE)) {
            positionHandler.deletePosition(admin, callback);
        } else if (data.startsWith(Position.CHOOSE)) {
            lessonHandler.showLessons(admin, Long.parseLong(data.split(":")[1]), 1);

        } else if (data.equals(Lesson.VIEW_LIST)) {
            positionHandler.showSelectPosition(admin);
        } else if (data.startsWith(Lesson.PAGE)) {
            var parts = data.split("_");
            lessonHandler.showLessons(admin, Long.parseLong(parts[2]), Integer.parseInt(parts[3]));
        } else if (data.equals(Lesson.NEW)) {
            lessonHandler.showNewLessonPositions(admin);
        } else if (data.startsWith(Lesson.TOGGLE_NEW_POSITION)) {
            lessonHandler.refreshNewLessonPositions(admin, callback);
        } else if (data.equals(Lesson.CONFIRM_NEW_POSITIONS)) {
            lessonHandler.handleNewLessonPositions(admin);
        } else if (data.startsWith(Lesson.VIEW)) {
            lessonHandler.handleViewLesson(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Lesson.EDIT)) {
            lessonHandler.handleEditLesson(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.equals(Lesson.EDIT_TITLE)) {
            lessonHandler.showEditLessonTitle(admin);
        } else if (data.equals(Lesson.EDIT_NUMBER)) {
            lessonHandler.showEditLessonNumber(admin);
        } else if (data.equals(Lesson.EDIT_VIDEO_URL)) {
            lessonHandler.showEditLessonVideoUrl(admin);
        } else if (data.equals(Lesson.EDIT_SUMMARY)) {
            lessonHandler.showEditLessonSummary(admin);
        } else if (data.startsWith(Lesson.DELETE)) {
            lessonHandler.confirmDeleteLesson(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Lesson.CONFIRM_DELETE) || data.startsWith(Lesson.REJECT_DELETE)) {
            lessonHandler.deleteLesson(admin, callback);

        } else if (data.startsWith(Employee.PAGE)) {
            employeeHandler.showEmployees(admin, Integer.parseInt(data.substring(Employee.PAGE.length())));
        } else if (data.equals(Employee.FILTERS)) {
            employeeHandler.showFilters(admin);
        } else if (data.equals(Employee.FILTERS_APPLY)) {
            employeeHandler.applyFilters(admin);
        } else if (data.equals(Employee.FILTERS_RESET)) {
            employeeHandler.resetFilters(admin);
        } else if (data.equals(Employee.FILTER_STATUS)) {
            employeeHandler.showStatusFilter(admin);
        } else if (data.equals(Employee.FILTER_ROLE)) {
            employeeHandler.showRoleFilter(admin);
        } else if (data.equals(Employee.FILTER_POSITION)) {
            employeeHandler.showPositionFilter(admin);
        } else if (data.startsWith(Employee.SET_FILTER_STATUS)) {
            employeeHandler.setStatusFilter(admin, data.split(":")[1]);
        } else if (data.startsWith(Employee.SET_FILTER_ROLE)) {
            employeeHandler.setRoleFilter(admin, data.split(":")[1]);
        } else if (data.startsWith(Employee.SET_FILTER_POSITION)) {
            employeeHandler.setPositionFilter(admin, data.split(":")[1]);
        } else if (data.startsWith(Employee.VIEW)) {
            employeeHandler.handleViewEmployee(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Employee.EDIT_ROLE)) {
            employeeHandler.showEditRole(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Employee.EDIT_STATUS)) {
            employeeHandler.showEditStatus(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Employee.EDIT_POSITION)) {
            employeeHandler.showEditPosition(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Employee.SET_ROLE)) {
            var parts = data.split(":");
            employeeHandler.handleSetRole(admin, Long.parseLong(parts[1]), UserRole.valueOf(parts[2]));
        } else if (data.startsWith(Employee.SET_STATUS)) {
            var parts = data.split(":");
            employeeHandler.handleSetStatus(admin, Long.parseLong(parts[1]), UserStatus.valueOf(parts[2]));
        } else if (data.startsWith(Employee.SET_POSITION)) {
            var parts = data.split(":");
            employeeHandler.handleSetPosition(admin, Long.parseLong(parts[1]), Long.parseLong(parts[2]));

        } else if (data.startsWith(Check.PAGE)) {
            var parts = data.split("_"); // checks_page_{lessonId}_{page}
            checkHandler.showChecks(admin, Long.parseLong(parts[2]), Integer.parseInt(parts[3]));
        } else if (data.startsWith(Check.NEW)) {
            checkHandler.createCheck(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Check.VIEW)) {
            checkHandler.handleViewCheck(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Check.DELETE)) {
            checkHandler.confirmDeleteCheck(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Check.CONFIRM_DELETE) || data.startsWith(Check.REJECT_DELETE)) {
            checkHandler.deleteCheck(admin, callback);

        } else if (data.startsWith(Question.PAGE)) {
            var parts = data.split("_");
            questionHandler.showQuestions(admin, Long.parseLong(parts[2]), Integer.parseInt(parts[3]));
        } else if (data.startsWith(Question.NEW) && !data.startsWith(Question.NEW_ANSWER)) {
            questionHandler.createQuestion(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Question.VIEW)) {
            questionHandler.handleViewQuestion(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Question.EDIT) && !data.equals(Question.EDIT_TEXT) && !data.equals(Question.EDIT_NUMBER)) {
            questionHandler.handleEditQuestion(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.equals(Question.EDIT_TEXT)) {
            questionHandler.showEditText(admin);
        } else if (data.equals(Question.EDIT_NUMBER)) {
            questionHandler.showEditNumber(admin);
        } else if (data.startsWith(Question.DELETE) && !data.startsWith(Question.DELETE_ANSWER)) {
            questionHandler.confirmDeleteQuestion(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Question.CONFIRM_DELETE) || data.startsWith(Question.REJECT_DELETE)) {
            questionHandler.deleteQuestion(admin, callback);
        } else if (data.startsWith(Question.TOGGLE_ANSWER)) {
            questionHandler.toggleAnswer(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Question.NEW_ANSWER)) {
            questionHandler.showNewAnswer(admin, Long.parseLong(data.split(":")[1]));
        } else if (data.startsWith(Question.DELETE_ANSWER)) {
            questionHandler.deleteAnswer(admin, Long.parseLong(data.split(":")[1]));
        }
    }
}
