package com.skill_gate.service;

import com.skill_gate.dto.EmployeeFilterDto;
import com.skill_gate.dto.UserDto;
import com.skill_gate.entity.DbPosition;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserState;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.exception.UserApprovalException;
import com.skill_gate.exception.UserBlockingException;
import com.skill_gate.exception.UserNotFoundException;
import com.skill_gate.mapper.UserMapper;
import com.skill_gate.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public DbUser getOrCreateUser(@NonNull Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> createDbUser(telegramId));
    }

    public DbUser getUser(@NonNull Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException("User with telegram id #" + telegramId + " not found"));
    }

    @Transactional
    public void approveUser(@NonNull Long telegramId) {
        var dbUser = getUser(telegramId);

        if (dbUser.getStatus() != UserStatus.NEW) {
            throw new UserApprovalException(
                    "Approval is not possible. User with telegram id #" + telegramId + " has status " + dbUser.getStatus() + ".");
        }

        dbUser.setCurrentLessonNumber(1L);
        dbUser.setOnboardingProgress(BigDecimal.ZERO);
        dbUser.setCurrentState(UserState.ONBOARDING_WAITING_VIDEO);
        dbUser.setStatus(UserStatus.ONBOARDING);
        save(dbUser);

        log.info("User with telegramId #{} has been approved", dbUser.getTelegramId());
    }

    @Transactional
    public void rejectUser(@NonNull Long telegramId) {
        var dbUser = getUser(telegramId);

        if (dbUser.getStatus() == UserStatus.BLOCKED) {
            throw new UserBlockingException("User with telegram id #" + telegramId + " has already been blocked.");
        }

        dbUser.setCurrentState(UserState.NONE);
        dbUser.setStatus(UserStatus.BLOCKED);
        save(dbUser);

        log.info("User with telegram id #{} has been blocked", telegramId);
    }

    @Transactional
    public void changeUserRole(@NonNull Long telegramId, UserRole newRole) {
        var dbUser = getUser(telegramId);

        dbUser.setRole(newRole);
        save(dbUser);

        log.info("Role for user with telegram Id #{} has been changed", telegramId);
    }

    @Transactional
    public void changeUserStatus(@NonNull Long telegramId, UserStatus newStatus) {
        var dbUser = getUser(telegramId);

        dbUser.setStatus(newStatus);
        save(dbUser);

        log.info("Status for user with id #{} has been updated", telegramId);
    }

    @Transactional
    public void assignPosition(DbUser user, DbPosition position) {
        user.setPosition(position);
        save(user);

        log.info("Position for user with id #{} has been assigned", user.getId());
    }

    public List<DbUser> getAdmins() {
        var admins = userRepository.findByRole(UserRole.ADMIN);

        if (admins.isEmpty()) {
            throw new UserNotFoundException("Admins not found");
        }

        log.info("Admins was retrieved");
        return admins;
    }

    @Transactional
    public void save(DbUser user) {
        userRepository.save(user);
    }

    private DbUser createDbUser(Long telegramId) {
        return new DbUser(telegramId, UserRole.EMPLOYEE, UserStatus.NEW, UserState.REGISTRATION_WAITING_FULL_NAME);
    }

//    public Page<UserDto> getUsersPage(int page, int size) {
//        var pageable = PageRequest.of(page - 1, size);
//        Page<UserDto> userDtoPage = userRepository.findAll(pageable)
//                .map(userMapper::dbToDto);
//
//        if (userDtoPage.isEmpty()) {
//            throw new UserNotFoundException("Users not found");
//        }
//
//        log.info("Retrieved users page: {}/{} (total users: {})",
//                page, userDtoPage.getTotalPages(), userDtoPage.getTotalElements());
//        return userDtoPage;
//    } старый employees

    public Page<UserDto> getUsersPage(EmployeeFilterDto filter, int page, int size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = userRepository.findFiltered(
                        filter.status(),
                        filter.role(),
                        filter.positionId(),
                        pageable
                )
                .map(userMapper::dbToDto);

        if (result.isEmpty()) {
            throw new UserNotFoundException("Users not found");
        }
        return result;
    }

    public Page<UserDto> getUsersPage(int page, int size) {
        return getUsersPage(EmployeeFilterDto.empty(), page, size);
    }

    public UserDto getUserDtoById(@NonNull Long id) {
        return userRepository.findById(id)
                .map(userMapper::dbToDto)
                .orElseThrow(() -> new UserNotFoundException("User with id #" + id + " not found"));
    }

    public DbUser getUserById(@NonNull Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id #" + id + " not found"));
    }

    @Transactional
    public void changeUserRoleById(@NonNull Long id, UserRole newRole) {
        var user = getUserById(id);
        user.setRole(newRole);
        save(user);
    }

    @Transactional
    public void changeUserStatusById(@NonNull Long id, UserStatus newStatus) {
        var user = getUserById(id);
        user.setStatus(newStatus);
        save(user);
    }

    @Transactional
    public void changeUserPositionById(@NonNull Long id, DbPosition position) {
        var user = getUserById(id);
        user.setPosition(position);
        save(user);

        log.info("Position for user #{} set to {}", id, position.getId());
    }
}
