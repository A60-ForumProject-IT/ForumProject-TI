package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.PermissionHelper;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.repositories.contracts.TagRepository;
import com.project.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PostServiceImpl implements PostService {
    public static final String AUTHORIZATION_EXCEPTION = "You are not creator of the post to edit it!";
    public static final String BLOCKED_USER_ERROR = "You are blocked and cannot create posts!";
    public static final String UNAUTHORIZED_DELETE_ERROR = "You are not authorized to delete this post!";
    public static final String FILTER_AND_SORT_ERROR = "You are not authorized to filter and sort posts!";
    public static final String CREATOR_LIKE_ERROR = "You are the creator of the post and can't like it!";
    public static final String MULTIPLE_LIKE_ERROR = "You already liked this comment!";
    public static final String BLOCKED_USER_EDIT_ERROR = "You are blocked and can't edit your post!";
    private static final String CREATOR_DISLIKE_ERROR = "You are the creator of the post and can't dislike it!";
    public static final String USER_BLOCKED_ERROR = "You are blocked and can't add tags!";
    public static final String AUTHORIZATION_ERROR_FOR_TAGS = "You are not the creator of the post and can't add tags!";

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<Post> getAllPosts(FilteredPostsOptions filteredPostsOptions) {
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
    public void likePost(Post post, User user) {
        PermissionHelper.isNotSameUser(post.getPostedBy(), user, CREATOR_LIKE_ERROR);
        Set<User> usersWhoDislikedThePost = post.getUsersWhoDislikedPost();
        Set<User> usersWhoLikedThePost = post.getUsersWhoLikedPost();

        if (usersWhoLikedThePost.contains(user)) {
            throw new UnauthorizedOperationException(MULTIPLE_LIKE_ERROR);
        }

        if (usersWhoDislikedThePost.remove(user)) {
            post.setDislikes(post.getDislikes() - 1);
        }
        usersWhoLikedThePost.add(user);
        post.setLikes(post.getDislikes() + 1);

        postRepository.updatePost(post);
    }

    @Override
    public void dislikePost(Post post, User user) {
        PermissionHelper.isNotSameUser(post.getPostedBy(), user, CREATOR_DISLIKE_ERROR);
        Set<User> usersWhoDislikedThePost = post.getUsersWhoDislikedPost();
        Set<User> usersWhoLikedThePost = post.getUsersWhoLikedPost();

        if (usersWhoDislikedThePost.contains(user)) {
            throw new UnauthorizedOperationException(MULTIPLE_LIKE_ERROR);
        }

        if (usersWhoLikedThePost.remove(user)) {
            post.setLikes(post.getLikes() - 1);
        }
        usersWhoDislikedThePost.add(user);
        post.setDislikes(post.getDislikes() + 1);

        postRepository.updatePost(post);
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
        PermissionHelper.isBlocked(post.getPostedBy(), BLOCKED_USER_EDIT_ERROR);
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
        PermissionHelper.isBlocked(user, BLOCKED_USER_ERROR);
        postRepository.deletePost(post);
    }

    @Override
    public void addTagToPost(User user, Post post, Tag tag) {
        PermissionHelper.isBlocked(user, USER_BLOCKED_ERROR);
        PermissionHelper.isSameUser(user, post.getPostedBy(), AUTHORIZATION_ERROR_FOR_TAGS);

        Set<Tag> tagSet = post.getPostTags();

        if (tagSet.contains(tag)) {
            throw new DuplicateEntityException("Tag", "name", tag.getTag());
        }

        Tag existingTag;
        try {
            existingTag = tagRepository.getTagByName(tag.getTag());
        } catch (EntityNotFoundException e) {
            tagRepository.createTag(tag);
            existingTag = tag;
        }
        tagSet.add(existingTag);
        postRepository.addTagToPost(post);
    }

    @Override
    public void deleteTagFromPost(User user, Post post, Tag tag) {
        PermissionHelper.isBlocked(user, USER_BLOCKED_ERROR);
        PermissionHelper.isAdminOrSameUser(user, post.getPostedBy(), AUTHORIZATION_ERROR_FOR_TAGS);

        Set<Tag> tagSet = post.getPostTags();

        if (!tagSet.contains(tag)) {
            throw new EntityNotFoundException("Tag", "name", tag.getTag());
        }
        tagSet.remove(tag);
        postRepository.deleteTagFromPost(post);
    }

    @Override
    public Post getPostByTitle(String title) {
        return postRepository.getPostByTitle(title);
    }

    @Override
    public List<Post> getAllUsersPosts(int userId, FilteredPostsOptions postFilterOptions) {
        return postRepository.getAllUsersPosts(userId, postFilterOptions);
    }

    public boolean hasUserLikedPost(Post post, User user) {
        return post.getUsersWhoLikedPost().contains(user); // Example logic
    }

    public boolean hasUserDislikedPost(Post post, User user) {
        return post.getUsersWhoDislikedPost().contains(user); // Example logic
    }
}
