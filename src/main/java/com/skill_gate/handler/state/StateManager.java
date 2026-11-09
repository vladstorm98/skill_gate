package com.skill_gate.handler.state;

import com.skill_gate.entity.DbUser;
import com.skill_gate.entity.UserState;
import com.skill_gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StateManager {

    private final UserService userService;

    public void change(DbUser user, UserState state) {
        user.setCurrentState(state);
        userService.save(user);
    }
}
