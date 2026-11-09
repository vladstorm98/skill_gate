package com.skill_gate.repository;

import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbCheckResult;
import com.skill_gate.entity.DbUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckResultRepository extends JpaRepository<DbCheckResult, Long> {

    List<DbCheckResult> findByUserOrderByCreatedAtDesc(DbUser user);

    List<DbCheckResult> findByCheck(DbCheck check);

    List<DbCheckResult> findByUserAndCheck(DbUser user, DbCheck check);
}
