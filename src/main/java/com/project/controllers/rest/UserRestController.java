package com.project.controllers.rest;

import com.project.exceptions.*;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.Avatar;
import com.project.models.FilteredUsersOptions;
import com.project.models.PhoneNumber;
import com.project.models.User;
import com.project.models.dtos.PhoneNumberDto;
import com.project.models.dtos.RegistrationDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.AvatarService;
import com.project.services.contracts.PhoneService;
import com.project.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class UserRestController {
    private static final String BLOCKED_SUCCESSFULLY = "User blocked successfully";
    private static final String UNBLOCKED_SUCCESSFULLY = "User unblocked successfully";
    private static final String DELETING_USER_SUCCESSFULLY = "Deleting user successfully";
    public static final String NOW_MODERATOR = "User is now moderator.";
    public static final String PHONE_TO_AN_ADMIN_SUCCESSFULLY = "Added phone to an admin successfully.";


    private final UserService userService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;
    private final PhoneService phoneService;
    private final AvatarService avatarService;

    @Autowired
    public UserRestController(UserService userService, MapperHelper mapperHelper,
                              AuthenticationHelper authenticationHelper, PhoneService phoneService, AvatarService avatarService) {
        this.userService = userService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
        this.phoneService = phoneService;
        this.avatarService = avatarService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) String firstName,
                                  @RequestParam(required = false) String email,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String SortOrder,
                                  @RequestParam(defaultValue = "0") int page, // Параметър за страницата
                                  @RequestParam(defaultValue = "5") int size // Параметър за размера на страницата
    ) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilteredUsersOptions filteredUsersOptional = new FilteredUsersOptions(username, firstName, email, sortBy, SortOrder);
            return userService.getAllUsers(user, filteredUsersOptional, page, size);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
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

    @PutMapping("/users/{id}")
    public User updateUser(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody UserDto userDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeUpdated = mapperHelper.updateUserFromDto(userDto, id);
            userService.update(user, userToBeUpdated);
            return user;
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@Valid @RequestBody RegistrationDto registrationDto) {
        try {
            User user = mapperHelper.createUserFromRegistrationDto(registrationDto);
            userService.create(user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/users/{userId}/moderators")
    public ResponseEntity<String> updateUserAdmin(@RequestHeader HttpHeaders headers, @PathVariable int userId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            User userToBeAdmin = userService.getUserById(user, userId);
            userService.userToBeAdmin(userToBeAdmin);
            return new ResponseEntity<>(NOW_MODERATOR, HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


    @PutMapping("/users/{id}/block")
    public ResponseEntity<String> blockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.blockUser(user, id);
            return new ResponseEntity<>(BLOCKED_SUCCESSFULLY, HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/users/{id}/unblock")
    public ResponseEntity<String> unblockUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.unblocked(user, id);
            return new ResponseEntity<>(UNBLOCKED_SUCCESSFULLY, HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnblockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            userService.deleteUser(user, id);
            return new ResponseEntity<>(DELETING_USER_SUCCESSFULLY, HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> countAllUsers() {
        Long count = userService.countAllUsers();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping("/users/phones")
    public ResponseEntity<String> addPhoneToAnAdmin(@RequestHeader HttpHeaders headers,
                                                    @RequestBody @Valid PhoneNumberDto phoneNumberDto) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            PhoneNumber phoneNumber = mapperHelper.getFromPhoneDto(phoneNumberDto);
            phoneService.addPhoneToAnAdmin(user, phoneNumber);
            return new ResponseEntity<>(PHONE_TO_AN_ADMIN_SUCCESSFULLY, HttpStatus.OK);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/uploadAvatar")
    public ResponseEntity<String> uploadAvatar(@PathVariable int userId,
                                               @RequestParam("file") MultipartFile file,
                                               @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            if (user.getId() != userId) {
                throw new UnauthorizedOperationException("You are not authorized to modify this user's avatar.");
            }
            Avatar avatar = avatarService.uploadAvatar(user, file);
            return new ResponseEntity<>(avatar.getAvatar(), HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload avatar", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("users/{userId}/avatar")
    public ResponseEntity<Avatar> getAvatarByUserId(@PathVariable int userId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Avatar avatar = avatarService.getUserAvatar(user);
            if (avatar != null) {
                return new ResponseEntity<>(avatar, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}/avatar")
    public ResponseEntity<String> deleteAvatar(@PathVariable int userId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            avatarService.deleteAvatarFromUser(user);
            return new ResponseEntity<>("Avatar deleted successfully", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to delete avatar", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Avatar not found", HttpStatus.NOT_FOUND);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
