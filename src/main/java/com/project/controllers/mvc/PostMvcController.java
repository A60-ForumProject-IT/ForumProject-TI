package com.project.controllers.mvc;

import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.FilterPostDto;
import com.project.services.contracts.PostService;
import com.project.services.contracts.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ti/forum")
public class PostMvcController {
    private final MapperHelper mapperHelper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final TagService tagService;

    @Autowired
    public PostMvcController(MapperHelper mapperHelper, PostService postService, AuthenticationHelper authenticationHelper, TagService tagService) {
        this.mapperHelper = mapperHelper;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.tagService = tagService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/posts")
    public String showAllPosts(@ModelAttribute("filterOptions") FilterPostDto filterPostDto, Model model) {
        FilteredPostsOptions filteredPostsOptions = new FilteredPostsOptions(
                filterPostDto.getMinLikes(),
                filterPostDto.getMinDislikes(),
                filterPostDto.getMaxLikes(),
                filterPostDto.getMaxDislikes(),
                filterPostDto.getTitle(),
                filterPostDto.getContent(),
                filterPostDto.getPostedBy(),
                filterPostDto.getSortBy(),
                filterPostDto.getSortOrder(),
                filterPostDto.getCreatedBefore(),
                filterPostDto.getCreatedAfter()
        );
        List<Post> posts = postService.getAllPosts(filteredPostsOptions);
        model.addAttribute("filterOptions", filterPostDto);
        model.addAttribute("posts", posts);
        return "AllPostsView";
    }

}
