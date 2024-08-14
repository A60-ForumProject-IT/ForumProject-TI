package com.project.services;

import com.project.exceptions.*;
import com.project.helpers.PermissionHelper;
import com.project.models.FilteredUsersOptions;
import com.project.models.PhoneNumber;
import com.project.models.User;
import com.project.repositories.contracts.RoleRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final String INVALID_PERMISSION = "You dont have permissions! Only admins can do this operation.";
    private static final String ALREADY_BLOCKED = "User is already blocked";
    public static final String ALREADY_NOT_BLOCKED = "User is already not blocked";
    public static final String CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS = "You can't edit information in other user accounts";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> getAllUsers(User user, FilteredUsersOptions filteredUsersOptional, int page, int size) {
        PermissionHelper.isAdminOrModerator(user, INVALID_PERMISSION);
        return userRepository.getAllUsers(filteredUsersOptional, page, size);
    }

    @Override
    public User getUserById(User user, int id) {
        PermissionHelper.isAdminOrModerator(user, INVALID_PERMISSION);
        return userRepository.getUserById(id);
    }

    @Override
    public void update(User user, User userToBeUpdated) {
        PermissionHelper.isSameUser(user, userToBeUpdated, CAN_T_EDIT_INFORMATION_IN_OTHER_USER_ACCOUNTS);
        boolean duplicateExists = true;

        try {
            User existingUser = userRepository.getByUsername(userToBeUpdated.getUsername());
            if (existingUser.getId() == userToBeUpdated.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("User", "username", userToBeUpdated.getUsername());
        }
        userRepository.update(userToBeUpdated);
    }

    @Override
    public void create(User user) {
        boolean duplicateUsernameExist = true;
        boolean duplicateEmailExist = true;

        try {
            userRepository.getByUsername(user.getUsername());
        } catch (EntityNotFoundException e) {
            duplicateUsernameExist = false;
        }

        try {
            userRepository.getByEmail(user.getEmail());
        } catch (EntityNotFoundException e) {
            duplicateEmailExist = false;
        }

        if (duplicateUsernameExist) {
            throw new DuplicateEntityException("User", "username", user.getUsername());
        }

        if (duplicateEmailExist) {
            throw new DuplicateEntityException("User", "email", user.getEmail());
        }

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
            PermissionHelper.isAdminOrModerator(user, INVALID_PERMISSION);
            User userToBlock = userRepository.getUserById(id);
            if (user.getRole().getRoleId() == 2 && userToBlock.getRole().getRoleId() == 3) {
                throw new UnauthorizedOperationException("You can't block admins");
            }
            if (user.getId() == id) {
                throw new UnauthorizedOperationException("You can't block yourself");
            }
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
            PermissionHelper.isAdminOrModerator(user, INVALID_PERMISSION);
            User userToUnblock = userRepository.getUserById(id);
            if (user.getRole().getRoleId() == 2 && userToUnblock.getRole().getRoleId() == 3) {
                throw new UnauthorizedOperationException("You can't unblock admins");
            }
            if (user.getId() == id) {
                throw new UnauthorizedOperationException("You can't unblock yourself");
            }
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
            PermissionHelper.isAdminOrModerator(user, INVALID_PERMISSION);
            User userToDelete = userRepository.getUserById(id);
            if (user.getRole().getRoleId() == 2 && userToDelete.getRole().getRoleId() == 3) {
                throw new UnauthorizedOperationException("You can't delete admins");
            }
            if (user.getId() == id) {
                throw new UnauthorizedOperationException("You can't delete yourself");
            }
            userRepository.deleteUser(userToDelete);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("User", id);
        }
    }

    @Override
    public Long countAllUsers() {
        return userRepository.countAllUsers();
    }

    @Override
    public void userToBeAdmin(User userToBeAdmin) {
        boolean isBlocked = false;
        boolean isAdminOrModerator = true;
        try {
            PermissionHelper.isBlocked(userToBeAdmin, INVALID_PERMISSION);
        } catch (BlockedException e) {
            isBlocked = true;
        }
        try {
            PermissionHelper.isAdminOrModerator(userToBeAdmin, INVALID_PERMISSION);
        } catch (UnauthorizedOperationException e) {
            isAdminOrModerator = false;
        }
        if (isBlocked) {
            userToBeAdmin.setBlocked(false);
        }

        if (!isAdminOrModerator) {
            userToBeAdmin.setRole(roleRepository.getRoleById(2));
        }
        userRepository.userToBeModerator(userToBeAdmin);
    }

    @Override
    public void userToBeDemoted(User userToBeDemoted) {
        boolean isBlocked = false;
        boolean isAdminOrModerator = true;
        try {
            PermissionHelper.isBlocked(userToBeDemoted, INVALID_PERMISSION);
        } catch (BlockedException e) {
            isBlocked = true;
        }
        try {
            PermissionHelper.isAdminOrModerator(userToBeDemoted, INVALID_PERMISSION);
        } catch (UnauthorizedOperationException e) {
            isAdminOrModerator = false;
        }
        if (isBlocked) {
            userToBeDemoted.setBlocked(false);
        }

        if (isAdminOrModerator) {
            userToBeDemoted.setRole(roleRepository.getRoleById(1));
        }
        userRepository.userToBeDemoted(userToBeDemoted);
    }
}
