package com.example.Insurance.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/testJWT")
    public Map<String, Object> checkJwt(Authentication authentication) {

        if (authentication == null) {
            return Map.of(
                    "message", "Authentication is NULL"
            );
        }

        return Map.of(
                "message", "JWT is valid",
                "username", authentication.getName(),
                "roles", authentication.getAuthorities(),
                "authenticated", authentication.isAuthenticated()
        );
    }
}
