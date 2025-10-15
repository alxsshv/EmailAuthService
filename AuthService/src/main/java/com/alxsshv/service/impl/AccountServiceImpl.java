package com.alxsshv.service.impl;

import com.alxsshv.entity.Status;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.entity.Account;
import com.alxsshv.repository.AccountRepository;
import com.alxsshv.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Account addAccount(Account account) {
       return accountRepository.save(account);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Account activateAccount(UUID accountId) {
        Account account = getAccountById(accountId);
        account.setStatus(Status.ENABLED);
        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccountById(UUID userId) {
        Optional<Account> userOpt = accountRepository.findById(userId);
        return userOpt.orElseThrow(
                () -> new EntityNotFoundException(String.format("Пользователь с id = %s не найден", userId)));
    }

    @Override
    public Account getAccountByEmail(String email) {
        Optional<Account> userOpt = accountRepository.findByEmail(email);
        return userOpt.orElseThrow(
                () -> new EntityNotFoundException(String.format("Пользователь с email = %s не найден", email)));
    }


}
