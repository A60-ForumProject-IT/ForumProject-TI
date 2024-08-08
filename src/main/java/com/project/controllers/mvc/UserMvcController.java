package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.User;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
}
