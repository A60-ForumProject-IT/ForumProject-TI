package com.project.controllers;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.PostDtoTopComments;
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
@RequestMapping("api/forum")
public class PostRestController {
    private final MapperHelper mapperHelper;
    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public PostRestController(PostService postService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper) {
        this.postService = postService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }

    //Само админ може да филтрира и сортира по content и title
    @GetMapping("/posts")
    public List<Post> getAllPosts(@RequestHeader HttpHeaders headers,
                                  @RequestParam(required = false) Integer minLikes,
                                  @RequestParam(required = false) Integer minDislikes,
                                  @RequestParam(required = false) Integer maxLikes,
                                  @RequestParam(required = false) Integer maxDislikes,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String content,
                                  @RequestParam(required = false) String createdBefore,
                                  @RequestParam(required = false) String createdAfter,
                                  @RequestParam(required = false) String postedBy,
                                  @RequestParam(required = false) String sortBy,
                                  @RequestParam(required = false) String sortOrder) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilteredPostsOptions postFilterOptions =
                    new FilteredPostsOptions(minLikes, minDislikes,maxLikes, maxDislikes, title, content, createdBefore, createdAfter, postedBy, sortBy, sortOrder);
            return postService.getAllPosts(user, postFilterOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/posts")
    public ResponseEntity<String> createPost(@Valid @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = mapperHelper.fromPostDto(postDto, user);
            postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully!");
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(id);
            postService.deletePost(user, post);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Post deleted successfully!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<String> updatePost(@Valid @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = mapperHelper.fromPostDtoToUpdate(postDto, id);
            postService.updatePost(user, post);
            return ResponseEntity.status(HttpStatus.OK).body("Post updated successfully!");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("posts/{id}")
    public Post getPostById(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            return postService.getPostById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    //Всеки аутентикиран може да преглежда, филтрира и сортира постовете на друг user по content и title
    @GetMapping("/users/{userId}/posts")
    public List<Post> getAllUsersPosts(@PathVariable int userId, @RequestHeader HttpHeaders headers,
                                       @RequestParam(required = false) Integer minLikes,
                                       @RequestParam(required = false) Integer minDislikes,
                                       @RequestParam(required = false) Integer maxLikes,
                                       @RequestParam(required = false) Integer maxDislikes,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) String content,
                                       @RequestParam(required = false) String createdBefore,
                                       @RequestParam(required = false) String createdAfter,
                                       @RequestParam(required = false) String postedBy,
                                       @RequestParam(required = false) String sortBy,
                                       @RequestParam(required = false) String sortOrder) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            FilteredPostsOptions postFilterOptions =
                    new FilteredPostsOptions(minLikes, minDislikes,maxLikes, maxDislikes, title, content, createdBefore, createdAfter, postedBy, sortBy, sortOrder);
            return postService.getAllUsersPosts(userId, postFilterOptions);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/top10MostLikedPosts")
    public List<PostDtoTopComments> getMostLikedPosts() {
        return postService.getMostLikedPosts();
    }

    @GetMapping("/top10MostCommentedPosts")
    public List<PostDtoTopComments> getMostCommentedPosts() {
        return postService.getMostCommentedPosts();
    }
}
