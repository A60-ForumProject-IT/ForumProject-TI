package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredUsersOptions;
import com.project.models.User;
import com.project.models.dtos.FilterUserDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ti/users")
public class UserMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final MapperHelper mapperHelper;


    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper, MapperHelper mapperHelper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
    }


    @ModelAttribute("isAdminOrModerator")
    public boolean populateIsAdmin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 2 || user.getRole().getRoleId() == 3) {
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

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/edit")
    public String showEditUserPage(Model model, HttpSession session) {
        User currentUser;
        try {
            currentUser = authenticationHelper.tryGetUserFromSession(session);
            model.addAttribute("user", currentUser);
            return "EditUserView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @PostMapping("/edit")
    public String handleEditUser(@Valid @ModelAttribute("user") UserDto userToBeEdited,
                                 BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "EditUserView";
        }
        try {
            User currentUser = authenticationHelper.tryGetUserFromSession(session);
            User updatedUser = mapperHelper.updateUserFromDto(userToBeEdited, currentUser.getId());
            userService.update(currentUser, updatedUser);
            return "redirect:/ti";
        } catch (Exception e) {
            bindingResult.rejectValue("username", "error.user", e.getMessage());
            return "EditUserView";
        }
    }

    @GetMapping("/admin")
    public String showAdminPortal(HttpSession session, Model model) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3) {
                return "AdminPanelView";
            }
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/admin/users")
    public String showAllUsers(@ModelAttribute("filterUsersOptions") FilterUserDto filterUserDto,
                               Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);

            if (user.getRole().getRoleId() == 1){
                model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
                model.addAttribute("error", "You dont have access to this page");
                return "ErrorView";
            }

            if (user.getRole().getRoleId() == 3) {
                FilteredUsersOptions filteredUsersOptions = new FilteredUsersOptions(
                        filterUserDto.getUsername(),
                        filterUserDto.getFirstName(),
                        filterUserDto.getEmail(),
                        filterUserDto.getSortBy(),
                        filterUserDto.getSortOrder()
                );
                List<User> users = userService.getAllUsers(user, filteredUsersOptions);
                model.addAttribute("filterUsersOptions", filterUserDto);
                model.addAttribute("users", users);
                return "AllUsersView";
            }
            return "ErrorView";
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/admin/users/{id}")
    public String showUserDetails(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3) {
                User userToDisplay = userService.getUserById(user, id);
                model.addAttribute("user", userToDisplay);
                return "UserDetailsView";
            }
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
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3) {
                userService.deleteUser(user, id);
                return "redirect:/ti/users/admin/users";
            }
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
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/block")
    public String blockUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3) {
                userService.blockUser(user, id);
                return "redirect:/ti/users/admin/users/" + id;
            }
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
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        }
    }

    @PostMapping("/admin/users/{id}/unblock")
    public String unblockUser(@PathVariable int id, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().getRoleId() == 3) {
                userService.unblocked(user, id);
                return "redirect:/ti/users/admin/users/" + id;
            }
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
            model.addAttribute("error", "You dont have access to this page");
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
                return "redirect:/ti/users/admin/users/" + id;
            }
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
            model.addAttribute("error", "You dont have access to this page");
            return "ErrorView";
        }
    }
}
