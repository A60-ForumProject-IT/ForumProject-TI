package com.project;

import com.project.exceptions.*;
import com.project.helpers.PermissionHelper;
import com.project.helpers.TestHelpers;
import com.project.models.*;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.repositories.contracts.TagRepository;
import com.project.services.PostServiceImpl;
import com.project.services.contracts.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

//    @Test
//    void getAllPosts_ShouldThrowException_WhenUserIsNotAdmin() {
//        User regularUser = new User();
//        regularUser.setRole(new Role(2, "user"));
//        FilteredPostsOptions options = new FilteredPostsOptions(
//                10, null, null, null, "test title", null, null, null, null, null, null
//        );
//
//        assertThrows(UnauthorizedOperationException.class, () -> {
//            postService.getAllPosts(regularUser, options);
//        });
//    }
//
//    @Test
//    void createPost_ShouldThrowDuplicateEntityException_WhenPostTitleExists() {
//        User user = new User();
//        Post post = new Post();
//        post.setTitle("Duplicate Title");
//        post.setPostedBy(user);
//
//        when(postRepository.getPostByTitle(anyString())).thenReturn(post);
//
//        assertThrows(DuplicateEntityException.class, () -> postService.createPost(post));
//        verify(postRepository, times(1)).getPostByTitle(post.getTitle());
//    }
//
//    @Test
//    void likePost_ShouldIncreaseLikeCount_WhenUserLikesPost() {
//        User user = new User();
//        user.setId(1);
//        User postOwner = new User();
//        postOwner.setId(2);
//
//        Post post = new Post();
//        post.setPostedBy(postOwner);
//        post.setLikes(0);
//        post.setUsersWhoLikedPost(new HashSet<>());
//        post.setUsersWhoDislikedPost(new HashSet<>());
//
//        postService.likePost(post, user);
//
//        assertTrue(post.getUsersWhoLikedPost().contains(user));
//        assertEquals(1, post.getLikes());
//        verify(postRepository, times(1)).updatePost(post);
//    }
//
//    @Test
//    void dislikePost_ShouldIncreaseDislikeCount_WhenUserDislikesPost() {
//        User user = new User();
//        user.setId(1);
//        User postOwner = new User();
//        postOwner.setId(2);
//
//        Post post = new Post();
//        post.setPostedBy(postOwner);
//        post.setDislikes(0);
//        post.setUsersWhoLikedPost(new HashSet<>());
//        post.setUsersWhoDislikedPost(new HashSet<>());
//
//        postService.dislikePost(post, user);
//
//        assertTrue(post.getUsersWhoDislikedPost().contains(user));
//        assertEquals(1, post.getDislikes());
//        verify(postRepository, times(1)).updatePost(post);
//    }
//
//    @Test
//    void updatePost_ShouldThrowUnauthorizedOperationException_WhenUserIsNotPostOwner() {
//        User user = new User();
//        user.setId(1);
//        User postOwner = new User();
//        postOwner.setId(2);
//
//        Post post = new Post();
//        post.setPostedBy(postOwner);
//
//        assertThrows(UnauthorizedOperationException.class, () -> postService.updatePost(user, post));
//    }
//
//    @Test
//    void deletePost_ShouldCallRepositoryDelete_WhenUserIsAdmin() {
//        User adminUser = new User();
//        adminUser.setRole(new Role(3, "admin"));
//        Post post = new Post();
//
//        postService.deletePost(adminUser, post);
//
//        verify(postRepository, times(1)).deletePost(post);
//    }
//
//    @Test
//    void addTagToPost_ShouldAddTag_WhenUserIsPostOwner() {
//        User user = new User();
//        user.setId(1);
//        Post post = new Post();
//        post.setPostedBy(user);
//        post.setPostTags(new HashSet<>());
//        Tag tag = new Tag();
//        tag.setTag("New Tag");
//
//        when(tagRepository.getTagByName(anyString())).thenThrow(EntityNotFoundException.class);
//
//        postService.addTagToPost(user, post, tag);
//
//        assertTrue(post.getPostTags().contains(tag));
//        verify(tagRepository, times(1)).createTag(tag);
//        verify(postRepository, times(1)).addTagToPost(post);
//    }
//
//    @Test
//    void deleteTagFromPost_ShouldRemoveTag_WhenUserIsAdmin() {
//        User adminUser = new User();
//        adminUser.setRole(new Role(3, "admin"));
//        Tag tag = new Tag();
//        tag.setTag("Existing Tag");
//        Post post = new Post();
//        post.setPostTags(new HashSet<>(Set.of(tag)));
//
//        postService.deleteTagFromPost(adminUser, post, tag);
//
//        assertFalse(post.getPostTags().contains(tag));
//        verify(postRepository, times(1)).deleteTagFromPost(post);
//    }
//
//    @Test
//    void getPostById_ShouldReturnPost_WhenPostExists() {
//        Post expectedPost = new Post();
//        expectedPost.setPostId(1);
//
//        when(postRepository.getPostById(1)).thenReturn(expectedPost);
//
//        Post actualPost = postService.getPostById(1);
//
//        assertEquals(expectedPost, actualPost);
//        verify(postRepository, times(1)).getPostById(1);
//    }
//
//    @Test
//    void getMostLikedPosts_ShouldReturnMostLikedPosts() {
//        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());
//
//        when(postRepository.getMostLikedPosts()).thenReturn(expectedPosts);
//
//        List<PostDtoTop> actualPosts = postService.getMostLikedPosts();
//
//        assertEquals(expectedPosts, actualPosts);
//        verify(postRepository, times(1)).getMostLikedPosts();
//    }
//
//    @Test
//    void getMostCommentedPosts_ShouldReturnMostCommentedPosts() {
//        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());
//
//        when(postRepository.getMostCommentedPosts()).thenReturn(expectedPosts);
//
//        List<PostDtoTop> actualPosts = postService.getMostCommentedPosts();
//
//        assertEquals(expectedPosts, actualPosts);
//        verify(postRepository, times(1)).getMostCommentedPosts();
//    }
//
//    @Test
//    void getMostRecentPosts_ShouldReturnMostRecentPosts() {
//        List<PostDtoTop> expectedPosts = List.of(new PostDtoTop(), new PostDtoTop());
//
//        when(postRepository.getMostRecentPosts()).thenReturn(expectedPosts);
//
//        List<PostDtoTop> actualPosts = postService.getMostRecentPosts();
//
//        assertEquals(expectedPosts, actualPosts);
//        verify(postRepository, times(1)).getMostRecentPosts();
//    }
//
//    @Test
//    void getTotalPostsCount_ShouldReturnTotalPostsCount() {
//        int expectedCount = 10;
//
//        when(postRepository.getTotalPostsCount()).thenReturn(expectedCount);
//
//        int actualCount = postService.getTotalPostsCount();
//
//        assertEquals(expectedCount, actualCount);
//        verify(postRepository, times(1)).getTotalPostsCount();
//    }
//
//    @Test
//    void getAllUsersPosts_ShouldReturnUsersPosts() {
//        int userId = 1;
//        FilteredPostsOptions options = new FilteredPostsOptions(
//                10, null, null, null, "test title", null, null, null, null, null, null
//        );
//        List<Post> expectedPosts = List.of(new Post(), new Post());
//
//        when(postRepository.getAllUsersPosts(userId, options)).thenReturn(expectedPosts);
//
//        List<Post> actualPosts = postService.getAllUsersPosts(userId, options);
//
//        assertEquals(expectedPosts, actualPosts);
//        verify(postRepository, times(1)).getAllUsersPosts(userId, options);
//    }
//
//    @Test
//    void updatePost_ShouldUpdatePost_WhenNoDuplicatesAndUserIsAuthorized() {
//        User user = new User();
//        user.setId(1);
//        Post post = new Post();
//        post.setPostedBy(user);
//        post.setTitle("Unique Title");
//
//        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
//            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
//            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR")).thenAnswer(invocation -> null);
//
//            when(postRepository.getPostByTitle(post.getTitle())).thenThrow(new EntityNotFoundException("Post", "title", post.getTitle()));
//
//            postService.updatePost(user, post);
//
//            verify(postRepository, times(1)).updatePost(post);
//        }
//    }
//
//    @Test
//    void updatePost_ShouldThrowDuplicateEntityException_WhenTitleIsDuplicate() {
//        User user = new User();
//        user.setId(1);
//        Post post = new Post();
//        post.setPostedBy(user);
//        post.setTitle("Duplicate Title");
//
//        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
//            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
//            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR")).thenAnswer(invocation -> null);
//
//            when(postRepository.getPostByTitle(post.getTitle())).thenReturn(post);
//
//            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
//        }
//    }
//
//    @Test
//    void updatePost_ShouldThrowAuthorizationException_WhenUserIsNotAuthorized() {
//        User user = new User();
//        user.setId(1);
//        User anotherUser = new User();
//        anotherUser.setId(2);
//        Post post = new Post();
//        post.setPostedBy(anotherUser);
//        post.setTitle("Title");
//
//        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
//            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION"))
//                    .thenThrow(new AuthenticationException("AUTHORIZATION_EXCEPTION"));
//
//            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
//        }
//    }
//
//    @Test
//    void updatePost_ShouldThrowBlockedUserException_WhenUserIsBlocked() {
//        User user = new User();
//        user.setId(1);
//        user.setBlocked(true);
//        Post post = new Post();
//        post.setPostedBy(user);
//        post.setTitle("Title");
//
//        try (MockedStatic<PermissionHelper> mockedPermissionHelper = mockStatic(PermissionHelper.class)) {
//            mockedPermissionHelper.when(() -> PermissionHelper.isSameUser(user, post.getPostedBy(), "AUTHORIZATION_EXCEPTION")).thenAnswer(invocation -> null);
//            mockedPermissionHelper.when(() -> PermissionHelper.isBlocked(post.getPostedBy(), "BLOCKED_USER_EDIT_ERROR"))
//                    .thenThrow(new BlockedException("BLOCKED_USER_EDIT_ERROR"));
//
//            assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
//        }
//    }

    @Test
    public void createPost_Should_CreatePost_When_PostCreatorIsNotBlockedAndValidPostIsPassed() {
        Post postToBeCreated = TestHelpers.createMockPost1();
        postToBeCreated.setTitle("Different title");
        Post post = TestHelpers.createMockPost2();

        Mockito.when(postRepository.createPost(postToBeCreated))
                .thenReturn(postToBeCreated);

        postService.createPost(postToBeCreated);

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(postToBeCreated);
    }

    @Test
    public void createPost_Should_Throw_When_PostCreatorIsBlocked() {
        User postCreator = TestHelpers.createMockNoAdminUser();
        postCreator.setBlocked(true);
        Post postToBeCreated = TestHelpers.createMockPost1();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.createPost(postToBeCreated));
    }

    @Test
    public void getAllPosts_Should_Pass() {
        User userExecutingTheRequest = TestHelpers.createMockAdminUser();
        List<Post> posts = new ArrayList<>();
        posts.add(TestHelpers.createMockPost1());
        FilteredPostsOptions filterOptions = TestHelpers.createPostFilterOptions();

        Mockito.when(postRepository.getAllPosts(Mockito.any()))
                .thenReturn(posts);

        postService.getAllPosts(userExecutingTheRequest, filterOptions);

        Mockito.verify(postRepository, Mockito.times(1))
                .getAllPosts(filterOptions);
    }

    @Test
    public void likePost_Should_Throw_When_UserTryingToLikeThePostIsPostCreator() {
        User userTryingToLikeThePost = TestHelpers.createMockNoAdminUser();
        Post postToLike = TestHelpers.createMockPost1();
        postToLike.setPostedBy(userTryingToLikeThePost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.likePost(postToLike, userTryingToLikeThePost));
    }

    @Test
    public void likePost_Should_Throw_When_UserTryingToLikeThePostHasAlreadyLikedIt() {
        User userTryingToLikeThePost = TestHelpers.createMockNoAdminUser();
        //TODO:
//        userTryingToLikeThePost.setId(777);
        Post postToLike = TestHelpers.createMockPost1();

        Set<User> usersWhoLikedThePost = new HashSet<>();
        usersWhoLikedThePost.add(userTryingToLikeThePost);
        Set<User> usersWhoDislikedThePost = new HashSet<>();

        postToLike.setUsersWhoLikedPost(usersWhoLikedThePost);
        postToLike.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.likePost(postToLike, userTryingToLikeThePost));
    }

    @Test
    public void likePost_Should_Pass_When_UserTryingToLikeThePostHasNotLikedItBefore() {
        User userTryingToLikeThePost = TestHelpers.createMockNoAdminUser();
        //TODO:
//        userTryingToLikeThePost.setId(777);
        Post postToLike = TestHelpers.createMockPost1();
        Set<User> usersWhoLikedThePost = new HashSet<>();
        Set<User> usersWhoDislikedThePost = new HashSet<>();
        postToLike.setUsersWhoLikedPost(usersWhoLikedThePost);
        postToLike.setUsersWhoDislikedPost(usersWhoDislikedThePost);

//        Mockito.when(postRepository.updatePost(postToLike))
//                .thenReturn(postToLike);

        postService.likePost(postToLike, userTryingToLikeThePost);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToLike);
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_UserTryingToRemoveTheIsBlocked() {
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();
        User blockedUser = TestHelpers.createMockNoAdminUser();
        blockedUser.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.deleteTagFromPost(blockedUser, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotPostCreator() {
        User notPostCreator = TestHelpers.createMockNoAdminUser();
        //TODO
//        notPostCreator.setUserId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.deleteTagFromPost(notPostCreator, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotAdmin() {
        User notAdmin = TestHelpers.createMockNoAdminUser();
        //TODO
//        notAdmin.setUserId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.deleteTagFromPost(notAdmin, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_ThePostDoesNotContainTheTag() {
        User postCreator = TestHelpers.createMockAdminUser();
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        postToRemoveTagFrom.setPostTags(new HashSet<>());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.deleteTagFromPost(postCreator, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void removeTagFromPost_Should_Pass_When_AllArgumentsProvidedToTheMethodAreOk() {
        User postCreator = TestHelpers.createMockNoAdminUser();
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();
        Set<Tag> tags = new HashSet<>();
        tags.add(tagToRemoveFromPost);
        postToRemoveTagFrom.setPostTags(tags);

        Mockito.when(postRepository.getPostByTitle(Mockito.anyString()))
                .thenThrow(new EntityNotFoundException("Post", "title", postToRemoveTagFrom.getTitle()));;

        postService.deleteTagFromPost(postCreator, postToRemoveTagFrom, tagToRemoveFromPost);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToRemoveTagFrom);
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsBlocked(){
        Post postToDelete = TestHelpers.createMockPost1();
        User userWhoTriesToDelete = TestHelpers.createMockNoAdminUser();
        userWhoTriesToDelete.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.deletePost(userWhoTriesToDelete, postToDelete));
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsNotAdminOrSameUser(){
        Post postToDelete = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        //TODO
//        nonBlockedUser.setUserId(777);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.deletePost(nonBlockedUser, postToDelete));
    }

    @Test
    public void deletePost_Should_Pass_When_ArgumentsAreValid(){
        Post postToDelete = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        postToDelete.setPostedBy(nonBlockedUser);

        postService.deletePost(nonBlockedUser, postToDelete);

        Mockito.verify(postRepository,Mockito.times(1))
                .deletePost(postToDelete);
    }

    @Test
    public void getPostByTitle_Should_Return_When_PostExists(){
        Post post = TestHelpers.createMockPost1();

        Mockito.when(postRepository.getPostByTitle(Mockito.anyString()))
                .thenReturn(post);

        Post result = postService.getPostByTitle(post.getTitle());

        Assertions.assertEquals(1, result.getPostId());
        Assertions.assertEquals("Mock post title.", result.getTitle());
        Assertions.assertEquals("Mock post random content.", result.getContent());
        Assertions.assertEquals(1, result.getPostId());
    }

    @Test
    public void dislikePost_Should_Throw_When_UserAlreadyDislikedPost(){
        Post postToDislike = TestHelpers.createMockPost1();
        User userToDislike = TestHelpers.createMockNoAdminUser();
        Set<User> usersWhoDislikedPost = new HashSet<>();
        usersWhoDislikedPost.add(userToDislike);
        postToDislike.setUsersWhoDislikedPost(usersWhoDislikedPost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.dislikePost(postToDislike, userToDislike));
    }

    @Test
    public void dislikePost_Should_Pass_When_Valid(){
        Post postToDislike = TestHelpers.createMockPost1();
        User userToDislike = TestHelpers.createMockNoAdminUser();
        //TODO
//        userToDislike.setUserId(888);

        Mockito.when(postRepository.getPostById(postToDislike.getPostId()))
                .thenReturn(postToDislike);

        postService.likePost(postToDislike, userToDislike);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToDislike);
    }

    @Test
    public void dislikePost_Should_Throw_When_UserIsPostCreator(){
        Post postToBeDisliked = TestHelpers.createMockPost1();
        User userTryingToDislike = TestHelpers.createMockNoAdminUser();
        postToBeDisliked.setPostedBy(userTryingToDislike);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.dislikePost(postToBeDisliked, userTryingToDislike));
    }

    @Test
    public void updatePost_Throw_When_UserIsBlocked() {
        Post post = TestHelpers.createMockPost2();
        User creator = post.getPostedBy();
        creator.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.updatePost(creator, post));
    }

    @Test
    public void updatePost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        User userToUpdate = TestHelpers.createMockNoAdminUser();
        //TODO
//        userToUpdate.setUserId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.updatePost(userToUpdate, post));
    }

    @Test
    public void updatePost_CallRepository_When_ValidParametersPassed() {
        Post post = TestHelpers.createMockPost2();

        Mockito.when(postRepository.getPostById(post.getPostId()))
                .thenReturn(post);

        postService.updatePost(post.getPostedBy(), post);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(post);
    }

    @Test
    public void getPostById_Should_ReturnPost_When_MethodCalled() {
        Mockito.when(postRepository.getPostById(1))
                .thenReturn(TestHelpers.createMockPost1());

        Post post = postService.getPostById(1);

        Assertions.assertEquals(1, post.getPostId());
    }

    @Test
    public void addTagToPost_Throw_When_UserIsBlocked() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();
        User userToAddTag = post.getPostedBy();
        userToAddTag.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addTagToPost(userToAddTag, post, tag));
    }

    @Test
    public void addTagToPost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();
        User userToAddTag = TestHelpers.createMockNoAdminUser();
//        userToAddTag.setId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addTagToPost(userToAddTag, post, tag));
    }

    @Test
    public void addTagToPost_AddTag_When_ValidParametersPassed() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();

        Mockito.when(tagService.getTagById(tag.getId()))
                .thenReturn(tag);

        postService.addTagToPost(post.getPostedBy(), post, tag);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(post);
    }

    @Test
    public void dislikePost_Should_Throw_When_UserTryingToDislikeThePostIsPostCreator() {
        User user = TestHelpers.createMockNoAdminUser();
        Post post = TestHelpers.createMockPost1();
        post.setPostedBy(user);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.dislikePost(post, user));
    }

    @Test
    public void dislikePost_Should_Throw_When_UserTryingToDislikeThePostHasAlreadyDislikedIt() {
        User user = TestHelpers.createMockNoAdminUser();
        //TODO
//        user.setUserId(100);
        Post post = TestHelpers.createMockPost1();
        Set<User> usersWhoDislikedThePost = new HashSet<>();
        usersWhoDislikedThePost.add(user);

        post.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.dislikePost(post, user));
    }

    @Test
    public void dislikePost_Should_CallRepository_When_ValidArgumentsPassed() {
        User user = TestHelpers.createMockNoAdminUser();
        //TODO
//        user.setUserId(100);
        Post post = TestHelpers.createMockPost1();

        postService.dislikePost(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(post);
    }

    @Test
    public void getMostLikedPosts() {
        postService.getMostLikedPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostLikedPosts();
    }

    @Test
    public void getAllPostsCount_Should_Pass(){
        int expectedCount = 10;

        Mockito.when(postRepository.getTotalPostsCount())
                .thenReturn(expectedCount);

        postService.getTotalPostsCount();

        Mockito.verify(postRepository, Mockito.times(1))
                .getTotalPostsCount();
    }

    @Test
    public void getMostRecentlyCreatedPosts_Should_Pass(){
        List<PostDtoTop> returnedPosts = new ArrayList<>();

        Mockito.when(postRepository.getMostRecentPosts())
                .thenReturn(returnedPosts);

        postService.getMostRecentPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostRecentPosts();
    }

    @Test
    public void getPostsByCreator_Should_Call_Repository(){
        Post postToBeFound = TestHelpers.createMockPost1();
        User userToCreatePost = TestHelpers.createMockAdminUser();
        postToBeFound.setPostedBy(userToCreatePost);
        FilteredPostsOptions filterOptions = TestHelpers.createPostFilterOptions();

        postService.getAllUsersPosts(userToCreatePost.getId(), filterOptions);

        Mockito.verify(postRepository, Mockito.times(1))
                .getAllUsersPosts(userToCreatePost.getId(), filterOptions);
    }

    @Test
    public void getMostCommentedPosts_Should_Call_Repository(){
        postService.getMostCommentedPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostCommentedPosts();
    }
}

