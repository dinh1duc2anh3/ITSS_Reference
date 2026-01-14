package com.darian.ecommerce.auth;

import com.darian.ecommerce.auth.dto.LoginDTO;
import com.darian.ecommerce.auth.dto.UserDTO;
import com.darian.ecommerce.auth.entity.User;
import com.darian.ecommerce.auth.exception.UserNotFoundException;
import com.darian.ecommerce.auth.mapper.UserMapper;
import com.darian.ecommerce.shared.exception.ErrorCode;
import com.darian.ecommerce.shared.exception.BaseException;
import com.darian.ecommerce.shared.constants.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO register(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BaseException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BaseException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + loginDTO.getUsername()));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_CREDENTIALS);
        }

        log.info("User logged in successfully: {}", user.getUsername());
        return userMapper.toDTO(user);
    }

    @Override
    public Boolean existedByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existedByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format(ErrorMessages.VALIDATION_FAILED, "User not found")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
