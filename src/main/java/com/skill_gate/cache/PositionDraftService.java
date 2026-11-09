package com.skill_gate.cache;

import com.skill_gate.entity.DbUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PositionDraftService {

    private static final String PREFIX = "position-draft:";

    private final DraftStore draftStore;

    public Long get(DbUser admin) {
        return draftStore.get(PREFIX, admin, Long.class);
    }

    public void save(DbUser admin, Long positionId) {
        draftStore.save(PREFIX, admin, positionId);
    }

    public void remove(DbUser admin) {
        draftStore.remove(PREFIX, admin);
    }
}
