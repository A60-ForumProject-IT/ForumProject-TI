package com.project.repositories.contracts;

import com.project.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUserById(int id);

    void update(User user);

    void create(User user);

    User getByUsername(String username);

    void blockUser(User user);

    void unblocked(User userToUnblock);
}
