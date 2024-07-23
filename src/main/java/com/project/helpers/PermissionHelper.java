package com.project.helpers;

import com.project.exceptions.BlockedException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.models.User;

public class PermissionHelper {
    public static final String ROLE_ADMIN = "admin";
    private static final String ROLE_MODERATOR = "moderator";

    public static void isAdmin(User authenticatedUser, String message) {
        boolean isAdmin = false;
        if (authenticatedUser.getRole().getName().equals(ROLE_ADMIN)) {
            isAdmin = true;
        }

        if (!isAdmin) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isAdminOrSameUser(User authorizedUser, User user, String message) {
        boolean isAuthorized = false;
        if (authorizedUser.equals(user)) {
            isAuthorized = true;
        } else {
            if (authorizedUser.getRole().getName().equals(ROLE_ADMIN) || authorizedUser.getRole().getName().equals(ROLE_MODERATOR)) {
                isAuthorized = true;
            }
        }

        if (!isAuthorized) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isSameUser(User user, User userWhoCreatedThePost, String message) {
        if (!user.equals(userWhoCreatedThePost)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isNotSameUser(User userToBeUpdated, User userIsAuthorized, String message) {

        if (userToBeUpdated.equals(userIsAuthorized)) {
            throw new UnauthorizedOperationException(message);
        }
    }

    public static void isBlocked(User authorizedUser, String message) {
        if (authorizedUser.isBlocked()) {
            throw new BlockedException(message);
        }
    }

    public static void isAdminOrModerator(User user, String message) {
        boolean isAuthorized = false;

        if (user.getRole().getName().equals(ROLE_ADMIN) || user.getRole().getName().equals(ROLE_MODERATOR)) {
            isAuthorized = true;
        }

        if (!isAuthorized) {
            throw new UnauthorizedOperationException(message);
        }
    }
}