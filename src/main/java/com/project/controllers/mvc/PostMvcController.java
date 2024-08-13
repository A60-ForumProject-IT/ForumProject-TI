package com.project.controllers.mvc;

import com.project.exceptions.*;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.*;
import com.project.models.dtos.CommentDto;
import com.project.models.dtos.FilterPostDto;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.TagDto;
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

import java.util.List;

@Controller
@RequestMapping("/ti/forum")
public class PostMvcController {
    private final MapperHelper mapperHelper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final CommentService commentService;
    private final RoleService roleService;
    private final TagService tagService;

    @Autowired
    public PostMvcController(MapperHelper mapperHelper, PostService postService, AuthenticationHelper authenticationHelper, CommentService commentService, RoleService roleService, TagService tagService) {
        this.mapperHelper = mapperHelper;
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.commentService = commentService;
        this.roleService = roleService;
        this.tagService = tagService;
    }

    @ModelAttribute("tags")
    public List<Tag> populateTags() {
        return tagService.getAllTags();
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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/posts")
    public String showAllPosts(@ModelAttribute("filterPostOptions") FilterPostDto filterPostDto, Model model) {
        filterPostDto.sanitize();
        FilteredPostsOptions filteredPostsOptions = new FilteredPostsOptions(
                filterPostDto.getMaxLikes(),
                filterPostDto.getMinLikes(),
                filterPostDto.getMaxDislikes(),
                filterPostDto.getMinDislikes(),
                filterPostDto.getTitle(),
                filterPostDto.getTagName(),
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
    public String handleCreatePost(@Valid @ModelAttribute("postDto") PostDto postDto,
                                   BindingResult errors,
                                   Model model,
                                   HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }


        try {
            Post post = mapperHelper.fromPostDto(postDto, user);
            postService.createPost(post);
            return "redirect:/ti/forum/posts/" + post.getPostId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("title", "duplicateTitle", e.getMessage());
            return "PostCreateView";
        }
    }

    @GetMapping("/posts/{id}")
    public String showSinglePost(@PathVariable int id,
                                 Model model,
                                 HttpSession session,
                                 FilteredCommentsOptions filteredCommentsOptions) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("post", post);
            model.addAttribute("relatedComments", commentService.getAllCommentsFromPost(post, filteredCommentsOptions));
            model.addAttribute("commentDto", new CommentDto());
            return "SinglePostView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";        }
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditPostForm(@PathVariable int id,
                                   Model model,
                                   HttpSession session) {

        try {
            authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            PostDto postDto = mapperHelper.toPostDto(post);
            model.addAttribute("postDto", postDto);
            model.addAttribute("tagDto", new TagDto());
            model.addAttribute("tagsInPost", postService.getPostById(id).getPostTags());
            return "PostEditView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @PostMapping("/posts/{id}/edit")
    public String handleEditPost(@PathVariable int id,
                                 @Valid @ModelAttribute("postDto") PostDto postDto,
                                 BindingResult errors,
                                 Model model,
                                 HttpSession session) {
        if (errors.hasErrors()) {
            return "PostEditView";
        }

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Post post = mapperHelper.fromPostDtoToUpdate(postDto, id);
            postService.updatePost(loggedInUser, post);
            return "redirect:/ti/forum/posts/" + id;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{id}/comment")
    public String showAddCommentForm(@PathVariable int id,
                                     Model model,
                                     HttpSession session) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            model.addAttribute("loggedInUser", loggedInUser);
            model.addAttribute("post", post);
            model.addAttribute("commentDto", new CommentDto());
            return "SinglePostAddComment";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }
    }

    @PostMapping("/posts/{id}/comment")
    public String handleAddCommentToPost(@PathVariable int id,
                                         @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                                         BindingResult errors,
                                         Model model,
                                         HttpSession session) {
        if (errors.hasErrors()) {
            return "SinglePostAddComment";
        }
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(id);
            Comment commentToAdd = mapperHelper.createCommentForPostFromCommentDto(commentDto, loggedInUser, post);
            commentService.createComment(commentToAdd, loggedInUser);
            return "redirect:/ti/forum/posts/" + id;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/delete")
    public String removeCommentFromPost(@PathVariable int postId,
                                        @PathVariable int commentId,
                                        HttpSession session,
                                        Model model) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(session);
            Comment commentToBeRemoved = commentService.getCommentById(commentId);
            commentService.deleteComment(commentToBeRemoved, loggedUser);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public String showEditCommentPage(@PathVariable int postId,
                                      @PathVariable int commentId,
                                      Model model,
                                      HttpSession session) {
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Post post = postService.getPostById(postId);

            Comment commentToBeEdited = commentService.getCommentById(commentId);
            CommentDto commentDto = mapperHelper.toCommentDto(commentToBeEdited);
            model.addAttribute("commentDto", commentDto);
            return "CommentEditView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    public String handleEditCommentPage(@PathVariable int postId,
                                        @PathVariable int commentId,
                                        @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                                        BindingResult errors,
                                        Model model,
                                        HttpSession session) {
        if (errors.hasErrors()) {
            return "SinglePostView";
        }
        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Comment commentToBeEdited = mapperHelper.fromCommentDtoToUpdate(commentDto, commentId);
            commentService.updateComment(commentToBeEdited, loggedInUser);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }

    @GetMapping("/posts/{postId}/delete")
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

    @GetMapping("/posts/{postId}/like")
    public String likePost(@PathVariable int postId,
                           Model model,
                           HttpSession httpSession) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(httpSession);
            Post postToBeLiked = postService.getPostById(postId);
            postService.likePost(postToBeLiked, loggedUser);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/ti/forum/posts/" + postId;
        }
    }

    @GetMapping("/posts/{postId}/dislike")
    public String dislikePost(@PathVariable int postId,
                              Model model,
                              HttpSession httpSession) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(httpSession);
            Post postToBeLiked = postService.getPostById(postId);
            postService.dislikePost(postToBeLiked, loggedUser);
            return "redirect:/ti/forum/posts/" + postId;
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/ti/forum/posts/" + postId;
        }
    }

    @PostMapping("/posts/{postId}/edit/add-tag")
    public String handleAddTagToPost(@PathVariable int postId,
                                     @Valid @ModelAttribute("tagDto") TagDto tagDto,
                                     BindingResult errors,
                                     Model model,
                                     HttpSession session) {
        Post post = postService.getPostById(postId);
        model.addAttribute("postDto", mapperHelper.toPostDto(post));
        model.addAttribute("tagDto", tagDto);
        model.addAttribute("postId", postId);
        if (errors.hasErrors()) {
            return "PostEditView";
        }

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            Tag tag = mapperHelper.fromTagDto(tagDto);
            postService.addTagToPost(loggedInUser, post, tag);
            return "redirect:/ti/forum/posts/" + postId + "/edit";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (ForbiddenTagException e) {
            errors.rejectValue("tagName", "invalid_tag_name", e.getMessage());
            return "PostEditView";
        }
    }

    @GetMapping("/posts/{postId}/edit/remove-tag/{tagId}")
    public String handleRemoveTagFromPost(@PathVariable int postId,
                                          @PathVariable int tagId,
                                          Model model,
                                          HttpSession httpSession) {
        try {
            User loggedUser = authenticationHelper.tryGetUserFromSession(httpSession);
            Post postToBeEdited = postService.getPostById(postId);
            Tag tagToBeRemoved = tagService.getTagById(tagId);
            postService.deleteTagFromPost(loggedUser, postToBeEdited, tagToBeRemoved);
            return "redirect:/ti/forum/posts/" + postId + "/edit";
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }
    }
}
