package com.project.controllers;

import com.project.helpers.MapperHelper;
import com.project.models.Post;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.PostDtoTopComments;
import com.project.services.contracts.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/forum")
public class PostRestController {
    private final MapperHelper mapperHelper;
    private final PostService postService;

    @Autowired
    public PostRestController(PostService postService, MapperHelper mapperHelper) {
        this.postService = postService;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/posts")
    public void createPost(@Valid @RequestBody PostDto postDto) {
        Post post = mapperHelper.fromPostDto(postDto);
        postService.createPost(post);
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
