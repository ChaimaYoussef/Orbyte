package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.config.JwtUtil;
import com.ecommerce.backend_orbyte.dto.request.LoginRequest;
import com.ecommerce.backend_orbyte.dto.response.LoginResponse;
import com.ecommerce.backend_orbyte.dto.request.RefreshRequest;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Compte désactivé");
        }

        return LoginResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user))
                .refreshToken(jwtUtil.generateRefreshToken(user))
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .build();
    }

    @Override
    public LoginResponse refresh(RefreshRequest request) {
        if (!jwtUtil.isValid(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token invalide ou expiré");
        }

        String email = jwtUtil.extractEmail(request.getRefreshToken());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return LoginResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user))
                .refreshToken(request.getRefreshToken())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .department(user.getDepartment())
                .build();
    }

    @Override
    public void logout(String token) {
    }
}
