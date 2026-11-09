package com.skill_gate.repository;

import com.skill_gate.entity.DbAnswerOption;
import com.skill_gate.entity.DbQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<DbAnswerOption, Long> {

    List<DbAnswerOption> findByQuestionOrderByIdAsc(DbQuestion question);
    void deleteByQuestion(DbQuestion question);
}
