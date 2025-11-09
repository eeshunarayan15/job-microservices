package com.common.security.controller;

import com.common.security.dto.JwtAuthResponse;
import com.common.security.dto.LoginRequest;
import com.common.security.dto.UserCredentialDto;
import com.common.security.entity.UserCredential;
import com.common.security.repository.UserCredentialRepository;
import com.common.security.response.ApiResponse;
import com.common.security.service.AuthService;
import com.common.security.springsecurity.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class UserController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;
    private  final UserCredentialRepository userCredentialRepository;

    @PostMapping("/api/v1/register")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> createUser(@Valid @RequestBody UserCredentialDto userCredentialDto) {
        String encode = passwordEncoder.encode(userCredentialDto.getPassword());
        userCredentialDto.setPassword(encode);
        UserCredential savedUser = authService.createUser(userCredentialDto);

        String token = jwtService.generateToken(savedUser);
        JwtAuthResponse authResponse = new JwtAuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
        ApiResponse<JwtAuthResponse> response =
                new ApiResponse<>("success", "User registered successfully", authResponse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }



    @PostMapping("/api/v1/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
    if(authenticate.isAuthenticated()) {
        // 3️⃣ Load user from DB (to get full details)
        UserCredential savedUser = userCredentialRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(savedUser);
        // 5️⃣ Build structured response
        JwtAuthResponse authResponse = new JwtAuthResponse(
                token,
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
        ApiResponse<JwtAuthResponse> response =
                new ApiResponse<>("success", "Login successful", authResponse);

        return ResponseEntity.ok(response);


    }
        ApiResponse<JwtAuthResponse> errorResponse =
                new ApiResponse<>("error", "Invalid email or password", null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
}
