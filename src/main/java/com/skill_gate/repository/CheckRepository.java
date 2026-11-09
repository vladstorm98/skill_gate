package com.skill_gate.repository;

import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbLesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckRepository extends JpaRepository<DbCheck, Long> {

    List<DbCheck> findByLesson(DbLesson lesson);

    Optional<DbCheck> findByLessonAndVersion(DbLesson lesson, Long version);

    Page<DbCheck> findByLesson(DbLesson lesson, Pageable pageable);

    @Query("SELECT c FROM DbCheck c JOIN FETCH c.lesson WHERE c.id = :id")
    Optional<DbCheck> findByIdWithLesson(@Param("id") Long id);

    @Query("SELECT MAX(c.version) FROM DbCheck c WHERE c.lesson = :lesson")
    Optional<Long> findMaxVersionByLesson(@Param("lesson") DbLesson lesson);
}
