package com.project.repositories.contracts;

import com.project.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUserById(int id);

    void update(User user);

    void create(User user);
}
