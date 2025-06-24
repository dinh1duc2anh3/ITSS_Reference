package com.darian.ecommerce.auth;

import com.darian.ecommerce.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Integer userId);

    Boolean existsByUsername(String username);

//    List<User> username(String username);

    Boolean existsByEmail(String email);
}
