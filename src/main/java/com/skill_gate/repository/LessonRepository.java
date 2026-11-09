package com.skill_gate.repository;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<DbLesson, Long> {

    Optional<DbLesson> findByNumber(Long id);

    @Query("""
    SELECT l FROM DbLesson l
    JOIN l.positions p
    WHERE p = :position
      AND l.isActive = true
      AND l.number = :lessonNumber
    """)
    Optional<DbLesson> findByPositionAndIsActiveTrueAndNumber(
            @Param("position") DbPosition position,
            @Param("lessonNumber") Long lessonNumber);

    @Query("""
    SELECT l FROM DbLesson l
    JOIN l.positions p
    WHERE p = :position
    ORDER BY l.number ASC
    """)
    Page<DbLesson> findByPositionOrderByNumberAsc(
            @Param("position") DbPosition position,
            Pageable pageable);

    @Query("""
    SELECT COUNT(l) FROM DbLesson l
    JOIN l.positions p
    WHERE p = :position AND l.isActive = true
    """)
    Long countByPositionAndIsActiveTrue(@Param("position") DbPosition position);

    boolean existsByTitleIgnoreCase(String title);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    UPDATE lessons
    SET number = number + 1
    WHERE id IN (
        SELECT lesson_id FROM lesson_positions WHERE position_id = :positionId
    )
    AND number >= :fromNumber
    AND number < :toNumber
    AND id <> :excludeLessonId
    """, nativeQuery = true)
    void shiftUp(@Param("positionId") Long positionId,
                 @Param("fromNumber") Long fromNumber,
                 @Param("toNumber") Long toNumber,
                 @Param("excludeLessonId") Long excludeLessonId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    UPDATE lessons
    SET number = number - 1
    WHERE id IN (
        SELECT lesson_id FROM lesson_positions WHERE position_id = :positionId
    )
    AND number > :fromNumber
    AND number <= :toNumber
    AND id <> :excludeLessonId
    """, nativeQuery = true)
    void shiftDown(@Param("positionId") Long positionId,
                   @Param("fromNumber") Long fromNumber,
                   @Param("toNumber") Long toNumber,
                   @Param("excludeLessonId") Long excludeLessonId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
    UPDATE lessons
    SET number = number - 1
    WHERE id IN (
        SELECT lesson_id FROM lesson_positions WHERE position_id = :positionId
    )
    AND number > :fromNumber
    """, nativeQuery = true)
    void shiftDownAfterDelete(@Param("positionId") Long positionId,
                              @Param("fromNumber") Long fromNumber);
}
