package com.project.services;

import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.PermissionHelper;
import com.project.models.User;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String INVALID_PERMISSION = "You dont have permission to block this User";
    private final UserRepository userRepository;
    private final PermissionHelper permissionHelper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PermissionHelper permissionHelper) {
        this.userRepository = userRepository;
        this.permissionHelper = permissionHelper;
    }


    //Валидация дали си админ. Направи метод!
    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void update(User user) {
         userRepository.update(user);
    }

    @Override
    public void create(User user) {
        userRepository.create(user);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public void blockUser(User user, int id) {
        try {
            PermissionHelper.isAdmin(user, INVALID_PERMISSION);
            User userToBlock = userRepository.getUserById(id);
            userToBlock.setBlocked(true);
            userRepository.blockUser(userToBlock);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User", id);
        }

    }
}
