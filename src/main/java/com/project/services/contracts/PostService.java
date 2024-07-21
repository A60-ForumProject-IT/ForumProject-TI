package com.project.services.contracts;

import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDtoTopComments;

import java.util.List;

public interface PostService {
    List<Post> getAllUsersPosts(int userId, FilteredPostsOptions postFilterOptions);

    List<Post> getAllPosts(User user, FilteredPostsOptions filteredPostsOptions);

    Post getPostById(int postId);

    List<PostDtoTopComments> getMostLikedPosts();

    List<PostDtoTopComments> getMostCommentedPosts();

    Post createPost(Post post);

    void updatePost(User user, Post post);

    void deletePost(User user, Post post);
}
