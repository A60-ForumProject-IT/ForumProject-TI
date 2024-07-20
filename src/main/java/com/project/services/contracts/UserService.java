package com.project.services.contracts;

import com.project.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(int id);

    void update(User user);

    void create(User user);
    User getByUsername(String username);

    void blockUser(User user, int id);
}
