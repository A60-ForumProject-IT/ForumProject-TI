package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.*;
import com.project.models.dtos.FilterCommentDto;
import com.project.models.dtos.FilterUserDto;
import com.project.models.dtos.PhoneNumberDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/ti/users")
public class UserMvcController {

    public static final String YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE = "You dont have access to this page";
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final MapperHelper mapperHelper;
    private final AvatarService avatarService;
    private final PhoneService phoneService;
    private final RoleService roleService;
    private final CommentService commentService;

    @Autowired
    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, MapperHelper mapperHelper, AvatarService avatarService, PhoneService phoneService, RoleService roleService, CommentService commentService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
        this.avatarService = avatarService;
        this.phoneService = phoneService;
        this.roleService = roleService;
        this.commentService = commentService;
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if ( user.getRole().getRoleId() == 3) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.isBlocked()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @ModelAttribute("isModerator")
    public boolean populateIsModerator(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().equals(roleService.getRoleById(2))) {
                return true;
            }
        }
        return false;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/edit")
    public String showEditUserPage(Model model, HttpSession session) {
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            UserDto userDto = mapperHelper.toUserDto(currentUser);
            model.addAttribute("user", currentUser);
            model.addAttribute("avatarUrl", currentUser.getAvatar().getAvatar());
            model.addAttribute("userDto", userDto);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("isAdmin", currentUser.getRole().getRoleId() == 3);
            model.addAttribute("phoneNumber", new PhoneNumberDto());
            return "EditUserView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }


    @PostMapping("/edit")
    public String handleEditUser(
            @Valid @ModelAttribute("userDto") UserDto userToBeEdited,
            BindingResult bindingResult, HttpSession session, Model model) {

        User currentUser = authenticationHelper.tryGetUserFromSession(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            model.addAttribute("avatarUrl", currentUser.getAvatar().getAvatar());
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("isAdmin", currentUser.getRole().getRoleId() == 3);
            model.addAttribute("phoneNumber", new PhoneNumberDto());
            return "EditUserView";
        }

        String avatarUrl = currentUser.getAvatar() != null ? currentUser.getAvatar().getAvatar() : "/images/default-avatar.png";
        model.addAttribute("avatarUrl", avatarUrl);

        try {
            if (!userToBeEdited.getPassword().equals(userToBeEdited.getPasswordConfirmation())) {
                bindingResult.rejectValue("passwordConfirmation", "registration_error", "Passwords do not match");
                model.addAttribute("userId", currentUser.getId());
                model.addAttribute("phoneNumber", new PhoneNumberDto());
                return "EditUserView";
            }
            User updatedUser = mapperHelper.updateUserFromDto(userToBeEdited, currentUser.getId());
            userService.update(currentUser, updatedUser);
            return "redirect:/ti";
        } catch (AuthenticationException e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("phoneNumber", new PhoneNumberDto());
            return "EditUserView";
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("phoneNumber", new PhoneNumberDto());
            return "EditUserView";
        }
    }

    @PostMapping("/edit/avatar")
    public String uploadAvatar(@RequestParam("avatarFile") MultipartFile avatarFile, HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Avatar avatar = avatarService.uploadAvatar(user, avatarFile);
            model.addAttribute("avatarUrl", avatar.getAvatar());
            return "redirect:/ti/users/edit";
        } catch (Exception e) {
            return "ErrorView";
        }
    }

    @GetMapping("/admin")
    public String showAdminPortal(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3 || user.getRole().getRoleId() == 2) {
                return "AdminPanelView";
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/admin/users")
    public String showAllUsers(@ModelAttribute("filterUsersOptions") FilterUserDto filterUserDto,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);

            if (user.getRole().getRoleId() == 1) {
                model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
                model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
                return "ErrorView";
            }

            if (user.getRole().getRoleId() == 3 || user.getRole().getRoleId() == 2) {
                FilteredUsersOptions filteredUsersOptions = new FilteredUsersOptions(
                        filterUserDto.getUsername(),
                        filterUserDto.getFirstName(),
                        filterUserDto.getEmail(),
                        filterUserDto.getSortBy(),
                        filterUserDto.getSortOrder()
                );
                int totalFilteredPosts = userService.getFilteredUsersCount(filteredUsersOptions);
                int totalPages = (int) Math.ceil((double) totalFilteredPosts / size);
                List<User> users = userService.getAllUsers(user, filteredUsersOptions, page, size);
                model.addAttribute("filterUsersOptions", filterUserDto);
                model.addAttribute("users", users);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
                model.addAttribute("size", size);
                return "AllUsersView";
            }
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/{id}")
    public String showUserDetails(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            User userToDisplay = userService.getUserById(user, id);
            model.addAttribute("user", userToDisplay);
            return "UserDetailsView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3 || user.getRole().getRoleId() == 2) {
                userService.deleteUser(user, id);
                return "redirect:/ti/users/admin/users";
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/block")
    public String blockUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3 || user.getRole().getRoleId() == 2) {
                userService.blockUser(user, id);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/unblock")
    public String unblockUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3 || user.getRole().getRoleId() == 2) {
                userService.unblocked(user, id);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/promote")
    public String promoteUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            User userToBePromoted = userService.getUserById(user, id);
            if (user.getRole().getRoleId() == 3) {
                userService.userToBeAdmin(userToBePromoted);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/demote")
    public String demoteUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            User userToBeDemoted = userService.getUserById(user, id);
            if (user.getRole().getRoleId() == 3) {
                userService.userToBeDemoted(userToBeDemoted);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @PostMapping("/admin/{id}/phone")
    public String addPhone(@PathVariable int id, @Valid @ModelAttribute("phoneNumber") PhoneNumberDto phoneNumberDto,
                           BindingResult bindingResult, Model model, HttpSession session) {

        User currentUser = authenticationHelper.tryGetUserFromSession(session);
        if (bindingResult.hasErrors()) {
            UserDto userDto = mapperHelper.toUserDto(currentUser);
            model.addAttribute("phoneNumber", phoneNumberDto);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("userDto", userDto);
            model.addAttribute("user", currentUser);
            model.addAttribute("avatarUrl", currentUser.getAvatar().getAvatar());
            model.addAttribute("isAdmin", currentUser.getRole().getRoleId() == 3);
            return "EditUserView";
        }

        String avatarUrl = currentUser.getAvatar() != null ? currentUser.getAvatar().getAvatar() : "/images/default-avatar.png";
        model.addAttribute("avatarUrl", avatarUrl);

        try {
            PhoneNumber phoneNumber = mapperHelper.getFromPhoneDto(phoneNumberDto);
            if (currentUser.getRole().getRoleId() == 3) {
                phoneService.addPhoneToAnAdmin(currentUser, phoneNumber);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("phoneNumber", "error.phone", e.getMessage());
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("userDto", new UserDto());
            model.addAttribute("phoneNumber", phoneNumberDto);
            return "EditUserView";
        }
    }


    @PostMapping("/admin/{id}/phone/{phoneId}/remove")
    public String removePhone(@PathVariable int id, @PathVariable int phoneId,
                              Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3 || user.getId() == id) {
                phoneService.removePhoneFromAdmin(user, phoneId);
                return "redirect:/ti/users/" + id;
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/posts")
    public String showUserPosts(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            User userToDisplay = userService.getUserById(user, id);
            model.addAttribute("user", userToDisplay);
            return "UserAllPostsView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }

    @GetMapping("/{id}/comments")
    public String showUserComments(@PathVariable int id, @ModelAttribute("filterCommentOptions") FilterCommentDto filterCommentDto,
                                   Model model, HttpSession session) {
        FilteredCommentsOptions filteredCommentsOptions = new FilteredCommentsOptions(filterCommentDto.getKeyWord());


        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            User userToDisplay = userService.getUserById(user, id);
            List<Comment> comments = commentService.getAllCommentsFromUser(userToDisplay.getId(), filteredCommentsOptions);
            model.addAttribute("filterCommentOptions", filterCommentDto);
            model.addAttribute("comments", comments);
            model.addAttribute("userId", userToDisplay.getId());
            model.addAttribute("user", userToDisplay);
            return "UserAllCommentsView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", YOU_DONT_HAVE_ACCESS_TO_THIS_PAGE);
            return "ErrorView";
        }
    }
}