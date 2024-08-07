package com.project.controllers.rest;

import com.project.exceptions.*;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.Comment;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.CommentDto;
import com.project.services.contracts.CommentService;
import com.project.services.contracts.PostService;
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

    private static final String PUBLISHED_SUCCESSFULLY = "Comment published successfully.";
    private static final String UPDATED_SUCCESSFULLY = "Updated successfully";
    private static final String DELETED_SUCCESSFULLY = "Deleted successfully";

    private final CommentService commentService;
    private final PostService postService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CommentRestController(CommentService commentService, PostService postService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper) {
        this.commentService = commentService;
        this.postService = postService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/post/{id}")
    public List<Comment> getAllCommentsFromPost(@RequestHeader HttpHeaders headers,
                                                @PathVariable int id,
                                                @RequestParam(required = false) String keyWord) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilteredCommentsOptions filteredCommentsOptions = new FilteredCommentsOptions(keyWord);
            return commentService.getAllCommentsFromPost(id, filteredCommentsOptions);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> createComment(@RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader HttpHeaders headers,
                                                @PathVariable int postId) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(postId);
            Comment comment = mapperHelper.createCommentForPostFromCommentDto(commentDto, user, post);
            commentService.createComment(comment, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(PUBLISHED_SUCCESSFULLY);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    //сложи дали юзъра е блокиран
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable int commentId,
                                                @RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = mapperHelper.fromCommentDtoToUpdate(commentDto, commentId);
            commentService.updateComment(comment, user);
            return ResponseEntity.status(HttpStatus.OK).body(UPDATED_SUCCESSFULLY);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable int commentId,
                                                @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Comment comment = commentService.getCommentById(commentId);
            commentService.deleteComment(comment, user);
            return ResponseEntity.status(HttpStatus.OK).body(DELETED_SUCCESSFULLY);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
