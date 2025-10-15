package com.alxsshv.service;

import com.alxsshv.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account addAccount(Account account);

    Account getAccountById(UUID userId);

    Account getAccountByEmail(String email);

    Account activateAccount(UUID accountId);

    List<Account> getAllAccounts();
}
