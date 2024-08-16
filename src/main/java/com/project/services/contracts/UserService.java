package com.project.services.contracts;

import com.project.models.FilteredUsersOptions;
import com.project.models.PhoneNumber;
import com.project.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers(User user, FilteredUsersOptions filteredUsersOptional, int page, int size);

    User getUserById(User user, int id);
    
  //  User getUserById(int id);

    void update(User user, User userToBeUpdated);

    void create(User user);

    User getByUsername(String username);

    void blockUser(User user, int id);

    void unblocked(User user, int id);

    void deleteUser(User user, int id);

    Long countAllUsers();

    void userToBeAdmin(User userToBeAdmin);

    void userToBeDemoted(User userToBeDemoted);


    int getFilteredUsersCount(FilteredUsersOptions filteredUsersOptions);
}
