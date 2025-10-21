package com.core.core.controllers;

import com.core.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    @Lazy
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String token = userService.login(credentials.get("username"), credentials.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            e.printStackTrace(); // imprime en consola
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


}
