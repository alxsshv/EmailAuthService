package com.alxsshv.service;

import com.alxsshv.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    Account addAccount(@NotNull Account account);

    Account getAccountById(@NotNull UUID userId);

    Account getAccountByEmail(@NotBlank String email);

    Account activateAccount(@NotNull UUID accountId);

    List<Account> getAllAccounts();
}
