package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.LoginRequest;
import com.ecommerce.backend_orbyte.dto.response.LoginResponse;
import com.ecommerce.backend_orbyte.dto.request.RefreshRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse refresh(RefreshRequest request);
    void logout(String token);
}
