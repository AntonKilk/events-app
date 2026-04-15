package com.eventsapp.auth;

import com.eventsapp.config.AdminProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class AdminAuthService {

    private final AdminProperties adminProperties;

    public AdminAuthService(AdminProperties adminProperties) {
        this.adminProperties = adminProperties;
    }

    public boolean check(String email, String password) {
        if (email == null || password == null) return false;
        boolean emailMatch = MessageDigest.isEqual(
            email.getBytes(StandardCharsets.UTF_8),
            adminProperties.email().getBytes(StandardCharsets.UTF_8)
        );
        boolean passwordMatch = MessageDigest.isEqual(
            password.getBytes(StandardCharsets.UTF_8),
            adminProperties.password().getBytes(StandardCharsets.UTF_8)
        );
        return emailMatch && passwordMatch;
    }
}
