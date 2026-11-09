package com.skill_gate.handler.admin.employee;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.skill_gate.config.BotConstants.Employee;
import com.skill_gate.dto.UserDto;
import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;
import com.skill_gate.handler.admin.AdminKeyboard;
import com.skill_gate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.skill_gate.handler.admin.employee.AdminEmployeeButton.*;

@Component
@RequiredArgsConstructor
public class AdminEmployeeKeyboard extends AdminKeyboard {

    private final PositionService positionService;

    public InlineKeyboardMarkup prepareEmployeesKeyboard(List<UserDto> users, int page, int totalPages) {
        var markup = new InlineKeyboardMarkup();
        users.forEach(user -> markup.addRow(view(user)));
        addPagination(markup, page, totalPages, Employee.PAGE);
        markup.addRow(filters(), resetFilters());
        return markup;
    }

    public InlineKeyboardMarkup prepareFiltersKeyboard(String status, String role, String position) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(filterStatus(status));
        markup.addRow(filterRole(role));
        markup.addRow(filterPosition(position));
        markup.addRow(applyFilters(), resetFilters());
        markup.addRow(employees());
        return markup;
    }

    public InlineKeyboardMarkup prepareStatusFilterKeyboard() {
        var markup = new InlineKeyboardMarkup();
        for (var status : UserStatus.values()) {
            markup.addRow(setFilterStatus(status));
        }
        markup.addRow(setFilterStatusAny());
        markup.addRow(backToFilters());
        return markup;
    }

    public InlineKeyboardMarkup prepareRoleFilterKeyboard() {
        var markup = new InlineKeyboardMarkup();
        for (var role : UserRole.values()) {
            markup.addRow(setFilterRole(role));
        }
        markup.addRow(setFilterRoleAny());
        markup.addRow(backToFilters());
        return markup;
    }

    public InlineKeyboardMarkup preparePositionFilterKeyboard() {
        var markup = new InlineKeyboardMarkup();
        positionService.getAllPositions().forEach(p ->
                markup.addRow(setFilterPosition(p.getId(), p.getName())));
        markup.addRow(setFilterPositionAny(), backToFilters());
        return markup;
    }

    public InlineKeyboardMarkup prepareViewEmployeeKeyboard(Long userId) {
        var markup = new InlineKeyboardMarkup();
        markup.addRow(editRole(userId), editStatus(userId), editPosition(userId));
        return withMenuButton(markup, employees());
    }

    public InlineKeyboardMarkup prepareChooseRoleKeyboard(Long userId) {
        var markup = new InlineKeyboardMarkup();
        for (var role : UserRole.values()) {
            markup.addRow(setRole(userId, role));
        }
        markup.addRow(backToProfile(userId));
        return markup;
    }

    public InlineKeyboardMarkup prepareChooseStatusKeyboard(Long userId) {
        var markup = new InlineKeyboardMarkup();
        for (var status : UserStatus.values()) {
            markup.addRow(setStatus(userId, status));
        }
        markup.addRow(backToProfile(userId));
        return markup;
    }

    public InlineKeyboardMarkup prepareChoosePositionKeyboard(Long userId) {
        var markup = new InlineKeyboardMarkup();
        positionService.getAllPositions().forEach(p ->
                markup.addRow(setPosition(userId, p.getId(), p.getName())));
        markup.addRow(backToProfile(userId));
        return markup;
    }

    private void addPagination(InlineKeyboardMarkup markup, int page, int totalPages, String prefix) {
        for (var row : preparePaginationKeyboard(page, totalPages, prefix).inlineKeyboard()) {
            markup.addRow(row);
        }
    }
}
