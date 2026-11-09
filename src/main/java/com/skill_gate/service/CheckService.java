package com.skill_gate.service;

import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbLesson;
import com.skill_gate.exception.CheckNotFoundException;
import com.skill_gate.repository.CheckRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckService {

    private final CheckRepository checkRepository;

    public DbCheck getCheck(@NonNull Long id) {
        return checkRepository.findByIdWithLesson(id)
                .orElseThrow(() -> new CheckNotFoundException("Check with id #" + id + " not found"));
    }

    public DbCheck getCheckForLesson(DbLesson lesson, @NonNull Long version) {
        return checkRepository.findByLessonAndVersion(lesson, version)
                .orElseThrow(() -> new CheckNotFoundException(
                        "Check for lesson '" + lesson.getTitle() + "' (v." + version + ") not found"));
    }

    public Page<DbCheck> getPage(DbLesson lesson, int page, int size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("version").descending());
        var result = checkRepository.findByLesson(lesson, pageable);
        if (result.isEmpty()) {
            throw new CheckNotFoundException("Checks not found");
        }
        return result;
    }

    @Transactional
    public DbCheck createCheck(DbLesson lesson) {
        Long nextVersion = checkRepository.findMaxVersionByLesson(lesson).orElse(0L) + 1;
        var check = new DbCheck(lesson, nextVersion);
        checkRepository.save(check);
        log.info("Check #{} v.{} created for lesson #{}", check.getId(), nextVersion, lesson.getId());
        return getCheck(check.getId());
    }

    @Transactional
    public void deleteCheck(@NonNull Long id) {
        var check = getCheck(id);
        checkRepository.delete(check);
        log.info("Check #{} deleted", id);
    }
}
