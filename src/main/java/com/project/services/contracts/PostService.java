package com.project.services.contracts;

import com.project.models.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllUsersPosts(int userId);

    List<Post> getAllPosts();

    Post getPostById(int postId);
}
