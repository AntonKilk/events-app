package com.eventsapp.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdminAuthService authService;

    public AuthController(AdminAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest body,
                                                     HttpServletRequest request) {
        if (!authService.check(body.email(), body.password())) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "message", "Invalid credentials"));
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("ADMIN", true);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public Map<String, Boolean> me(Principal principal) {
        boolean isAdmin = principal != null && "admin".equals(principal.getName());
        return Map.of("isAdmin", isAdmin);
    }
}
