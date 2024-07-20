package com.project.controllers;

import com.project.exceptions.BlockedException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.exceptions.UnblockedException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.helpers.PermissionHelper;
import com.project.models.User;
import com.project.models.dtos.RegistrationDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/forum/users")
public class UserRestController {
    private static final String BLOCKED_SUCCESSFULLY = "User blocked successfully";
    public static final String UNBLOCKED_SUCCESSFULLY = "User unblocked successfully";


    private final UserService userService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UserRestController(UserService userService, MapperHelper mapperHelper,
                              AuthenticationHelper authenticationHelper) {
        this.userService = userService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }


    //админа трябва да прави това само
    //филтрация по username, email, firstName
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //админ трябва да прави това
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @Valid @RequestBody UserDto userDto) {
        User user = mapperHelper.updateUserFromDto(userDto, id);
        userService.update(user);
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody RegistrationDto registrationDto) {
        User user = mapperHelper.createUserFromRegistrationDto(registrationDto);
        userService.create(user);
        return user;
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.blockUser(user, id);
            return new ResponseEntity<>(BLOCKED_SUCCESSFULLY,HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BlockedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{id}/unblock")
    public ResponseEntity<String> unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.unblocked(user, id);
            return new ResponseEntity<>(UNBLOCKED_SUCCESSFULLY,HttpStatus.OK);
        } catch (UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnblockedException e){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
