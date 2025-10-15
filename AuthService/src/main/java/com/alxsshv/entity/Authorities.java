package com.alxsshv.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Authorities implements GrantedAuthority {

    READ_ONLY,
    MODIFY_BASE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
