package com.ecommerce.backend_orbyte.repository;

import com.ecommerce.backend_orbyte.entity.User;
import com.ecommerce.backend_orbyte.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByStatus(UserStatus status);

    List<User> findAllByDepartment(String department);
}
