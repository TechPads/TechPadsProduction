package com.core.core;

import com.core.core.controllers.UserController;
import com.core.core.modules.Client;
import com.core.core.modules.ClientDetail;
import com.core.core.modules.User;
import com.core.core.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user = new User();
        when(userService.getAllUsers()).thenReturn(List.of(user));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testGetAllUsersEmpty() {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetUserByIdFound() {
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userService.getUserById(1L)).thenReturn(null);

        ResponseEntity<?> response = userController.getUserById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No se encontro"));
    }

    @Test
    void testCreateUserAdmSuccess() {
        // Simula una petición HTTP
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users/adm");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        User user = new User();
        user.setId(10L);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.createUserAdm(user, bindingResult);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());

        // Validar que la URL tenga el ID correcto
        String location = response.getHeaders().getLocation().toString();
        assertTrue(location.contains("10"), "La URL de ubicación debería contener el ID del usuario creado");
    }

    @Test
    void testCreateUserAdmWithErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = userController.createUserAdm(new User(), bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateUserSuccess() {
        User user = new User();
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(user);

        ResponseEntity<?> response = userController.updateUser(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testUpdateUserNotFound() {
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(null);

        ResponseEntity<?> response = userController.updateUser(1L, new User());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteUserSuccess() {
        when(userService.deleteUser(1L)).thenReturn(true);

        ResponseEntity<String> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteUserNotFound() {
        when(userService.deleteUser(1L)).thenReturn(false);

        ResponseEntity<String> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreateClientMissingUser() {
        Client client = new Client();
        client.setUser(null);
        client.setClientDetail(new ClientDetail());

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<?> response = userController.createClient(client, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }
}
