package com.project.repositories.contracts;

import com.project.models.Post;

import java.util.List;

public interface PostRepository {
    List<Post> getAllUsersPosts(int userId);
}
