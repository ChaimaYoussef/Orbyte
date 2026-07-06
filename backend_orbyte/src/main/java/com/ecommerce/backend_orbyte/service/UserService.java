package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.dto.request.AcceptInviteRequest;
import com.ecommerce.backend_orbyte.dto.request.InviteRequest;
import com.ecommerce.backend_orbyte.dto.request.UserRequest;
import com.ecommerce.backend_orbyte.dto.response.InviteResponse;
import com.ecommerce.backend_orbyte.dto.response.LoginResponse;
import com.ecommerce.backend_orbyte.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(UUID id);
    UserResponse create(UserRequest request);
    UserResponse update(UUID id, UserRequest request);
    void delete(UUID id);
    InviteResponse inviteUser(InviteRequest request);
    LoginResponse acceptInvite(AcceptInviteRequest request);
}
