package com.project.services.contracts;

import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;
import com.project.models.dtos.PostDtoTop;

import java.util.List;

public interface PostService {
    List<Post> getAllUsersPosts(int userId, FilteredPostsOptions postFilterOptions);

    List<Post> getAllPosts(FilteredPostsOptions filteredPostsOptions, int page, int size);

    Post getPostById(int postId);

    List<PostDtoTop> getMostLikedPosts();

    List<PostDtoTop> getMostCommentedPosts();

    List<PostDtoTop> getMostRecentPosts();

    int getTotalPostsCount();

    void likePost(Post post, User user);

    void dislikePost(Post post, User user);

    Post createPost(Post post);

    void updatePost(User user, Post post);

    void deletePost(User user, Post post);

    void addTagToPost(User user, Post post, Tag tag);

    void deleteTagFromPost(User user, Post post, Tag tag);

    Post getPostByTitle(String title);

    List<Post> getMostRecentPostsMvc();

    List<Post> getMostLikedPostsMvc();

    List<Post> getMostCommentedPostsMvc();

    int getFilteredPostsCount(FilteredPostsOptions postFilterOptions);
}
