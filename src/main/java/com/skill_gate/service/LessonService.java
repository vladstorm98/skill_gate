package com.skill_gate.service;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.exception.LessonExistsException;
import com.skill_gate.exception.LessonNotFoundException;
import com.skill_gate.repository.LessonRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;

    private static final String DEFAULT_LESSON_TITLE = "_blank_";
    private static final Long DEFAULT_LESSON_NUMBER = 1000L;
    private static final Long DEFAULT_NOT_ACTIVE_LESSON_NUMBER = 9999L; // todo

    @Transactional
    public DbLesson createLesson(Set<DbPosition> positions) {
        var title = DEFAULT_LESSON_TITLE + System.currentTimeMillis();
        var lesson = new DbLesson(positions, DEFAULT_LESSON_NUMBER, title, true);
        save(lesson);

        log.info("Lesson created with id #{}", lesson.getId());
        return lesson;
    }

    public Optional<DbLesson> getLessonByPositionAndNumber(DbPosition position, Long lessonNumber) {
        var lesson = lessonRepository.findByPositionAndIsActiveTrueAndNumber(position, lessonNumber);

        log.info("Lesson was retrieved for position: {}", position);
        return lesson;
    }

    @Cacheable(cacheNames = "lessonCache", key = "#id")
    public DbLesson getLesson(@NonNull Long id) {
        var lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new LessonNotFoundException("Lesson with id #" + id + " not found"));

        var positions = getPositionCopy(lesson);
        lesson.setPositions(positions);

        log.info("Lesson  with id #{} was retrieved", id);
        return lesson;
    }

    public Page<DbLesson> getPage(int page, int size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("number"));
        var lessonsPage = lessonRepository.findAll(pageable);

        if (lessonsPage.isEmpty()) {
            throw new LessonNotFoundException("Lessons not found");
        }

        log.info("Retrieved lessons page: {}/{} (total lessons: {})",
                page, lessonsPage.getTotalPages(), lessonsPage.getTotalElements());
        return lessonsPage;
    }

    public Page<DbLesson> getPageByPosition(@NonNull DbPosition position, int page, int size) {
        var pageable = PageRequest.of(page - 1, size);
        var lessonsPage = lessonRepository.findByPositionOrderByNumberAsc(position, pageable);

        if (lessonsPage.isEmpty()) {
            throw new LessonNotFoundException("Lessons not found");
        }

        log.info("Retrieved lessons page for position '{}' : {}/{} (total: {})",
                position.getName(), page, lessonsPage.getTotalPages(), lessonsPage.getTotalElements());

        return lessonsPage;
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#id")
    public void updateTitle(Long id, @NonNull String title) {
        var lesson = getLesson(id);

        if (!lesson.getTitle().equalsIgnoreCase(title) && lessonExists(title)) {
            throw new LessonExistsException("Lesson with title '" + title + "' already exists");
        }

        lesson.setTitle(title);
        save(lesson);

        log.info("Lesson #{} title changed to '{}'", id, title);
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#id")
    public void updateNumber(Long id, @NonNull Long newNumber) {
        var lesson = getLesson(id);
        var oldNumber = lesson.getNumber();

        if (newNumber.equals(oldNumber)) return;

        var positions = lesson.getPositions();

        if (positions == null || positions.isEmpty()) {
            lesson.setNumber(newNumber);
            save(lesson);
            return;
        }

        for (DbPosition position : positions) {
            if (newNumber < oldNumber) {
                lessonRepository.shiftUp(position.getId(), newNumber, oldNumber, lesson.getId());
            } else {
                lessonRepository.shiftDown(position.getId(), oldNumber, newNumber, lesson.getId());
            }
        }

        lesson.setNumber(newNumber);
        save(lesson);

        log.info("Lesson #{} number changed from {} to {}", id, oldNumber, newNumber);
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#id")
    public void updateVideoUrl(Long id, @NonNull String videoUrl) {
        var lesson = getLesson(id);
        lesson.setVideoUrl(videoUrl);
        save(lesson);

        log.info("Lesson #{} videoUrl changed to '{}'", id, videoUrl);
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#id")
    public void updateSummary(Long id, @NonNull String summary) {
        var lesson = getLesson(id);
        lesson.setSummary(summary);
        save(lesson);

        log.info("Lesson #{} summary changed to '{}'", id, summary);
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#id")
    public void deleteLesson(@NonNull Long id) {
        var lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new LessonNotFoundException("Lesson with id #" + id + " not found"));

        var number = lesson.getNumber();
        var positions = new HashSet<>(lesson.getPositions());

        lessonRepository.delete(lesson);

        for (var position : positions) {
            lessonRepository.shiftDownAfterDelete(position.getId(), number);
        }

        log.info("Lesson #{} (number {}) was deleted", id, number);
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#number")
    public DbLesson toggleIsActive(@NonNull Long number) {
        var lesson = lessonRepository.findByNumber(number)
                .orElseThrow(() -> new LessonNotFoundException("Lesson №" + number + " not found"));

        lesson.setIsActive(!lesson.getIsActive());
        save(lesson);

        log.info("Lesson №{} was updated", number);
        return lesson;
    }

    @Transactional
    @CacheEvict(cacheNames = "lessonCache", key = "#lesson.id")
    public void save(DbLesson lesson) {
        lessonRepository.save(lesson);
    }

    private Set<DbPosition> getPositionCopy(DbLesson lesson) {
        return lesson.getPositions().stream()
                .map(position -> {
                    var copy = new DbPosition();
                    copy.setId(position.getId());
                    copy.setName(position.getName());
                    return copy;
                })
                .collect(Collectors.toCollection(HashSet::new));
    }

    private boolean lessonExists(@NonNull String title) {
        return lessonRepository.existsByTitleIgnoreCase(title);
    }
}
