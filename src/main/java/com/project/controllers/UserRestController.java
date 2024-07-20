package com.project.controllers;

import com.project.helpers.MapperHelper;
import com.project.models.User;
import com.project.models.dtos.RegistrationDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/users")
public class UserRestController {
    private final UserService userService;
    private final MapperHelper mapperHelper;

    @Autowired
    public UserRestController(UserService userService, MapperHelper mapperHelper) {
        this.userService = userService;
        this.mapperHelper = mapperHelper;
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
    public User updateUser(@PathVariable int id,@Valid @RequestBody UserDto userDto) {
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
}
