package com.common.security.config;

import com.common.security.entity.UserCredential;
import com.common.security.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);

        Optional<UserCredential> credential = userCredentialRepository.findByEmail(username);

        if (credential.isEmpty()) {
            log.error("User not found with email: {}", username);
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        UserCredential user = credential.get();
        log.debug("User found: {} with role: {}", user.getEmail(), user.getRole());
        log.debug("User authorities: {}", user.getAuthorities());

        // Return UserCredential directly since it implements UserDetails
        // This ensures authorities are properly included
        return user;
    }
}