package com.core.core.controllers;

import com.core.core.modules.Client;
import com.core.core.modules.ClientDetail;
import com.core.core.modules.User;
import com.core.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    @Lazy
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getAllActiveUsers() {
        List<User> users = userService.getAllUsersActive();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro ningun usuario con el id: " + id);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/adm")
    public ResponseEntity<?> createUserAdm(@RequestBody User user) {
        User createdUser = userService.createUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/cli")
    public ResponseEntity<?> createClient(@RequestBody Client client) {
        try {
            User user = client.getUser();
            ClientDetail clientDetail = client.getClientDetail();

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El objeto 'user' es obligatorio"));
            }
            if (clientDetail == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El objeto 'clientDetail' es obligatorio"));
            }
            if (clientDetail.getCity() == null || clientDetail.getCity().getCityID() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "City es obligatorio y debe tener un cityID válido"));
            }
            if (clientDetail.getDepartment() == null || clientDetail.getDepartment().getDepID() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Department es obligatorio y debe tener un depID válido"));
            }

            User createdUser = userService.createClient(user, clientDetail);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdUser.getId())
                    .toUri();

            return ResponseEntity.created(location).body(createdUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);

        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro ningun usuario con el id: " + id);
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro ningun usuario con el id: " + id);
        }
        return ResponseEntity.noContent().build();
    }


}
