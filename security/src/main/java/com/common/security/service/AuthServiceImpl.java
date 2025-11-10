package com.common.security.service;

import com.common.security.dto.UserCredentialDto;
import com.common.security.entity.Role;
import com.common.security.entity.UserCredential;
import com.common.security.exception.UserAlreadyExistsException;
import com.common.security.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserCredential createUser(UserCredentialDto userCredentialDto) {
        if (userCredentialRepository.findByEmail(userCredentialDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists with this email");
        }
        UserCredential user = UserCredential.builder()
                .email(userCredentialDto.getEmail())
                .password(userCredentialDto.getPassword())
                .name(userCredentialDto.getName())
                .role(Role.USER)
                .build();
        UserCredential savedUser = userCredentialRepository.save(user);
        return savedUser;


    }
}
