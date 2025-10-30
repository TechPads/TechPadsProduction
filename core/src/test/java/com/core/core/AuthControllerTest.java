package com.core.core;

import com.core.core.controllers.AuthController;
import com.core.core.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "12345"
        );

        when(userService.login(anyString(), anyString())).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("fake-jwt-token", body.get("token"));
    }

    @Test
    void testLoginError() {
        Map<String, String> credentials = Map.of(
                "username", "admin",
                "password", "wrongpass"
        );

        when(userService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue(body.get("error").toString().contains("Credenciales inválidas"));
    }
}
