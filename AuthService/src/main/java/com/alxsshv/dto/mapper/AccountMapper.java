package com.alxsshv.dto.mapper;

import com.alxsshv.dto.AccountDto;
import com.alxsshv.entity.Account;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountMapper {

    public AccountDto map(Account account) {
        AccountDto dto = new AccountDto();
        if (account.getId() != null) {
            dto.setId(account.getId().toString());
        }
        if (account.getEmail() != null) {
            dto.setEmail(account.getEmail());
        }
        dto.setStatus(account.getStatus().name());
        return dto;
    }

    public List<AccountDto> mapList(List<Account> accounts) {
        return accounts.stream().map(this::map).toList();
    }



}
