package com.project.helpers;

import com.project.exceptions.UnauthorizedOperationException;
import com.project.models.Role;
import com.project.models.User;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class PermissionHelper {
    private final UserService userService;
    public static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    public PermissionHelper(UserService userService) {
        this.userService = userService;
    }

    public void isAdmin(User authenticatedUser, String message) {
        boolean isAdmin = false;
        if (authenticatedUser.getRole().getName().equals(ROLE_ADMIN)) {
            isAdmin = true;
        }

        if (!isAdmin) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isAdminOrSameUser(User userToBeUpdated, User userIsAuthorized, String message) {
        boolean isAuthorized = false;
        if (userToBeUpdated.equals(userIsAuthorized)) {
            isAuthorized = true;
        } else {
            if (userIsAuthorized.getRole().getName().equals(ROLE_ADMIN)) {
                isAuthorized = true;
            }
        }

        if (!isAuthorized) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isSameUser(User userToBeUpdated, User userIsAuthorized, String message) {
        if (!userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public void isNotSameUser(User userToBeUpdated,
                                     User userIsAuthorized,
                                     String message) {

        if (userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }
    public void isBlocked(User authorizedUser, String message) {
        if (authorizedUser.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }
}