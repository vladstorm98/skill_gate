package com.skill_gate.handler.admin.employee;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.skill_gate.config.BotConstants.Employee;
import com.skill_gate.dto.UserDto;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.handler.admin.AdminButton;

public final class AdminEmployeeButton extends AdminButton {

    private AdminEmployeeButton() {}

    public static InlineKeyboardButton employees() {
        return back(Employee.PAGE_DEFAULT);
    }

    public static InlineKeyboardButton filters() {
        return button("🔎 Filters", Employee.FILTERS);
    }

    public static InlineKeyboardButton resetFilters() {
        return button("♻️ Reset", Employee.FILTERS_RESET);
    }

    public static InlineKeyboardButton applyFilters() {
        return button("✅ Apply", Employee.FILTERS_APPLY);
    }

    public static InlineKeyboardButton filterStatus(String label) {
        return button("Status: " + label, Employee.FILTER_STATUS);
    }

    public static InlineKeyboardButton filterRole(String label) {
        return button("Role: " + label, Employee.FILTER_ROLE);
    }

    public static InlineKeyboardButton filterPosition(String label) {
        return button("Position: " + label, Employee.FILTER_POSITION);
    }

    public static InlineKeyboardButton setFilterStatus(UserStatus status) {
        return button(status.name(), Employee.SET_FILTER_STATUS + status.name());
    }

    public static InlineKeyboardButton setFilterStatusAny() {
        return any(Employee.SET_FILTER_STATUS + "ANY");
    }

    public static InlineKeyboardButton setFilterRole(UserRole role) {
        return button(role.name(), Employee.SET_FILTER_ROLE + role.name());
    }

    public static InlineKeyboardButton setFilterRoleAny() {
        return any(Employee.SET_FILTER_ROLE + "ANY");
    }

    public static InlineKeyboardButton setFilterPosition(Long id, String name) {
        return button(name, Employee.SET_FILTER_POSITION + id);
    }

    public static InlineKeyboardButton setFilterPositionAny() {
        return any(Employee.SET_FILTER_POSITION + "ANY");
    }

    public static InlineKeyboardButton view(UserDto user) {
        var title = user.fullName() != null ? user.fullName() : "—";
        return button(title, Employee.VIEW + user.id());
    }

    public static InlineKeyboardButton editRole(Long userId) {
        return button("✏️ Role", Employee.EDIT_ROLE + userId);
    }

    public static InlineKeyboardButton editStatus(Long userId) {
        return button("✏️ Status", Employee.EDIT_STATUS + userId);
    }

    public static InlineKeyboardButton editPosition(Long userId) {
        return button("✏️ Position", Employee.EDIT_POSITION + userId);
    }

    public static InlineKeyboardButton setRole(Long userId, UserRole role) {
        return button(role.name(), Employee.SET_ROLE + userId + ":" + role.name());
    }

    public static InlineKeyboardButton setStatus(Long userId, UserStatus status) {
        return button(status.name(), Employee.SET_STATUS + userId + ":" + status.name());
    }

    public static InlineKeyboardButton setPosition(Long userId, Long positionId, String name) {
        return button(name, Employee.SET_POSITION + userId + ":" + positionId);
    }

    public static InlineKeyboardButton backToFilters() {
        return back(Employee.FILTERS);
    }

    public static InlineKeyboardButton backToProfile(Long userId) {
        return back(Employee.VIEW + userId);
    }
}
