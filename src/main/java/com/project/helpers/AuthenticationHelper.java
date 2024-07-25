package com.project.helpers;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.EntityNotFoundException;
import com.project.models.User;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AuthenticationHelper {
    public static final String INVALID_AUTHENTICATION_ERROR = "Invalid username or password.";
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
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader.startsWith("Basic ")) {
                String base64Credentials = authHeader.substring("Basic".length()).trim();
                byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                final String[] values = credentials.split(":", 2);

                String username = values[0];
                String password = values[1];

                User user = userService.getByUsername(username);

                if (!user.getPassword().equals(password)) {
                    throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
                }
                return user;
            } else {
                throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
            }
        } catch (EntityNotFoundException | ArrayIndexOutOfBoundsException e) {
            throw new AuthenticationException(INVALID_AUTHENTICATION_ERROR);
        }
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
