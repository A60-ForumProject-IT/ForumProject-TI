package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.PermissionHelper;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    public static final String AUTHORIZATION_EXCEPTION = "You are not creator of the post to edit it!";
    public static final String BLOCKED_USER_ERROR = "You are blocked and cannot create posts!";
    public static final String UNAUTHORIZED_DELETE_ERROR = "You are not authorized to delete this post";
    public static final String FILTER_AND_SORT_ERROR = "You are not authorized to filter and sort posts.";
    private final PostRepository postRepository;


    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllPosts(User user, FilteredPostsOptions filteredPostsOptions) {
        PermissionHelper.isAdmin(user, FILTER_AND_SORT_ERROR);
        return postRepository.getAllPosts(filteredPostsOptions);
    }

    @Override
    public Post getPostById(int postId) {
        return postRepository.getPostById(postId);
    }

    @Override
    public List<PostDtoTop> getMostLikedPosts() {
        return postRepository.getMostLikedPosts();
    }

    @Override
    public List<PostDtoTop> getMostCommentedPosts() {
        return postRepository.getMostCommentedPosts();
    }

    @Override
    public List<PostDtoTop> getMostRecentPosts() {
        return postRepository.getMostRecentPosts();
    }

    @Override
    public int getTotalPostsCount() {
        return postRepository.getTotalPostsCount();
    }

    @Override
    public Post createPost(Post post) {
        PermissionHelper.isBlocked(post.getPostedBy(), BLOCKED_USER_ERROR);
        boolean duplicateExists = true;
        try {
            postRepository.getPostByTitle(post.getTitle());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Post", "title", post.getTitle());
        }

        return postRepository.createPost(post);
    }

    @Override
    public void updatePost(User user, Post post) {
        PermissionHelper.isSameUser(user, post.getPostedBy(), AUTHORIZATION_EXCEPTION);
        boolean duplicateExists = true;
        try {
           postRepository.getPostByTitle(post.getTitle());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Post", "title", post.getTitle());
        }
        postRepository.updatePost(post);
    }

    @Override
    public void deletePost(User user, Post post) {
        PermissionHelper.isAdminOrSameUser(user, post.getPostedBy(), UNAUTHORIZED_DELETE_ERROR);
        postRepository.deletePost(post);
    }

    @Override
    public List<Post> getAllUsersPosts(int userId, FilteredPostsOptions postFilterOptions) {
        return postRepository.getAllUsersPosts(userId, postFilterOptions);
    }
}
