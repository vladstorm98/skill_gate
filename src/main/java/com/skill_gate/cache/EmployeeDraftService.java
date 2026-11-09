package com.skill_gate.cache;

import com.skill_gate.dto.EmployeeFilterDto;
import com.skill_gate.entity.DbUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeDraftService {

    private static final String FILTER_PREFIX = "employee-filter:";
    private static final String USER_PREFIX = "employee-edit:";

    private final DraftStore draftStore;

    public void saveFilter(DbUser admin, EmployeeFilterDto filter) {
        draftStore.save(FILTER_PREFIX, admin, filter);
    }

    public EmployeeFilterDto getFilter(DbUser admin) {
        EmployeeFilterDto filter = draftStore.get(FILTER_PREFIX, admin, EmployeeFilterDto.class);
        return filter != null ? filter : EmployeeFilterDto.empty();
    }

    public void removeFilter(DbUser admin) {
        draftStore.remove(FILTER_PREFIX, admin);
    }

    public void saveUserId(DbUser admin, Long userId) {
        draftStore.save(USER_PREFIX, admin, String.valueOf(userId));
    }

    public Long getUserId(DbUser admin) {
        String value = draftStore.get(USER_PREFIX, admin, String.class);
        return value == null ? null : Long.valueOf(value);
    }

    public void removeUserId(DbUser admin) {
        draftStore.remove(USER_PREFIX, admin);
    }
}
