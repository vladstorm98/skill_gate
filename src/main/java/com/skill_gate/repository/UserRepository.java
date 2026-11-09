package com.skill_gate.repository;

import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<DbUser, Long> {

    Optional<DbUser> findByTelegramId(Long telegramId);

    List<DbUser> findByRole(UserRole role);

    List<DbUser> findByStatus(UserStatus status);

    Page<DbUser> findByStatus(UserStatus status, Pageable pageable);

    @NotNull Page<DbUser> findAll(@NotNull Pageable pageable);

    // Для админки — поиск по части ФИО
    List<DbUser> findByFullNameContainingIgnoreCase(String fullName);

    boolean existsByTelegramId(Long telegramId);

    @Query("""
    SELECT u FROM DbUser u
    LEFT JOIN u.position p
    WHERE (:status IS NULL OR u.status = :status)
      AND (:role IS NULL OR u.role = :role)
      AND (:positionId IS NULL OR p.id = :positionId)
    ORDER BY u.id
    """)
    Page<DbUser> findFiltered(
            @Param("status") UserStatus status,
            @Param("role") UserRole role,
            @Param("positionId") Long positionId,
            Pageable pageable
    );
}
