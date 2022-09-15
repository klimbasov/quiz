package com.innowise.quiz.config.security.role;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    LEAD,
    USER;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
