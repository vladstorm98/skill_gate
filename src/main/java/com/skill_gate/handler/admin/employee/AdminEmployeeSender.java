package com.skill_gate.handler.admin.employee;

import com.skill_gate.dto.EmployeeFilterDto;
import com.skill_gate.dto.UserDto;
import com.skill_gate.entity.DbUser;
import com.skill_gate.handler.telegram.TelegramSender;
import com.skill_gate.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminEmployeeSender {

    private final TelegramSender sender;
    private final AdminEmployeeKeyboard employeeKeyboard;
    private final PositionService positionService;

    public void sendEmployees(DbUser admin, EmployeeFilterDto filter, Page<UserDto> page, int currentPage) {
        var text = """
                👥 Employees (Page %d/%d)
                
                %s
                """.formatted(currentPage, page.getTotalPages(), filterLine(filter));

        sender.send(admin, text, employeeKeyboard.prepareEmployeesKeyboard(
                page.getContent(), currentPage, page.getTotalPages()
        ));
    }

    public void sendFilters(DbUser admin, EmployeeFilterDto filter) {
        sender.send(
                admin,
                "🔎 Employee filters\n\n" + filterLine(filter),
                employeeKeyboard.prepareFiltersKeyboard(
                        label(filter.status()),
                        label(filter.role()),
                        positionLabel(filter.positionId())
                )
        );
    }

    public void sendStatusFilter(DbUser admin) {
        sender.send(admin, "Select status filter:", employeeKeyboard.prepareStatusFilterKeyboard());
    }

    public void sendRoleFilter(DbUser admin) {
        sender.send(admin, "Select role filter:", employeeKeyboard.prepareRoleFilterKeyboard());
    }

    public void sendPositionFilter(DbUser admin) {
        sender.send(admin, "Select position filter:", employeeKeyboard.preparePositionFilterKeyboard());
    }

    public void sendViewEmployee(DbUser admin, UserDto user) {
        var text = """
                👤 Employee #%d
                
                Name: %s
                Phone: %s
                Role: %s
                Status: %s
                Position: %s
                """.formatted(
                user.id(),
                user.fullName() != null ? user.fullName() : "—",
                user.phone() != null ? user.phone() : "—",
                user.role(),
                user.status(),
                user.positionName() != null ? user.positionName() : "—"
        );

        sender.send(admin, text, employeeKeyboard.prepareViewEmployeeKeyboard(user.id()));
    }

    public void sendChooseRole(DbUser admin, Long userId) {
        sender.send(admin, "Select new role:", employeeKeyboard.prepareChooseRoleKeyboard(userId));
    }

    public void sendChooseStatus(DbUser admin, Long userId) {
        sender.send(admin, "Select new status:", employeeKeyboard.prepareChooseStatusKeyboard(userId));
    }

    public void sendChoosePosition(DbUser admin, Long userId) {
        sender.send(admin, "Select new position:", employeeKeyboard.prepareChoosePositionKeyboard(userId));
    }

    public void sendFieldUpdated(DbUser admin, String fieldName) {
        sender.send(admin, "%s updated.".formatted(fieldName));
    }

    public void sendEmpty(DbUser admin) {
        sender.send(admin, "No employees found for current filters.");
    }

    private String filterLine(EmployeeFilterDto filter) {
        return "Status: %s | Role: %s | Position: %s".formatted(
                label(filter.status()),
                label(filter.role()),
                positionLabel(filter.positionId())
        );
    }

    private String label(Enum<?> value) {
        return value == null ? "Any" : value.name();
    }

    private String positionLabel(Long positionId) {
        if (positionId == null) {
            return "Any";
        }
        return positionService.getPosition(positionId).getName();
    }
}
