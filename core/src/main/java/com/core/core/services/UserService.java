package com.core.core.services;

import com.core.core.modules.User;
import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    List<User> getAllUsersActive();
    User getUserById(Long id);
    User createUser(User user);
    User updateUser(Long id, User user);
    boolean deleteUser(Long id);

    //LOGIN
    User getUserByUsername(String username);
    String login(String username, String password);
}

