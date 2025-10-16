package com.alxsshv.service;

import com.alxsshv.entity.AuthPair;


public interface CodeSendingService {

    void sendCode(AuthPair authPair);

}
