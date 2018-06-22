package com.slesh.gallery.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slesh.gallery.auth.AuthRequest;
import com.slesh.gallery.persistence.model.ApplicationUser;
import com.slesh.gallery.persistence.model.Role;
import com.slesh.gallery.persistence.repository.ApplicationUserRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public AuthController(ApplicationUserRepository applicationUserRepository,
                          PasswordEncoder passwordEncoder,
                          ObjectMapper objectMapper) {
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApplicationUser> signUp(@RequestBody AuthRequest authRequest) {
        ApplicationUser user = new ApplicationUser();
        user.setUsername(authRequest.getUsername());
        boolean exists = applicationUserRepository.exists(
            Example.of(user, ExampleMatcher.matchingAll()));
        if (exists) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRoles(Collections.singletonList(Role.ROLE_USER));
        ApplicationUser entity = applicationUserRepository.save(user);
        return ResponseEntity.ok(entity);
    }
}
