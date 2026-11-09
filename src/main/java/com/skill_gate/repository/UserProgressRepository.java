package com.skill_gate.repository;

import com.skill_gate.entity.DbLesson;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.DbUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<DbUserProgress, Long> {

    Optional<DbUserProgress> findByUserAndLesson(DbUser user, DbLesson lesson);

    Long countByUserAndUser_PositionAndIsCompletedTrue(DbUser user, DbPosition position);
}
