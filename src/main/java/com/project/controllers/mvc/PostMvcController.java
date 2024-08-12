package com.project.controllers.mvc;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.*;
import com.project.models.dtos.CommentDto;
import com.project.models.dtos.FilterPostDto;
import com.project.models.dtos.PostDto;
import com.project.services.RoleServiceImpl;
import com.project.services.contracts.CommentService;
import com.project.services.contracts.PostService;
import com.project.services.contracts.RoleService;
import com.project.services.contracts.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String showPost(@PathVariable int id, Model model,
                           @RequestParam(value = "editCommentId", required = false) Integer editCommentId,
                           FilteredCommentsOptions filteredCommentsOptions, HttpSession session) {
        try {
            Post post = postService.getPostById(id);
            List<Comment> postComments = commentService.getAllCommentsFromPost(post, filteredCommentsOptions);

            model.addAttribute("commentToAdd", new CommentDto());
            model.addAttribute("comments", postComments);
            model.addAttribute("post", post);
            model.addAttribute("postToBeLiked", post);
            model.addAttribute("postToBeDisliked", post);
            model.addAttribute("editPostId", id);

            User currentUser = null;
            boolean isPostCreator = false;

            if (session.getAttribute("currentUser") != null) {
                currentUser = authenticationHelper.tryGetUserFromSession(session);
                isPostCreator = isUserPostCreator(currentUser, post);
            }

            boolean hasLiked = currentUser != null && postService.hasUserLikedPost(post, currentUser);
            boolean hasDisliked = currentUser != null && postService.hasUserDislikedPost(post, currentUser);

            model.addAttribute("hasLiked", hasLiked);
            model.addAttribute("hasDisliked", hasDisliked);
            model.addAttribute("isPostCreator", isPostCreator);


            PostDto postToBeUpdated = mapperHelper.toPostDto(post);
            model.addAttribute("postToBeUpdated", postToBeUpdated);

            if (editCommentId != null) {
                Comment commentToEdit = commentService.getCommentById(editCommentId);
                CommentDto commentToUpdate = mapperHelper.toCommentDto(commentToEdit);
                model.addAttribute("commentToUpdate", commentToUpdate);
            } else {
                model.addAttribute("commentToUpdate", new CommentDto());
            }

            model.addAttribute("editCommentId", editCommentId);


            if (currentUser != null) {
                model.addAttribute("editPermissions", getEditPermissionsMap(currentUser, postComments));
            }
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{id}")
    public String addCommentToPost(@Valid @ModelAttribute("commentToAdd") CommentDto commentDto,
                                   BindingResult bindingResult,
                                   @PathVariable int id,
                                   Model model,
                                   HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "SinglePostView";
        }

        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            Comment comment = mapperHelper.createCommentForPostFromCommentDto(commentDto, user, post);
            commentService.createComment(comment, user);
            return "redirect:/ti/forum/posts/" + id;
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

    @GetMapping("/posts/{postId}/comments/{commentId}")
    public String editCommentPage(@PathVariable int postId, @PathVariable int commentId, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        Comment comment = commentService.getCommentById(commentId);
        CommentDto newComment = mapperHelper.toCommentDto(comment);
        model.addAttribute("comment", newComment);
        return "SinglePostView";
    }

    @PostMapping("/posts/{postId}/comments/{commentId}")
    public String editComment(@PathVariable int postId,
                              @PathVariable int commentId,
                              @Valid @ModelAttribute("commentToUpdate") CommentDto commentDto,
                              HttpSession session,
                              BindingResult bindingResult,
                              Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        if (bindingResult.hasErrors()) {
            return "SinglePostView";
        }

        try {
            Comment comment = mapperHelper.fromCommentDtoToUpdate(commentDto, commentId);
            commentService.updateComment(comment, user);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable int postId, @PathVariable int commentId, HttpSession session, Model model) {
        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        try {
            Comment comment = commentService.getCommentById(commentId);
            commentService.deleteComment(comment, user);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (AuthenticationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

    }

    @GetMapping("/posts/new")
    public String newPost(Model model, HttpSession session) {

        try {
            authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
        model.addAttribute("newPost", new PostDto());
        return "PostCreateView";
    }

    @PostMapping("/posts/new")
    public String newPost(@Valid @ModelAttribute("newPost") PostDto postDto, BindingResult bindingResult, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "PostCreateView";
        }

        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }


        try {
            Post post = mapperHelper.fromPostDto(postDto, user);
            postService.createPost(post);
            return "redirect:/ti/forum/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("title", "duplicateTitle", e.getMessage());
            return "PostCreateView";
        }
    }

    @PostMapping("/posts/{postId}/like")
    public String likePost(@PathVariable int postId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        try {
            Post post = postService.getPostById(postId);
            model.addAttribute("postToBeLiked", post);
            postService.likePost(post, user);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{postId}/dislike")
    public String dislikePost(@PathVariable int postId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        try {
            Post post = postService.getPostById(postId);
            model.addAttribute("postToBeDisliked", post);
            postService.dislikePost(post, user);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{postId}/edit")
    public String editPost(@PathVariable int postId, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        try {
            Post post = postService.getPostById(postId);
            model.addAttribute("post", post);
            model.addAttribute("editPostId", postId);  // Set the editPostId to control the visibility
            PostDto postDto = mapperHelper.toPostDto(post);
            model.addAttribute("postToBeUpdated", postDto);
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{postId}/edit")
    public String editPost(@PathVariable int postId,
                           @Valid @ModelAttribute("postToBeUpdated") PostDto postDto,
                           BindingResult bindingResult,
                           HttpSession session,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentToAdd", new CommentDto());
            return "SinglePostView";
        }

        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }


        try {
            Post post = mapperHelper.fromPostDtoToUpdate(postDto, postId);
            postService.updatePost(user, post);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            model.addAttribute("statusCode", HttpStatus.CONFLICT.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

    }

    @PostMapping("/posts/{postId}/delete")
    public String deletePost(@PathVariable int postId, Model model, HttpSession session) {
        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        try {
            Post post = postService.getPostById(postId);
            postService.deletePost(user, post);
            return "redirect:/ti/forum/posts";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    private Map<Integer, Boolean> getEditPermissionsMap(User user, List<Comment> comments) {
        Map<Integer, Boolean> editPermissions = new HashMap<>();
        for (Comment comment : comments) {
            boolean canEdit = comment.getUserId().equals(user);
            editPermissions.put(comment.getCommentId(), canEdit);
        }
        return editPermissions;
    }

    private boolean isUserPostCreator(User user, Post post) {
        return user.equals(post.getPostedBy());
    }

}
