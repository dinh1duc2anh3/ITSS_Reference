package com.darian.ecommerce.auth;

import com.darian.ecommerce.auth.dto.LoginDTO;
import com.darian.ecommerce.auth.dto.UserDTO;
import com.darian.ecommerce.auth.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDTO register(UserDTO userDTO);
    UserDTO login(LoginDTO loginDTO);
    Boolean existedByUsername(String username);
    Boolean existedByEmail(String email);
    User getUserById(Integer userId);
    UserDetails loadUserByUsername(String username);
}