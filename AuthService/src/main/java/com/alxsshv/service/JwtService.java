package com.alxsshv.service;

import com.alxsshv.entity.Account;

public interface JwtService {

    String generateAccessToken(Account account);

}
