package com.project.controllers.mvc;

import com.project.helpers.AuthenticationHelper;
import com.project.models.User;
import com.project.services.contracts.PostService;
import com.project.services.contracts.RoleService;
import com.project.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ti")
public class HomeMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final RoleService roleService;
    private final PostService postService;

    public HomeMvcController(AuthenticationHelper authenticationHelper, UserService userService, RoleService roleService, PostService postService) {
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.roleService = roleService;
        this.postService = postService;
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

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("isBlocked")
    public boolean populateIsBlocked(HttpSession httpSession){
        return (httpSession.getAttribute("currentUser") != null &&
                authenticationHelper
                        .tryGetUserFromSession(httpSession)
                        .isBlocked()
        );
    }

    @ModelAttribute("username")
    public String populateUsername(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return user.getUsername();
        }
        return null;
    }

    @GetMapping
    public String showHomePage(Model model) {
        long userCount = userService.countAllUsers();
        long postsCount = postService.getTotalPostsCount();
        model.addAttribute("userCount", userCount);
        model.addAttribute("postsCount", postsCount);
        return "HomeView";
    }

    @GetMapping("/top10mostCommentedPosts")
    public String showTop10MostCommentedPosts(Model model) {
        model.addAttribute("mostCommentedPosts", postService.getMostCommentedPostsMvc());
        return "TopTenMostCommentedPosts";
    }

    @GetMapping("/top10mostLikedPosts")
    public String showTop10MostLikedPosts(Model model) {
        model.addAttribute("mostLikedPosts", postService.getMostLikedPostsMvc());
        return "TopTenMostLikedPosts";
    }

    @GetMapping("/top10mostRecentPosts")
    public String showTop10MostRecentPosts(Model model) {
        model.addAttribute("mostRecentPosts", postService.getMostRecentPostsMvc());
        return "TopTenMostRecentPosts";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "AboutView";
    }
}
