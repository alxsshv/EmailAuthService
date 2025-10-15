package com.alxsshv.service;

import com.alxsshv.entity.AuthPair;

public interface CodeDistributionService {

    void sendCode(AuthPair authPair);

}
