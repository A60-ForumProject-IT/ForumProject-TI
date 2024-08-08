package com.project.controllers.rest;

import com.project.exceptions.*;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.PostDtoTop;
import com.project.models.dtos.TagDto;
import com.project.services.contracts.PostService;
import com.project.services.contracts.TagService;
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
    private final TagService tagService;

    @Autowired
    public PostRestController(PostService postService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper, TagService tagService) {
        this.postService = postService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
        this.tagService = tagService;
    }

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
            return postService.getAllPosts(postFilterOptions);
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
        } catch (BlockedException e) {
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
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
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
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/posts/{id}")
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
            authenticationHelper.tryGetUser(headers);
            FilteredPostsOptions postFilterOptions =
                    new FilteredPostsOptions(minLikes, minDislikes,maxLikes, maxDislikes, title, content, createdBefore, createdAfter, postedBy, sortBy, sortOrder);
            return postService.getAllUsersPosts(userId, postFilterOptions);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}/likes")
    public void likePost(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(id);
            postService.likePost(post, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/posts/{id}/dislikes")
    public void dislikePost(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(id);
            postService.dislikePost(post, user);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/posts/totalPostsCount")
    public int getTotalPostsCount() {
        return postService.getTotalPostsCount();
    }

    @GetMapping("/posts/top10MostLikedPosts")
    public List<PostDtoTop> getMostLikedPosts() {
        return postService.getMostLikedPosts();
    }

    @GetMapping("/posts/top10MostCommentedPosts")
    public List<PostDtoTop> getMostCommentedPosts() {
        return postService.getMostCommentedPosts();
    }

    @GetMapping("/posts/top10MostRecentPosts")
    public List<PostDtoTop> getMostRecentPosts() {
        return postService.getMostRecentPosts();
    }

    @PutMapping("/posts/{postId}/tags")
    public void addTagToPost(@Valid @RequestBody TagDto tagDto, @PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(postId);
            Tag tag = mapperHelper.fromTagDto(tagDto);
            postService.addTagToPost(user, post, tag);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/posts/{postId}/{tagId}")
    public void deleteTagFromPost(@PathVariable int tagId, @PathVariable int postId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = postService.getPostById(postId);
            Tag tag = tagService.getTagById(tagId);
            postService.deleteTagFromPost(user, post, tag);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
