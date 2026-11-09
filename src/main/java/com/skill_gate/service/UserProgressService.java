package com.skill_gate.service;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.DbUserProgress;
import com.skill_gate.exception.LessonNotFoundException;
import com.skill_gate.exception.UserNotFoundException;
import com.skill_gate.repository.LessonRepository;
import com.skill_gate.repository.UserProgressRepository;
import com.skill_gate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Transactional
    public void markVideoWatched(DbUser user, DbLesson lesson) {
        var progress = getOrCreateUserProgress(user, lesson);
        progress.setVideoWatched(true);
        save(progress);
    }

    @Transactional
    public void updateCheckResult(DbUser user, DbLesson lesson, BigDecimal score, boolean passed) {
        var progress = getOrCreateUserProgress(user, lesson);
        progress.setLastCheckScore(score);
        progress.setCheckAttemptCount(progress.getCheckAttemptCount() + 1);

        if (passed) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(ZonedDateTime.now());
            log.info("{} completed the check. Result: {}", user.getFullName(), score);
        }

        save(progress);
    }

    public Long getCheckAttemptCount(Long userId, Long lessonId) {
        return getCheckAttemptCount(getUser(userId), getLesson(lessonId));
    }

    public Long getCheckAttemptCount(DbUser user, DbLesson lesson) {
        return getOrCreateUserProgress(user, lesson).getCheckAttemptCount();
    }

    /**
     * Calculates the user's overall onboarding progress in percentage (0.0 - 100.0).
     *
     * <p>Progress is calculated as: (completed lessons / total active lessons for position) * 100</p>
     *
     * @param user the user
     * @return overall progress percentage
     */
    public double calculateOverallProgress(DbUser user) {
        if (user.getPosition() == null) return 0.0;

        var total = lessonRepository.countByPositionAndIsActiveTrue(user.getPosition());
        if (total == 0) return 0.0;

        var completed = userProgressRepository.countByUserAndUser_PositionAndIsCompletedTrue(user, user.getPosition());

        return (completed * 100.0) / total;
    }

    private DbUserProgress getOrCreateUserProgress(DbUser user, DbLesson lesson) {
        return userProgressRepository.findByUserAndLesson(user, lesson)
                .orElseGet(() -> createUserProgress(user, lesson));
    }

    private DbUserProgress createUserProgress(DbUser user, DbLesson lesson) {
        return new DbUserProgress(user, lesson, false, 0L, false);
    }

    private void save(DbUserProgress progress) {
        userProgressRepository.save(progress);
    }

    private DbUser getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " not found"));
    }

    private DbLesson getLesson(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson with id: " + lessonId + " not found"));
    }
}
