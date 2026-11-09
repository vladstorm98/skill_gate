package com.skill_gate.handler.admin.employee;

import com.skill_gate.cache.EmployeeDraftService;
import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.exception.UserNotFoundException;
import com.skill_gate.service.PositionService;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.skill_gate.config.BotConstants.PAGE_SIZE;

@Component
@RequiredArgsConstructor
public class AdminEmployeeHandler {

    private final UserService userService;
    private final PositionService positionService;
    private final EmployeeDraftService employeeDraftService;
    private final AdminEmployeeSender employeeSender;

    public void showEmployees(DbUser admin, Integer page) {
        if (page == null || page < 1) {
            page = 1;
        }

        var filter = employeeDraftService.getFilter(admin);

        try {
            var userPage = userService.getUsersPage(filter, page, PAGE_SIZE);
            employeeSender.sendEmployees(admin, filter, userPage, page);
        } catch (UserNotFoundException e) {
            employeeSender.sendEmpty(admin);
            employeeSender.sendFilters(admin, filter);
        }
    }

    public void showFilters(DbUser admin) {
        employeeSender.sendFilters(admin, employeeDraftService.getFilter(admin));
    }

    public void showStatusFilter(DbUser admin) {
        employeeSender.sendStatusFilter(admin);
    }

    public void showRoleFilter(DbUser admin) {
        employeeSender.sendRoleFilter(admin);
    }

    public void showPositionFilter(DbUser admin) {
        employeeSender.sendPositionFilter(admin);
    }

    public void setStatusFilter(DbUser admin, String value) {
        var filter = employeeDraftService.getFilter(admin);
        filter = "ANY".equals(value)
                ? filter.withStatus(null)
                : filter.withStatus(UserStatus.valueOf(value));
        employeeDraftService.saveFilter(admin, filter);
        employeeSender.sendFilters(admin, filter);
    }

    public void setRoleFilter(DbUser admin, String value) {
        var filter = employeeDraftService.getFilter(admin);
        filter = "ANY".equals(value)
                ? filter.withRole(null)
                : filter.withRole(UserRole.valueOf(value));
        employeeDraftService.saveFilter(admin, filter);
        employeeSender.sendFilters(admin, filter);
    }

    public void setPositionFilter(DbUser admin, String value) {
        var filter = employeeDraftService.getFilter(admin);
        filter = "ANY".equals(value)
                ? filter.withPositionId(null)
                : filter.withPositionId(Long.valueOf(value));
        employeeDraftService.saveFilter(admin, filter);
        employeeSender.sendFilters(admin, filter);
    }

    public void applyFilters(DbUser admin) {
        showEmployees(admin, 1);
    }

    public void resetFilters(DbUser admin) {
        employeeDraftService.removeFilter(admin);
        showEmployees(admin, 1);
    }

    public void handleViewEmployee(DbUser admin, Long userId) {
        employeeDraftService.saveUserId(admin, userId);
        employeeSender.sendViewEmployee(admin, userService.getUserDtoById(userId));
    }

    public void showEditRole(DbUser admin, Long userId) {
        employeeDraftService.saveUserId(admin, userId);
        employeeSender.sendChooseRole(admin, userId);
    }

    public void showEditStatus(DbUser admin, Long userId) {
        employeeDraftService.saveUserId(admin, userId);
        employeeSender.sendChooseStatus(admin, userId);
    }

    public void showEditPosition(DbUser admin, Long userId) {
        employeeDraftService.saveUserId(admin, userId);
        employeeSender.sendChoosePosition(admin, userId);
    }

    public void handleSetRole(DbUser admin, Long userId, UserRole role) {
        userService.changeUserRoleById(userId, role);
        employeeSender.sendFieldUpdated(admin, "Role");
        handleViewEmployee(admin, userId);
    }

    public void handleSetStatus(DbUser admin, Long userId, UserStatus status) {
        userService.changeUserStatusById(userId, status);
        employeeSender.sendFieldUpdated(admin, "Status");
        handleViewEmployee(admin, userId);
    }

    public void handleSetPosition(DbUser admin, Long userId, Long positionId) {
        var position = positionService.getPosition(positionId);
        userService.changeUserPositionById(userId, position);
        employeeSender.sendFieldUpdated(admin, "Position");
        handleViewEmployee(admin, userId);
    }
}
