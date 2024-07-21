package com.project.controllers;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.Comment;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.CommentDto;
import com.project.services.contracts.CommentService;
import com.project.services.contracts.PostService;
import com.project.services.contracts.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/forum/comments")
public class CommentRestController {

    public static final String PUBLISHED_SUCCESSFULLY = "Comment published successfully.";
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentRestController(CommentService commentService, PostService postService, UserService userService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.postService = postService;
        this.userService = userService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/post/{id}")
    public List<Comment> getAllCommentsFromPost(@RequestHeader HttpHeaders headers, @PathVariable int id){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            return commentService.getAllCommentsFromPost(id);
        } catch (UnauthorizedOperationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> createComment(@RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader HttpHeaders headers,
                                                 @PathVariable int postId){
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(postId);
            Comment comment = mapperHelper.createCommentForPostFromCommentDto(commentDto, user, post);
            commentService.createComment(comment, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(PUBLISHED_SUCCESSFULLY);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
