package com.alxsshv.security;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.Status;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class AccountDetails implements UserDetails {

    private final Account account;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return account.getAuthorities();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return account.getStatus().equals(Status.ENABLED);
    }
}
