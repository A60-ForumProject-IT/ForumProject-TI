package com.project;

import com.project.exceptions.*;
import com.project.helpers.PermissionHelper;
import com.project.models.*;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.repositories.contracts.TagRepository;
import com.project.services.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getAllPosts_ShouldThrowException_WhenUserIsNotAdmin() {
        User regularUser = new User();
        regularUser.setRole(new Role(2, "user"));
        FilteredPostsOptions options = new FilteredPostsOptions(
                10, null, null, null, "test title", null, null, null, null, null, null
        );

        assertThrows(UnauthorizedOperationException.class, () -> {
            postService.getAllPosts(regularUser, options);
        });
    }

    @Test
    void createPost_ShouldThrowDuplicateEntityException_WhenPostTitleExists() {
        User user = new User();
        Post post = new Post();
        post.setTitle("Duplicate Title");
        post.setPostedBy(user);

        when(postRepository.getPostByTitle(anyString())).thenReturn(post);

        assertThrows(DuplicateEntityException.class, () -> postService.createPost(post));
        verify(postRepository, times(1)).getPostByTitle(post.getTitle());
    }

    @Test
    void likePost_ShouldIncreaseLikeCount_WhenUserLikesPost() {
        User user = new User();
        user.setId(1);
        User postOwner = new User();
        postOwner.setId(2);

        Post post = new Post();
        post.setPostedBy(postOwner);
        post.setLikes(0);
        post.setUsersWhoLikedPost(new HashSet<>());
        post.setUsersWhoDislikedPost(new HashSet<>());

        postService.likePost(post, user);

        assertTrue(post.getUsersWhoLikedPost().contains(user));
        assertEquals(1, post.getLikes());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void dislikePost_ShouldIncreaseDislikeCount_WhenUserDislikesPost() {
        User user = new User();
        user.setId(1);
        User postOwner = new User();
        postOwner.setId(2);

        Post post = new Post();
        post.setPostedBy(postOwner);
        post.setDislikes(0);
        post.setUsersWhoLikedPost(new HashSet<>());
        post.setUsersWhoDislikedPost(new HashSet<>());

        postService.dislikePost(post, user);

        assertTrue(post.getUsersWhoDislikedPost().contains(user));
        assertEquals(1, post.getDislikes());
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    void updatePost_ShouldThrowUnauthorizedOperationException_WhenUserIsNotPostOwner() {
        User user = new User();
        user.setId(1);
        User postOwner = new User();
        postOwner.setId(2);

        Post post = new Post();
        post.setPostedBy(postOwner);

        assertThrows(UnauthorizedOperationException.class, () -> postService.updatePost(user, post));
    }

    @Test
    void deletePost_ShouldCallRepositoryDelete_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setRole(new Role(3, "admin"));
        Post post = new Post();

        postService.deletePost(adminUser, post);

        verify(postRepository, times(1)).deletePost(post);
    }

    @Test
    void addTagToPost_ShouldAddTag_WhenUserIsPostOwner() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setPostedBy(user);
        post.setPostTags(new HashSet<>());
        Tag tag = new Tag();
        tag.setTag("New Tag");

        when(tagRepository.getTagByName(anyString())).thenThrow(EntityNotFoundException.class);

        postService.addTagToPost(user, post, tag);

        assertTrue(post.getPostTags().contains(tag));
        verify(tagRepository, times(1)).createTag(tag);
        verify(postRepository, times(1)).addTagToPost(post);
    }

    @Test
    void deleteTagFromPost_ShouldRemoveTag_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setRole(new Role(3, "admin"));
        Tag tag = new Tag();
        tag.setTag("Existing Tag");
        Post post = new Post();
        post.setPostTags(new HashSet<>(Set.of(tag)));

        postService.deleteTagFromPost(adminUser, post, tag);

        assertFalse(post.getPostTags().contains(tag));
        verify(postRepository, times(1)).deleteTagFromPost(post);
    }

    @Test
    void getPostById_ShouldReturnPost_WhenPostExists() {
        Post expectedPost = new Post();
        expectedPost.setPostId(1);

        when(postRepository.getPostById(1)).thenReturn(expectedPost);

        Post actualPost = postService.getPostById(1);

        assertEquals(expectedPost, actualPost);
        verify(postRepository, times(1)).getPostById(1);
    }

    @Test
    void getMostLikedPosts_ShouldReturnMostLikedPosts() {
        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());

        when(postRepository.getMostLikedPosts()).thenReturn(expectedPosts);

        List<PostDtoTop> actualPosts = postService.getMostLikedPosts();

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).getMostLikedPosts();
    }

    @Test
    void getMostCommentedPosts_ShouldReturnMostCommentedPosts() {
        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());

        when(postRepository.getMostCommentedPosts()).thenReturn(expectedPosts);

        List<PostDtoTop> actualPosts = postService.getMostCommentedPosts();

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).getMostCommentedPosts();
    }

    @Test
    void getMostRecentPosts_ShouldReturnMostRecentPosts() {
        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());

        when(postRepository.getMostRecentPosts()).thenReturn(expectedPosts);

        List<PostDtoTop> actualPosts = postService.getMostRecentPosts();

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).getMostRecentPosts();
    }

    @Test
    void getTotalPostsCount_ShouldReturnTotalPostsCount() {
        int expectedCount = 10;

        when(postRepository.getTotalPostsCount()).thenReturn(expectedCount);

        int actualCount = postService.getTotalPostsCount();

        assertEquals(expectedCount, actualCount);
        verify(postRepository, times(1)).getTotalPostsCount();
    }

    @Test
    void getAllUsersPosts_ShouldReturnUsersPosts() {
        int userId = 1;
        FilteredPostsOptions options = new FilteredPostsOptions(
                10, null, null, null, "test title", null, null, null, null, null, null
        );
        List<Post> expectedPosts = List.of(new Post(), new Post());

        when(postRepository.getAllUsersPosts(userId, options)).thenReturn(expectedPosts);

        List<Post> actualPosts = postService.getAllUsersPosts(userId, options);

        assertEquals(expectedPosts, actualPosts);
        verify(postRepository, times(1)).getAllUsersPosts(userId, options);
    }

    @Test
    void updatePost_ShouldUpdatePost_WhenNoDuplicatesAndUserIsAuthorized() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setPostedBy(user);
        post.setTitle("Unique Title");

        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR")).thenAnswer(invocation -> null);

            when(postRepository.getPostByTitle(post.getTitle())).thenThrow(new EntityNotFoundException("Post", "title", post.getTitle()));

            postService.updatePost(user, post);

            verify(postRepository, times(1)).updatePost(post);
        }
    }

    @Test
    void updatePost_ShouldThrowDuplicateEntityException_WhenTitleIsDuplicate() {
        User user = new User();
        user.setId(1);
        Post post = new Post();
        post.setPostedBy(user);
        post.setTitle("Duplicate Title");

        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR")).thenAnswer(invocation -> null);

            when(postRepository.getPostByTitle(post.getTitle())).thenReturn(post);

            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
        }
    }

    @Test
    void updatePost_ShouldThrowAuthorizationException_WhenUserIsNotAuthorized() {
        User user = new User();
        user.setId(1);
        User anotherUser = new User();
        anotherUser.setId(2);
        Post post = new Post();
        post.setPostedBy(anotherUser);
        post.setTitle("Title");

        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION"))
                    .thenThrow(new AuthenticationException("AUTHORIZATION_EXCEPTION"));

            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
        }
    }

    @Test
    void updatePost_ShouldThrowBlockedUserException_WhenUserIsBlocked() {
        User user = new User();
        user.setId(1);
        user.setBlocked(true);
        Post post = new Post();
        post.setPostedBy(user);
        post.setTitle("Title");

        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR"))
                    .thenThrow(new BlockedException("BLOCKED_USER_EDIT_ERROR"));

            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
        }
    }
}
