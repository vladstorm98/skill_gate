package com.skill_gate.repository;

import com.skill_gate.entity.DbPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<DbPosition, Long> {

    Optional<DbPosition> findByName(String name);

    boolean existsByNameIgnoreCase(String name);
}
