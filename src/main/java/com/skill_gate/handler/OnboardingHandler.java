package com.skill_gate.handler;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.dto.CheckResult;
import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbQuestion;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.exception.AnswerOptionNotFoundException;
import com.skill_gate.handler.employee.menu.EmployeeMenuHandler;
import com.skill_gate.service.CheckResultService;
import com.skill_gate.service.CheckService;
import com.skill_gate.service.CheckSessionService;
import com.skill_gate.service.LessonService;
import com.skill_gate.service.BotService;
import com.skill_gate.service.QuestionService;
import com.skill_gate.service.UserProgressService;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Manages the entire employee onboarding flow:
 *  Video watching → Check (Test) → Result evaluation → Next lesson or retry.
 */
@Component
@RequiredArgsConstructor
public class OnboardingHandler {

    private final BotService botService;
    private final CheckService checkService;
    private final CheckResultService checkResultService;
    private final UserService userService;
    private final LessonService lessonService;
    private final UserProgressService userProgressService;
    private final QuestionService questionService;
    private final EmployeeMenuHandler employeeMenuHandler;

    // TODO: now it's temporary storage for current check session (in production better to use Redis or DB)
    private final CheckSessionService checkSessionService;

    /**
     * Starts or continue onboarding process for the user.
     *
     * @param user the current user
     */
    public void startOrContinueOnboarding(DbUser user) {
        getCurrentLesson(user)
                .ifPresentOrElse(
                        lesson -> sendCurrentLessonToUser(user, lesson),
                        () -> finishOnboarding(user)
                );
    }

    /**
     * Handles the callback when user marks a video as watched and start knowledge check.
     *
     * @param callback the callback query from Telegram
     * @param user     the current user
     */
    public void handleVideoWatched(CallbackQuery callback, DbUser user) {
        var lesson = getLessonByCallback(callback);

        markVideoWatched(callback, user, lesson);
        startCheckSession(user, lesson);
    }

    /**
     * Handles user's answer to a knowledge check question.
     *
     * @param callback callback query from the inline button
     * @param user     current user entity
     */
    public void handleAnswerFromUser(CallbackQuery callback, DbUser user) {
        handleAnswer(callback, user);

        getCurrentLesson(user)
                .ifPresentOrElse(
                        lesson -> continueCheck(user, lesson),
                        () -> finishOnboarding(user)
                );
    }

    /**
     * Completes the onboarding process and grants access to the knowledge base.
     *
     * @param user current user entity
     */
    public void finishOnboarding(DbUser user) {
        user.setOnboardingProgress(BigDecimal.valueOf(100));
        setStatusAndSave(user, UserStatus.COMPLETED);

        botService.sendMessage(user.getTelegramId(), """
                🎉 Congratulations! You have successfully completed the onboarding!
                
                Now you have full access to the knowledge base for your position.""");
        employeeMenuHandler.showMainMenu(user);
    }

    /**
     * Sends the specified lesson to the user via Telegram bot.
     *
     * <p>This includes:
     * <ul>
     *   <li>Lesson title and number</li>
     *   <li>Video URL (if available)</li>
     *   <li>Inline button "✅ Video Watched" for further progress</li>
     * </ul>
     *
     * @param user   the user to whom the lesson should be sent
     * @param lesson the lesson to be sent
     */
    private void sendCurrentLessonToUser(DbUser user, DbLesson lesson) {
        var text = """
                📚 Lesson %s: %s
                
                Watch the video below:
                """.formatted(lesson.getNumber(), lesson.getTitle());

        if (lesson.getVideoUrl() != null) {
            text += lesson.getVideoUrl() + "\n";
        }

        text += "After watching the video, press the button:";
        var keyboard = prepareVideoWatchedKeyboard(lesson);

        sendMessageWithMarkup(user.getTelegramId(), text, keyboard);
    }

    private InlineKeyboardMarkup prepareVideoWatchedKeyboard(DbLesson lesson) {
        var button = new InlineKeyboardButton("✅ Video Watched")
                .callbackData("video_watched:" + lesson.getId());

        return new InlineKeyboardMarkup(button);
    }

    private Optional<DbLesson> getCurrentLesson(DbUser user) {
        return lessonService.getLessonByPositionAndNumber(user.getPosition(), user.getCurrentLessonNumber());
    }

    /**
     * Marks the video as watched, updates user state, sends confirmation message and saves the user.
     *
     * @param callback the callback query from Telegram
     * @param user     the current user
     * @param lesson   the lesson that was watched
     */
    private void markVideoWatched(CallbackQuery callback, DbUser user, DbLesson lesson) {
        userProgressService.markVideoWatched(user, lesson);
        user.setCurrentState(UserState.ONBOARDING_WAITING_CHECK);
        botService.sendMessage(callback.message().chat().id(), """
                ✅ Video marked as watched.
                
                Now let's take the knowledge check for this lesson.""");

        userService.save(user);
    }

    /**
     * Starts a new knowledge check session for the user.
     *
     * @param user   the current user
     * @param lesson the lesson for which the check is started
     */
    private void startCheckSession(DbUser user, DbLesson lesson) {
        var version = getCheckAttemptCount(user, lesson);
        var check = getCheckForLesson(lesson, version);

        checkSessionService.startCheckSession(user.getId(), check.getId(), lesson.getId());
    }

    /**
     * Retrieves the appropriate check version for the lesson based on attempt count.
     *
     * @param lesson   the lesson
     * @param version  number of previous attempts by the user
     * @return the check to be presented to the user
     */
    private DbCheck getCheckForLesson(DbLesson lesson, Long version) {
        return checkService.getCheckForLesson(lesson, version);
    }

    /**
     * Returns the number of times the user has already attempted the knowledge check
     * for this specific lesson.
     *
     * @param user   the current user
     * @param lesson the lesson
     * @return attempt count
     */
    private Long getCheckAttemptCount(DbUser user, DbLesson lesson) {
        return userProgressService.getCheckAttemptCount(user, lesson);
    }

    /**
     * Saves the user's answer to a question received via callback query.
     *
     * <p>Sends feedback to the user
     * (correct/incorrect + explanation if available).</p>
     *
     * @param callback the callback query containing question and answer IDs
     * @param user     the user who answered the question
     */
    private void handleAnswer(CallbackQuery callback, DbUser user) {
        var question = getQuestionByCallback(callback);
        var answerOption = getAnswerOptionByCallback(callback);

        checkSessionService.saveAnswer(user.getId(), question.getId(), answerOption);

        var resultText = buildAnswerResultMessage(question, answerOption);
        botService.sendMessage(callback.message().chat().id(), resultText);
    }

    /**
     * Continues the knowledge check by sending the next question to the user
     * or finishing the check if there are no more questions.
     *
     * @param user   the user currently taking the knowledge check
     * @param lesson the current lesson
     */
    private void continueCheck(DbUser user, DbLesson lesson) {
        getNextQuestion(user)
                .ifPresentOrElse(
                        question -> sendNextQuestionToUser(user, question),
                        () -> finishCheck(user, lesson)
                );
    }

    /**
     * Sends the next question to the user with a list of answer options as inline keyboard buttons.
     *
     * @param user     the user to whom the question should be sent
     * @param question the question to display
     */
    private void sendNextQuestionToUser(DbUser user, DbQuestion question) {
        InlineKeyboardMarkup markup = buildAnswerOptionsMarkup(question);

        String messageText = """
                ❓ Question %d/%d
                
                %s""".formatted(
                question.getNumber(),
                checkSessionService.getTotalQuestions(user.getId()),
                question.getText()
        );

        botService.sendMessageWithMarkup(user.getTelegramId(), messageText, markup);
    }

    /**
     * Builds an inline keyboard markup containing all answer options for the given question.
     *
     * <p>Each answer option is placed on a separate row as a single button.</p>
     * <p>Callback data format: "check_answer:{questionId}:{answerOptionId}"</p>
     *
     * @param question the question for which answer buttons should be created
     * @return configured {@link InlineKeyboardMarkup} with answer options
     */
    private InlineKeyboardMarkup buildAnswerOptionsMarkup(DbQuestion question) {
        InlineKeyboardButton[][] rows = getAnswerOptions(question).stream()
                .map(option -> new InlineKeyboardButton(option.getText())
                        .callbackData("check_answer:" + question.getId() + ":" + option.getId()))
                .map(button -> new InlineKeyboardButton[]{button})
                .toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(rows);
    }

    /**
     * Finishes the knowledge check, calculates result, saves progress and decides next step.
     *
     * @param user  the user who completed the check
     * @param lesson the lesson being checked
     */
    private void finishCheck(DbUser user, DbLesson lesson) {
        var result = calculateCheckResult(user);

        saveCheckResults(user, lesson, result);
        updateUserProgress(user, lesson, result);

        sendCheckResultMessage(user, lesson, result);

        if (result.passed()) {
            completeLessonAndProceedToNext(user);
        } else {
            handleFailedCheck(user);
        }

        checkSessionService.clearSession(user.getId());
    }

    /**
     * Calculates the final result of the knowledge check for the user.
     *
     * <p>Computes the percentage score based on correct answers and total questions,
     * determines whether the user passed (score ≥ 70%), and returns the result
     * as a {@link CheckResult} object.</p>
     *
     * @param user the user who completed the knowledge check
     * @return the calculated check result containing score, passed status, etc.
     */
    private CheckResult calculateCheckResult(DbUser user) {
        var total = getTotalQuestions(user);
        var correct = getCorrectAnswersCount(user);

        var score = BigDecimal.valueOf((double) correct / total * 100)
                .setScale(2, RoundingMode.HALF_UP);

        boolean passed = score.compareTo(BigDecimal.valueOf(70)) >= 0;

        return new CheckResult(total, correct, score, passed);
    }

    /**
     * Saves the check result to the database and records the attempt.
     *
     * @param user   the user who took the check
     * @param lesson  the lesson entity
     * @param result the calculated result of the check
     */
    private void saveCheckResults(DbUser user, DbLesson lesson, CheckResult result) {
        var checkAttemptCount = checkSessionService.getCheckAttemptCount(user.getId());
        var check = checkService.getCheckForLesson(lesson, checkAttemptCount);

        checkResultService.saveCheckResult(user, check, result.score(), result.passed(), checkAttemptCount);
    }

    /**
     * Updates the user's overall progress for the current lesson based on check results.
     *
     * <p>Retrieves the current lesson and updates the user's progress record with
     * the achieved score and pass/fail status.</p>
     *
     * @param user   the user
     * @param result the check result
     */
    private void updateUserProgress(DbUser user, DbLesson lesson, CheckResult result) {
        userProgressService.updateCheckResult(user, lesson, result.score(), result.passed());
    }

    /**
     * Sends the knowledge check result message to the user via Telegram.
     *
     * @param user   the user to notify
     * @param result the check result to include in the message
     */
    private void sendCheckResultMessage(DbUser user, DbLesson lesson, CheckResult result) {
        String message = buildCheckResultMessage(lesson, result);
        botService.sendMessage(user.getTelegramId(), message);
    }

    /**
     * Handles successful completion of the knowledge check (score ≥ 70%).
     *
     * <p>Increments the user's current lesson number, saves the user and
     * automatically starts the next lesson in the onboarding flow.</p>
     *
     * @param user the user who passed the check
     */
    private void completeLessonAndProceedToNext(DbUser user) {
        user.setCurrentLessonNumber(user.getCurrentLessonNumber() + 1);

        var progress = userProgressService.calculateOverallProgress(user);
        user.setOnboardingProgress(BigDecimal.valueOf(progress));
        save(user);

        startOrContinueOnboarding(user);
    }

    /**
     * Handles failed knowledge check (score < 70%).
     *
     * <p>Changes user state so they can retake the video and check,
     * saves the user and restarts the onboarding flow for the same lesson.</p>
     *
     * @param user the user who failed the check
     */
    private void handleFailedCheck(DbUser user) {
        setCurrentStateAndSave(user, UserState.ONBOARDING_WAITING_VIDEO);

        startOrContinueOnboarding(user);
    }

    /**
     * Builds a detailed result message for the user after completing a knowledge check.
     *
     * <p>Includes score, number of correct answers, congratulatory or motivational text,
     * and the lesson summary (if available and the user passed).</p>
     *
     * @param lesson the lesson
     * @param result the check result
     * @return fully formatted message ready to be sent to the user
     */
    private String buildCheckResultMessage(DbLesson lesson, CheckResult result) {
        StringBuilder message = new StringBuilder("""
            📊 Knowledge check completed!
            
            Correct answers: %d/%d
            Score: %s%%
            """.formatted(result.correctAnswers(), result.totalQuestions(), result.score()));

        if (result.passed()) {
            message.append("\n\n🎉 Excellent! You passed the knowledge check.");

            if (lesson.getSummary() != null && !lesson.getSummary().isBlank()) {
                message.append("\n\n📝 Summary of the lesson:\n").append(lesson.getSummary());
            }
        } else {
            message.append("\n\n❗ You scored less than 70%. ")
                   .append("You need to watch the video again and retake the knowledge check.");
        }

        return message.toString();
    }

    /**
     * Builds the feedback message for the user's answer.
     *
     * @param question     the question that was answered
     * @param answerOption the option selected by the user
     * @return formatted result text with correctness and optional explanation
     */
    private String buildAnswerResultMessage(DbQuestion question, DbAnswerOption answerOption) {
        String resultText = answerOption.getIsCorrect()
                ? "✅ Correct!"
                : "❌ Wrong. The correct answer is: " + getCorrectAnswerText(question);

        if (hasExplanation(question)) {
            resultText += "\n\nExplanation: " + question.getExplanation();
        }

        return resultText;
    }

    /**
     * Returns the text of the correct answer for the given question.
     *
     * @param question the question entity
     * @return text of the correct answer or "Not found" if none
     */
    private String getCorrectAnswerText(DbQuestion question) {
        return questionService.getAnswerOptions(question)
                .stream()
                .filter(DbAnswerOption::getIsCorrect)
                .findFirst()
                .map(DbAnswerOption::getText)
                .orElseThrow(() -> new AnswerOptionNotFoundException("Correct answer text not found"));
    }

    /**
     * Checks whether the given question has a non-empty explanation.
     *
     * @param question the question to check
     * @return {@code true} if the question has a non-blank explanation, {@code false} otherwise
     */
    private boolean hasExplanation(DbQuestion question) {
        return question.getExplanation() != null && !question.getExplanation().isBlank();
    }

    /**
     * Retrieves the next unanswered question for the current check session.
     *
     * <p>Returns an empty Optional if there are no more questions left
     * (meaning the check should be finished).</p>
     *
     * @param user  the user taking the check
     * @return Optional containing the next question, or empty if check is complete
     */
    private Optional<DbQuestion> getNextQuestion(DbUser user) {
        return Optional.ofNullable(checkSessionService.getNextQuestion(user.getId()));
    }

    /**
     * Retrieves all available answer options for the specified question.
     *
     * @param question the question for which to retrieve answer options
     * @return list of answer options for the question
     */
    private List<DbAnswerOption> getAnswerOptions(DbQuestion question) {
        return questionService.getAnswerOptions(question);
    }

    /**
     * Extracts the lesson ID from callback data and retrieves the lesson.
     *
     * <p>Expected callback data format: "video_watched:{lessonId}"</p>
     *
     * @param callback the callback query from Telegram
     * @return the corresponding lesson
     */
    private DbLesson getLessonByCallback(CallbackQuery callback) {
        var lessonId = Long.parseLong(callback.data().split(":")[1]);
        return lessonService.getLesson(lessonId);
    }

    /**
     * Extracts question IDs from callback data and retrieves DbQuestion entity.
     *
     * <p>Expected callback data format: "{prefix}:{questionId}:{answerOptionId}"</p>
     *
     * @param callback the callback query
     * @return the question being answered
     */
    private DbQuestion getQuestionByCallback(CallbackQuery callback) {
        var questionId = Long.parseLong(callback.data().split(":")[1]);
        return questionService.getQuestion(questionId);
    }

    /**
     * Extracts answer option ID from callback data and retrieves DbAnswerOption entity.
     *
     * <p>Expected callback data format: "{prefix}:{questionId}:{answerOptionId}"</p>
     *
     * @param callback the callback query
     * @return the selected answer option
     */
    private DbAnswerOption getAnswerOptionByCallback(CallbackQuery callback) {
        var answerOptionId = Long.parseLong(callback.data().split(":")[2]);
        return questionService.getAnswerOption(answerOptionId);
    }

    /**
     * Retrieves the total number of questions in the current knowledge check session.
     *
     * <p>This value is used to calculate the user's score (e.g. "5/10").</p>
     *
     * @param user the user whose check session is being processed
     * @return total number of questions in the check
     */
    private Long getTotalQuestions(DbUser user) {
        return (long) checkSessionService.getTotalQuestions(user.getId());
    }

    /**
     * Retrieves the number of questions that the user answered correctly
     * in the current knowledge check session.
     *
     * <p>This value is used to calculate the percentage score.</p>
     *
     * @param user the user whose check session is being processed
     * @return number of correctly answered questions
     */
    private Long getCorrectAnswersCount(DbUser user) {
        return checkSessionService.getCorrectAnswersCount(user.getId());
    }

    private void sendMessage(Long chatId, String text) {
        botService.sendMessage(chatId, text);
    }

    private void sendMessageWithMarkup(Long chatId, String text, InlineKeyboardMarkup markup) {
        botService.sendMessageWithMarkup(chatId, text, markup);
    }

    private void setStatusAndSave(DbUser user, UserStatus status) {
        user.setStatus(status);
        save(user);
    }

    private void setCurrentStateAndSave(DbUser user, UserState userState) {
        user.setCurrentState(userState);
        save(user);
    }

    private void save(DbUser user) {
        userService.save(user);
    }
}
