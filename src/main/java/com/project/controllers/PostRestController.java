package com.project.controllers;

import com.project.exceptions.DuplicateEntityException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.PostDtoTopComments;
import com.project.services.contracts.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts")
    public void createPost(@Valid @RequestBody PostDto postDto, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Post post = mapperHelper.fromPostDto(postDto, user);
            postService.createPost(post);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("posts/{id}")
    public Post getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    @GetMapping("/users/{userId}/posts")
    public List<Post> getAllUsersPosts(@PathVariable int userId) {
        return postService.getAllUsersPosts(userId);
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
