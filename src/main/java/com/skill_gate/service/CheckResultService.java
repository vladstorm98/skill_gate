package com.skill_gate.service;

import com.skill_gate.entity.DbCheck;
import com.skill_gate.entity.DbCheckResult;
import com.skill_gate.entity.DbUser;
import com.skill_gate.repository.CheckResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CheckResultService {

    private final CheckResultRepository checkResultRepository;

    @Transactional
    public void saveCheckResult(DbUser user, DbCheck check, BigDecimal score, boolean passed, Long attemptNumber) {
        save(createCheckResult(user, check, score, passed, attemptNumber));
    }

    private DbCheckResult createCheckResult(DbUser user, DbCheck check, BigDecimal score,
                                                     boolean passed, Long attemptNumber) {
        return new DbCheckResult(user, check, score, passed, attemptNumber);
    }

    private void save(DbCheckResult checkResult) {
        checkResultRepository.save(checkResult);
    }
}
