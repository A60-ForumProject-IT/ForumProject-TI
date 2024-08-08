package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.*;
import com.project.models.dtos.CommentDto;
import com.project.models.dtos.FilterPostDto;
import com.project.services.RoleServiceImpl;
import com.project.services.contracts.CommentService;
import com.project.services.contracts.PostService;
import com.project.services.contracts.RoleService;
import com.project.services.contracts.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ti/forum")
public class PostMvcController {
    private final MapperHelper mapperHelper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final TagService tagService;
    private final CommentService commentService;
    private final RoleService roleService;

    @Autowired
    public PostMvcController(MapperHelper mapperHelper, PostService postService, AuthenticationHelper authenticationHelper, TagService tagService, CommentService commentService, RoleServiceImpl roleServiceImpl, RoleService roleService) {
        this.mapperHelper = mapperHelper;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.tagService = tagService;
        this.commentService = commentService;
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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/posts")
    public String showAllPosts(@ModelAttribute("filterPostOptions") FilterPostDto filterPostDto, Model model) {
        FilteredPostsOptions filteredPostsOptions = new FilteredPostsOptions(
                filterPostDto.getMaxLikes(),
                filterPostDto.getMinLikes(),
                filterPostDto.getMaxDislikes(),
                filterPostDto.getMinDislikes(),
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
            model.addAttribute("commentToAdd", new CommentDto());
            model.addAttribute("comments", postComments);
            model.addAttribute("post", post);
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{id}")
    public String addCommentToPost(@ModelAttribute("commentToAdd") CommentDto commentDto, @PathVariable int id,
                                   Model model,
                                   HttpSession session,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "SinglePostView";
        }

        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            Comment comment = mapperHelper.createCommentForPostFromCommentDto(commentDto, user, post);
            if (user.equals(post.getPostedBy())) {
                model.addAttribute("isCreator", true);
            }
            commentService.createComment(comment, user);
            return "redirect:/ti/forum/posts/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }  catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public String editCommentPage(@PathVariable int postId, @PathVariable int commentId, Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Comment comment = commentService.getCommentById(commentId);
            Post post = postService.getPostById(postId);
            model.addAttribute("postToUpdate", post);
            model.addAttribute("commentToUpdate", comment);
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String editComment(@PathVariable int postId,
                              @PathVariable int commentId,
                              @ModelAttribute("commentToUpdate") CommentDto commentDto,
                              HttpSession session,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "SinglePostView";
        }

        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Comment existingComment = commentService.getCommentById(commentId);
            Post post = postService.getPostById(postId);
            if (user.equals(post.getPostedBy())) {
                model.addAttribute("isCreator", true);
            }
            existingComment.setContent(commentDto.getContent());
            model.addAttribute("commentToUpdate", existingComment);
            commentService.updateComment(existingComment, user);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}
