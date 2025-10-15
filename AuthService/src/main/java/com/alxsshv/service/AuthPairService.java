package com.alxsshv.service;

import com.alxsshv.entity.Account;
import com.alxsshv.entity.AuthPair;

public interface AuthPairService {

    AuthPair createAndSaveAuthPair(Account account);

    AuthPair getByEmail(String email);

    void deleteAllPairsForAccount(Account account);

}
