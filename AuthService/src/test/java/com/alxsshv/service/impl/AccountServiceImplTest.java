package com.alxsshv.service.impl;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.Authorities;
import com.alxsshv.entity.Status;
import com.alxsshv.exception.EntityNotFoundException;
import com.alxsshv.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;


        @Test
        @DisplayName("Test addAccount method when invoke method then return saved account")
        void testAddAccount_whenMethodArgumentIsValid_thenReturnSavedAccount() {
            Account testAccount = Account.builder()
                    .id(UUID.randomUUID())
                    .email("test@email.com")
                    .status(Status.ENABLED)
                    .authorities(Set.of(Authorities.READ_ONLY))
                    .build();
            when(accountRepository.save(testAccount)).thenReturn(testAccount);

            Account actualAccount = accountService.addAccount(testAccount);

            verify(accountRepository, times(1)).save(testAccount);
            Assertions.assertEquals(testAccount, actualAccount);
        }



    @Test
    @DisplayName("Test activateAccount method when invoke method then account's status changed to enabled")
    void testActivateAccount_whenInvokeMethod_thenAccountStatusChangedToEnabled() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.DISABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        Account actualAccount = accountService.activateAccount(testAccount.getId());

        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountRepository, times(1)).save(testAccount);
        Assertions.assertEquals(Status.ENABLED, actualAccount.getStatus());
    }

    @Test
    @DisplayName("Test getAllAccounts method when invoke method then return list of accounts")
    void testGetAllAccounts_whenInvokeMethod_thenReturnListOfAccounts() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        int expectedAccountsListSize = 1;
        when(accountRepository.findAll()).thenReturn(List.of(testAccount));


        List<Account> actualAccountList = accountService.getAllAccounts();

        verify(accountRepository, times(1)).findAll();
        Assertions.assertEquals(expectedAccountsListSize, actualAccountList.size());
    }

    @Test
    @DisplayName("Test getAccountById method when account is found then return account")
    void testGetAccountById_whenAccountIsFound_thenReturnAccount() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));


        Account actualAccount = accountService.getAccountById(testAccount.getId());

        verify(accountRepository, times(1)).findById(testAccount.getId());
        Assertions.assertEquals(testAccount, actualAccount);
    }

    @Test
    @DisplayName("Test getAccountById method when account is not found then throw EntityNotFoundException")
    void testGetAccountById_whenAccountNotFound_thenThrowException() {
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(accountId));

        verify(accountRepository, times(1)).findById(accountId);
    }


    @Test
    @DisplayName("Test getAccountByEmail method when account is found then return account")
    void testGetAccountByEmail_whenAccountIsFound_thenReturnAccount() {
        Account testAccount = Account.builder()
                .id(UUID.randomUUID())
                .email("test@email.com")
                .status(Status.ENABLED)
                .authorities(Set.of(Authorities.READ_ONLY))
                .build();
        when(accountRepository.findByEmail(testAccount.getEmail())).thenReturn(Optional.of(testAccount));


        Account actualAccount = accountService.getAccountByEmail(testAccount.getEmail());

        verify(accountRepository, times(1)).findByEmail(testAccount.getEmail());
        Assertions.assertEquals(testAccount, actualAccount);
    }

    @Test
    @DisplayName("Test getAccountByEmail method when account is not found then throw EntityNotFoundException")
    void testGetAccountByEmail_whenAccountNotFound_thenThrowException() {
        String email = "test@email.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());


        Assertions.assertThrows(EntityNotFoundException.class, () -> accountService.getAccountByEmail(email));

        verify(accountRepository, times(1)).findByEmail(email);
    }





}
