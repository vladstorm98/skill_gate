package com.skill_gate.repository;

import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<DbQuestion, Long> {

    Page<DbQuestion> findByCheckOrderByNumberAsc(DbCheck check, Pageable pageable);
    List<DbQuestion> findByCheckOrderByNumberAsc(DbCheck check);

    @Query("SELECT MAX(q.number) FROM DbQuestion q WHERE q.check = :check")
    Optional<Long> findMaxNumberByCheck(@Param("check") DbCheck check);
}
