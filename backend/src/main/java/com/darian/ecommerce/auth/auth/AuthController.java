package com.darian.ecommerce.auth;

import com.darian.ecommerce.auth.dto.LoginDTO;
import com.darian.ecommerce.auth.dto.UserDTO;
import com.darian.ecommerce.shared.constants.ApiEndpoints;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiEndpoints.AUTH)
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(ApiEndpoints.AUTH_LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserDTO user = userService.login(loginDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping(ApiEndpoints.AUTH_REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.register(userDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", registeredUser);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}