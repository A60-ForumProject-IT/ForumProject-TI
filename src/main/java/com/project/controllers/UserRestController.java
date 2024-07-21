package com.project.controllers;

import com.project.exceptions.*;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredUsersOptional;
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
    private static final String UNBLOCKED_SUCCESSFULLY = "User unblocked successfully";
    private static final String DELETING_USER_SUCCESSFULLY = "Deleting user successfully";


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

    //филтрация по username, email, firstName
    @GetMapping
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String firstName
                                  ){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilteredUsersOptional filteredUsersOptional = new FilteredUsersOptional(username, email, firstName);
            return userService.getAllUsers(user, filteredUsersOptional);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return userService.getUserById(user, id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.deleteUser(user, id);
            return new ResponseEntity<>(DELETING_USER_SUCCESSFULLY,HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
