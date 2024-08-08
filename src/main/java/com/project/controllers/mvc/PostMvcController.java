package com.project.controllers.mvc;

import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.*;
import com.project.models.dtos.FilterPostDto;
import com.project.services.contracts.CommentService;
import com.project.services.contracts.PostService;
import com.project.services.contracts.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ti/forum")
public class PostMvcController {
    private final MapperHelper mapperHelper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final TagService tagService;
    private final CommentService commentService;

    @Autowired
    public PostMvcController(MapperHelper mapperHelper, PostService postService, AuthenticationHelper authenticationHelper, TagService tagService, CommentService commentService) {
        this.mapperHelper = mapperHelper;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.tagService = tagService;
        this.commentService = commentService;
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
    public String showAllPosts(@ModelAttribute("filterPostOptions") FilterPostDto filterPostDto, Model model) {
        FilteredPostsOptions filteredPostsOptions = new FilteredPostsOptions( filterPostDto.getMinLikes(),
                filterPostDto.getMaxLikes(),
                filterPostDto.getMinDislikes(),
                filterPostDto.getMaxDislikes(),
                filterPostDto.getTitle(),
                filterPostDto.getContent(),
                filterPostDto.getCreatedBefore(),
                filterPostDto.getCreatedAfter(),
                filterPostDto.getPostedBy(),
                filterPostDto.getSortBy(),
                filterPostDto.getSortOrder()
        );
        List<Post> posts = postService.getAllPosts(filteredPostsOptions);
        model.addAttribute("filterPostOptions", filterPostDto);
        model.addAttribute("posts", posts);
        return "AllPostsView";
    }

    @GetMapping("/posts/{id}")
    public String showPost(@PathVariable int id, Model model, FilteredCommentsOptions filteredCommentsOptions) {
        try {
            Post post = postService.getPostById(id);
            List<Comment> postComments = commentService.getAllCommentsFromPost(post, filteredCommentsOptions);
            model.addAttribute("comments", postComments);
            model.addAttribute("post", post);
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }


}
