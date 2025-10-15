package com.alxsshv.service;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;

import java.util.Set;

public interface AuthPairService {

    AuthPair createAndSaveAuthPair(Account account);

    Set<AuthPair> getAllByEmail(String email);

    void deleteAllPairsForAccount(Account account);

}
