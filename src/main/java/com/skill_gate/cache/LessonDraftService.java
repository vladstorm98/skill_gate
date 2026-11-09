package com.skill_gate.cache;

import com.skill_gate.dto.LessonDraftDto;
import com.skill_gate.entity.DbUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class LessonDraftService {

    private static final String PREFIX = "lesson-draft:";

    private final DraftStore draftStore;

    public LessonDraftDto get(DbUser admin) {
        var draft = draftStore.get(PREFIX, admin, LessonDraftDto.class);
        return draft != null ? draft : LessonDraftDto.empty();
    }

    public void save(DbUser admin, LessonDraftDto draft) {
        draftStore.save(PREFIX, admin, draft);
    }

    public void togglePosition(DbUser admin, Long positionId) {
        var draft = get(admin);
        var positionIds = new HashSet<>(draft.positionIds());

        if (!positionIds.remove(positionId)) {
            positionIds.add(positionId);
        }

        save(admin, new LessonDraftDto(draft.id(), positionIds));
    }

    public void remove(DbUser admin) {
        draftStore.remove(PREFIX, admin);
    }
}
