package com.skill_gate.service;

import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbQuestion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing temporary user check (test) sessions during onboarding.
 * Stores current test state in memory (for MVP).
 * In production, it is recommended to replace with Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckSessionService {

    // In-memory storage: userId -> session data
    private final QuestionService questionService;
    private final CheckService checkService;
    private final UserProgressService userProgressService;
    private final Map<Long, CheckSession> sessions = new ConcurrentHashMap<>();

    /**
     * Starts a new check session for the user.
     *
     * @param userId    user id
     * @param checkId   test/check id
     * @param lessonId  current lesson id
     */
    public void startCheckSession(Long userId, Long checkId, Long lessonId) {
        var session = createSession(checkId, lessonId);
        sessions.put(userId, session);
        log.info("Started new check session for user #{} on lesson #{}", userId, lessonId);
    }

    /**
     * Saves user's answer to a specific question.
     *
     * @param userId         user id
     * @param questionId     id of the answered question
     * @param answerOption   selected answer option
     */
    public void saveAnswer(Long userId, Long questionId, DbAnswerOption answerOption) {
        var session = sessions.get(userId);
        if (session != null) {
            session.saveAnswer(questionId, answerOption.getIsCorrect());
        }
        // todo add opportunity to save answers
    }

    /**
     * Returns the next unanswered question in the current check.
     *
     * @param userId  user id
     * @return next unanswered Question or null if all questions are answered
     */
    public DbQuestion getNextQuestion(Long userId) {
        var session = sessions.get(userId);
        return session != null ? session.getNextQuestion() : null;
    }

    /**
     * Returns total number of questions in the current check.
     *
     * @param userId user id
     * @return total questions count
     */
    public int getTotalQuestions(Long userId) {
        var session = sessions.get(userId);
        return session != null ? session.getTotalQuestions() : 0;
    }

    /**
     * Returns number of correct answers given by the user.
     *
     * @param userId user id
     * @return count of correct answers
     */
    public Long getCorrectAnswersCount(Long userId) {
        var session = sessions.get(userId);
        return session != null ? session.getCorrectAnswersCount() : 0;
    }

    /**
     * Returns current lesson id for the user.
     *
     * @param userId user id
     * @return current lesson id
     */
    public Long getCurrentLessonId(Long userId) {
        var session = sessions.get(userId);
        return session != null ? session.getLessonId() : null;
    }

    /**
     * Returns current attempt number for this check.
     *
     * @param userId user id
     * @return attempt number
     */
    public Long getCheckAttemptCount(Long userId) {
        var session = sessions.get(userId);
        var lessonId = session.getLessonId();
        return userProgressService.getCheckAttemptCount(userId, lessonId);
    }

    /**
     * Clears the check session after completion or cancellation.
     *
     * @param userId user id
     */
    public void clearSession(Long userId) {
        sessions.remove(userId);
        log.info("Check session cleared for user {}", userId);
    }

    /**
     * Inner static class that holds the state of one user's knowledge check session.
     */
    @Getter
    private static class CheckSession {
        private final Long checkId;
        private final Long lessonId;
        private final List<DbQuestion> questions;
        private final Map<Long, Boolean> answers = new HashMap<>();

        public CheckSession(Long checkId, Long lessonId, List<DbQuestion> questions) {
            this.checkId = checkId;
            this.lessonId = lessonId;
            this.questions = questions;
        }

        /**
         * Saves the user's answer for a specific question.
         *
         * @param questionId id of the answered question
         * @param isCorrect  true if the answer is correct, false otherwise
         */
        public void saveAnswer(Long questionId, boolean isCorrect) {
            answers.put(questionId, isCorrect);
        }

        /**
         * Returns the next unanswered question in ascending order.
         *
         * @return next unanswered DbQuestion or null if all questions have been answered
         */
        public DbQuestion getNextQuestion() {
            for (var question : questions) {
                if (!answers.containsKey(question.getId())) {
                    return question;
                }
            }
            return null;
        }

        /**
         * Returns the total number of questions in this check session.
         *
         * @return total count of questions
         */
        public Integer getTotalQuestions() {
            return questions.size();
        }

        /**
         * Returns the number of correctly answered questions.
         *
         * @return count of correct answers
         */
        public Long getCorrectAnswersCount() {
            return answers.values().stream()
                    .filter(Boolean::booleanValue)
                    .count();
        }
    }

    /**
     * Creates a new knowledge check session for the user.
     *
     * @param checkId  id of the check (test) template
     * @param lessonId id of the lesson to which this check belongs
     * @return new CheckSession instance with loaded questions
     */
    private CheckSession createSession(Long checkId, Long lessonId) {
        var questions = getQuestions(checkId);
        return new CheckSession(checkId, lessonId, questions);
    }

    /**
     * Retrieves all questions for a specific knowledge check.
     *
     * @param checkId id of the check (test)
     * @return list of questions for this check
     */
    private List<DbQuestion> getQuestions(Long checkId) {
        var check = checkService.getCheck(checkId);
        return questionService.getQuestions(check);
    }
}
