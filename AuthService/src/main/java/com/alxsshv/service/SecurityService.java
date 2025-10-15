package com.alxsshv.service;

import com.alxsshv.dto.AuthRequest;
import com.alxsshv.dto.AuthResponse;

public interface SecurityService {


    void getAuthorizationCode(String email);



    AuthResponse authenticate(AuthRequest authRequest);

}
