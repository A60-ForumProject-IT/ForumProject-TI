package com.project.services;

import com.project.exceptions.BlockedException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnblockedException;
import com.project.helpers.PermissionHelper;
import com.project.models.FilteredUsersOptional;
import com.project.models.User;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String INVALID_PERMISSION = "You dont have permission to do this Operation. Only admins can do this operation";
    private static final String ALREADY_BLOCKED = "User is already blocked";
    public static final String ALREADY_NOT_BLOCKED = "User is already not blocked";

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    //Валидация дали си админ. Направи метод!
    @Override
    public List<User> getAllUsers(User user, FilteredUsersOptional filteredUsersOptional) {
        PermissionHelper.isAdmin(user, INVALID_PERMISSION);
        return userRepository.getAllUsers(filteredUsersOptional);
    }

    @Override
    public User getUserById(User user, int id) {
        try {
            PermissionHelper.isAdmin(user, INVALID_PERMISSION );
            return userRepository.getUserById(id);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User", id);
        }
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
        boolean alreadyBlocked = false;
        try {
            PermissionHelper.isAdmin(user, INVALID_PERMISSION);
            User userToBlock = userRepository.getUserById(id);
            if (userToBlock.isBlocked()) {
                alreadyBlocked = true;
            }
            userToBlock.setBlocked(true);
            userRepository.blockUser(userToBlock);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User", id);
        }
        if (alreadyBlocked) {
            throw new BlockedException(ALREADY_BLOCKED);
        }

    }

    @Override
    public void unblocked(User user, int id) {
        boolean alreadyUnblocked = false;
        try {
            PermissionHelper.isAdmin(user, INVALID_PERMISSION);
            User userToUnblock = userRepository.getUserById(id);
            if (!userToUnblock.isBlocked()) {
                alreadyUnblocked = true;
            }
            userToUnblock.setBlocked(false);
            userRepository.unblocked(userToUnblock);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User", id);
        }
        if (alreadyUnblocked) {
            throw new UnblockedException(ALREADY_NOT_BLOCKED);
        }
    }

    @Override
    public void deleteUser(User user, int id) {
        try {
            PermissionHelper.isAdmin(user, INVALID_PERMISSION);
            User userToDelete = userRepository.getUserById(id);
            userRepository.deleteUser(userToDelete);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("User", id);
        }
    }
}
