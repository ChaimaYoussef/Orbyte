package com.ecommerce.backend_orbyte.service;

import com.ecommerce.backend_orbyte.common.exception.ResourceNotFoundException;
import com.ecommerce.backend_orbyte.config.JwtUtil;
import com.ecommerce.backend_orbyte.dto.request.AcceptInviteRequest;
import com.ecommerce.backend_orbyte.dto.request.InviteRequest;
import com.ecommerce.backend_orbyte.dto.request.UserRequest;
import com.ecommerce.backend_orbyte.dto.response.InviteResponse;
import com.ecommerce.backend_orbyte.dto.response.LoginResponse;
import com.ecommerce.backend_orbyte.dto.response.UserResponse;
import com.ecommerce.backend_orbyte.entity.InvitationToken;
import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.entity.UserRole;
import com.ecommerce.backend_orbyte.entity.UserStatus;
import com.ecommerce.backend_orbyte.repository.InvitationTokenRepository;
import com.ecommerce.backend_orbyte.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final InvitationTokenRepository invitationTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initMockUsers() {
        if (userRepository.count() == 0) {
            String[] names = {
                    "Sarah Connor", "John Doe", "Alice Smith", "Bob Johnson",
                    "Charlie Brown", "Diana Prince", "Bruce Wayne", "Clark Kent"
            };
            String[] emails = {
                    "sarah@orbyte.ai", "john@orbyte.ai", "alice@orbyte.ai", "bob@orbyte.ai",
                    "charlie@orbyte.ai", "diana@orbyte.ai", "bruce@orbyte.ai", "clark@orbyte.ai"
            };
            UserRole[] roles = {
                    UserRole.ADMIN, UserRole.USER, UserRole.CURATOR, UserRole.USER,
                    UserRole.CURATOR, UserRole.USER, UserRole.ADMIN, UserRole.USER
            };
            UserStatus[] statuses = {
                    UserStatus.ACTIVE, UserStatus.ACTIVE, UserStatus.PENDING, UserStatus.INACTIVE,
                    UserStatus.ACTIVE, UserStatus.PENDING, UserStatus.ACTIVE, UserStatus.ACTIVE
            };
            String[] depts = {
                    "IT", "Marketing", "HR", "Sales",
                    "Design", "Product", "Executive", "Engineering"
            };

            for (int i = 0; i < names.length; i++) {
                userRepository.save(User.builder()
                        .fullName(names[i])
                        .email(emails[i])
                        .password(passwordEncoder.encode("orbyte123"))
                        .role(roles[i])
                        .status(statuses[i])
                        .department(depts[i])
                        .lastActive(LocalDateTime.now().minusHours(i * 3))
                        .messagesCount(12 + i * 45)
                        .active(statuses[i] == UserStatus.ACTIVE)
                        .build());
            }
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CURATOR')")
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'CURATOR') or #id == authentication.principal.id")
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(UserRequest request) {
        User user = User.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "orbyte123"))
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .status(UserStatus.valueOf(request.getStatus().toUpperCase()))
                .department(request.getDepartment())
                .lastActive(LocalDateTime.now())
                .active(true)
                .build();
        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));
        user.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));
        user.setDepartment(request.getDepartment());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        return toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public InviteResponse inviteUser(InviteRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Cet utilisateur existe déjà");
        }
        
        invitationTokenRepository.findByEmail(request.getEmail()).ifPresent(token -> {
            if (!token.isUsed() && token.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Une invitation active existe déjà pour cet email");
            }
        });

        String rawToken = UUID.randomUUID().toString();
        
        InvitationToken invitation = InvitationToken.builder()
                .email(request.getEmail())
                .token(rawToken)
                .role(request.getRole())
                .expiresAt(LocalDateTime.now().plusHours(48))
                .used(false)
                .build();
                
        invitationTokenRepository.save(invitation);
        
        log.info("Invitation envoyée à {} — token : {}", request.getEmail(), rawToken);
        
        return InviteResponse.builder()
                .email(invitation.getEmail())
                .token(invitation.getToken())
                .expiresAt(invitation.getExpiresAt())
                .build();
    }

    @Override
    public LoginResponse acceptInvite(AcceptInviteRequest request) {
        InvitationToken token = invitationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token invalide ou introuvable"));
                
        if (token.isUsed()) {
            throw new RuntimeException("Cette invitation a déjà été utilisée");
        }
        
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cette invitation a expiré");
        }
        
        User newUser = User.builder()
                .fullName(request.getFullName())
                .email(token.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(token.getRole())
                .status(UserStatus.ACTIVE)
                .active(true)
                .build();
                
        userRepository.save(newUser);
        
        token.setUsed(true);
        invitationTokenRepository.save(token);
        
        String accessToken = jwtUtil.generateAccessToken(newUser);
        String refreshToken = jwtUtil.generateRefreshToken(newUser);
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .lastActive(user.getLastActive())
                .messagesCount(user.getMessagesCount())
                .department(user.getDepartment())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
