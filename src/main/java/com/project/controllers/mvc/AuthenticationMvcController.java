package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.Avatar;
import com.project.models.User;
import com.project.models.dtos.LoginDto;
import com.project.models.dtos.RegistrationDto;
import com.project.services.contracts.AvatarService;
import com.project.services.contracts.RoleService;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/ti/auth")
public class AuthenticationMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final MapperHelper mapperHelper;
    private final AvatarService avatarService;
    private final RoleService roleService;

    @Autowired
    public AuthenticationMvcController(UserService userService, AuthenticationHelper authenticationHelper, MapperHelper mapperHelper, AvatarService avatarService, RoleService roleService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
        this.avatarService = avatarService;
        this.roleService = roleService;
    }

    @ModelAttribute("isAdmin")
    public boolean populateIsAdmin(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            if (user.getRole().equals(roleService.getRoleById(3))) {
                return true;
            }
        }
        return false;
    }

    @ModelAttribute("loggedUser")
    public User populateLoggedUser(HttpSession httpSession) {
        if (httpSession.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(httpSession);
        }
        return new User();
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

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession){
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked()
        );
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("login", new LoginDto());
        model.addAttribute("register", new RegistrationDto());
        model.addAttribute("showLogin", true);
        return "AuthView";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @ModelAttribute("login") LoginDto login,
                              BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("register", new RegistrationDto());
            model.addAttribute("showLogin", true);
            return "AuthView";
        }
        try {
            authenticationHelper.verifyAuthentication(login.getUsername(), login.getPassword());
            session.setAttribute("currentUser", login.getUsername());
            return "redirect:/ti";
        } catch (AuthenticationException e) {
            bindingResult.rejectValue("username", "error.login", e.getMessage());
            model.addAttribute("register", new RegistrationDto());
            model.addAttribute("showLogin", true);
            return "AuthView";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/ti";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("login", new LoginDto());
        model.addAttribute("register", new RegistrationDto());
        model.addAttribute("showLogin", false);
        return "AuthView";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("register") RegistrationDto registrationDto,
                                 BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("login", new LoginDto());
            model.addAttribute("showLogin", false);
            return "AuthView";
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "registration_error", "Passwords do not match");
            model.addAttribute("login", new LoginDto());
            model.addAttribute("showLogin", false);
            return "AuthView";
        }

        try {
            User user = mapperHelper.createUserFromRegistrationDto(registrationDto);

            Avatar defaultAvatar = avatarService.initializeDefaultAvatar();
            user.setAvatar(defaultAvatar);

            userService.create(user);
            return "redirect:/ti/auth/login";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("username", "registration_error", e.getMessage());
            model.addAttribute("login", new LoginDto());
            model.addAttribute("showLogin", false);
            return "AuthView";
        }

    }
}
