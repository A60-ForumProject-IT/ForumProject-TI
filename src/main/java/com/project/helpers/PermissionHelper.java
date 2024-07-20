package com.project.helpers;

import com.project.exceptions.UnauthorizedOperationException;
import com.project.models.User;
import org.springframework.stereotype.Component;

@Component
public class PermissionHelper {
    public static final String ROLE_ADMIN = "ADMIN";

    public static void isAdmin(User authenticatedUser, String message) {
        boolean isAdmin = false;
        if (authenticatedUser.getRole().getName().equals(ROLE_ADMIN)) {
            isAdmin = true;
        }

        if (!isAdmin) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isAdminOrSameUser(User userToBeUpdated, User userIsAuthorized, String message) {
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

    public static void isSameUser(User userToBeUpdated, User userIsAuthorized, String message) {
        if (!userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isNotSameUser(User userToBeUpdated,
                                     User userIsAuthorized,
                                     String message) {

        if (userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }
    public static void isBlocked(User authorizedUser, String message) {
        if (authorizedUser.isBlocked()) {
            throw new UnauthorizedOperationException(message);
        }
    }
}