package com.project.repositories.contracts;

import com.project.models.FilteredUsersOptions;
import com.project.models.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers(FilteredUsersOptions filteredUsersOptional);

    User getUserById(int id);

    void update(User user);

    void create(User user);

    User getByUsername(String username);

    User getByEmail(String email);

    void blockUser(User user);

    void unblocked(User userToUnblock);

    void deleteUser(User user);

    Long countAllUsers();
}
