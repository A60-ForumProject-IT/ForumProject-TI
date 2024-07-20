package com.project.helpers;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.EntityNotFoundException;
import com.project.models.User;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid authentication.";
    public static final String LOGGED_USER_ERROR = "No user logged in.";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }

        try {
            String userInfo = headers.getFirst(HttpHeaders.AUTHORIZATION);
            String username = getUsername(userInfo);
            String password = getPassword(userInfo);

            User user = userService.getByUsername(username);

            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }

            return user;

        } catch (EntityNotFoundException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
    }

    private String getPassword(String userInfo) {
        int firstSpaceIndex = userInfo.indexOf(" ");

        if (firstSpaceIndex == -1) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(firstSpaceIndex + 1);
    }

    private String getUsername(String userInfo) {
        int firstSpaceIndex = userInfo.indexOf(" ");

        if (firstSpaceIndex == -1) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }

        return userInfo.substring(0, firstSpaceIndex);
    }

    public User tryGetUserFromSession(HttpSession httpSession) {
        String currentUser = (String) httpSession.getAttribute("currentUser");

        if(currentUser == null){
            throw new AuthenticationException(LOGGED_USER_ERROR);
        }

        return userService.getByUsername(currentUser);
    }
    public User verifyAuthentication(String username, String password) {
        try {
            User user = userService.getByUsername(username);
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }
            return user;

        } catch (EntityNotFoundException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
    }
}