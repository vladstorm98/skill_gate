package com.skill_gate.service;

import com.skill_gate.entity.DbPosition;
import com.skill_gate.exception.LessonNotFoundException;
import com.skill_gate.exception.PositionExistsException;
import com.skill_gate.exception.PositionNotFoundException;
import com.skill_gate.repository.PositionRepository;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionRepository positionRepository;

    @Transactional
    public void createPosition(@NonNull String name) {
        if (positionExists(name)) {
            throw new PositionExistsException("Position with name '" + name + "' already exists. Try again:");
        }

        save(new DbPosition(name));

        log.info("Position '{}' was created", name);
    }

    @Cacheable(cacheNames = "positionCache", key = "#id")
    public DbPosition getPosition(@NonNull Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException("Position with id #" + id + " not found"));
    }

    public List<DbPosition> getAllPositions() {
        var positions = positionRepository.findAll();

        if (positions.isEmpty()) {
            throw new PositionNotFoundException("Positions not found");
        }

        log.info("All positions were retrieved");
        return positions;
    }

    public Page<DbPosition> getPage(int page, int size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("name"));
        var positionPage = positionRepository.findAll(pageable);

        if (positionPage.isEmpty()) {
            throw new LessonNotFoundException("Positions not found");
        }

        log.info("Retrieved positions page: {}/{} (total positions: {})",
                page, positionPage.getTotalPages(), positionPage.getTotalElements());
        return positionPage;
    }

    @Transactional
    @CacheEvict(cacheNames = "positionCache", key = "#id")
    public void updateName(Long id, @NonNull String name) {
        if (id == null) {

        }

        if (positionExists(name)) {
            throw new PositionExistsException("Position with name '" + name + "' already exists. Try again:");
        }

        var position = getPosition(id);
        position.setName(name);
        save(position);

        log.info("Position's name '{}' was updated", name);
    }

    @Transactional
    @CacheEvict(cacheNames = "positionCache", key = "#id")
    public void updateDescription(@NonNull Long id, @NonNull String description) {
        var position = getPosition(id);
        position.setDescription(description);
        save(position);

        log.info("Position's description '{}' was updated", description);
    }

    @Transactional
    @CacheEvict(cacheNames = "positionCache", key = "#id")
    public void delete(@NonNull Long id) {
        var position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException("Position with id #" + id + " not found"));

        positionRepository.delete(position);

        log.info("Position {} was deleted", position.getName());
    }

    public boolean positionExists(@NonNull String name) {
        return positionRepository.existsByNameIgnoreCase(name);
    }

    @Transactional
    @CacheEvict(cacheNames = "positionCache", key = "#position.id")
    public void save(@NonNull DbPosition position) {
        positionRepository.save(position);
    }

    // todo добавить возможность добавления описания для позиции
}
