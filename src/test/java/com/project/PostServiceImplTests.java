package com.project;

import com.project.exceptions.*;
import com.project.helpers.PermissionHelper;
import com.project.helpers.TestHelpers;
import com.project.models.*;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.repositories.contracts.TagRepository;
import com.project.services.PostServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
        User userExecutingTheRequest = TestHelpers.createMockNoAdminUser();
        List<Post> posts = new ArrayList<>();
        posts.add(TestHelpers.createMockPost1());
        PostFilterOptions filterOptions = TestHelpers.createPostFilterOptions();

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
        postToLike.setCreatedBy(userTryingToLikeThePost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.likePost(postToLike, userTryingToLikeThePost));
    }

    @Test
    public void likePost_Should_Throw_When_UserTryingToLikeThePostHasAlreadyLikedIt() {
        User userTryingToLikeThePost = TestHelpers.createMockNoAdminUser();
        userTryingToLikeThePost.setUserId(777);
        Post postToLike = TestHelpers.createMockPost1();
        Set<User> usersWhoLikedThePost = new TreeSet<>();
        usersWhoLikedThePost.add(userTryingToLikeThePost);
        Set<User> usersWhoDislikedThePost = new TreeSet<>();
        postToLike.setUsersWhoLikedPost(usersWhoLikedThePost);
        postToLike.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.likePost(postToLike, userTryingToLikeThePost));
    }

    @Test
    public void likePost_Should_Pass_When_UserTryingToLikeThePostHasNotLikedItBefore() {
        User userTryingToLikeThePost = TestHelpers.createMockNoAdminUser();
        userTryingToLikeThePost.setUserId(777);
        Post postToLike = TestHelpers.createMockPost1();
        Set<User> usersWhoLikedThePost = new TreeSet<>();
        Set<User> usersWhoDislikedThePost = new TreeSet<>();
        postToLike.setUsersWhoLikedPost(usersWhoLikedThePost);
        postToLike.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        Mockito.when(postRepository.updatePost(postToLike))
                .thenReturn(postToLike);

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
                () -> postService.removeTagFromPost(postToRemoveTagFrom, tagToRemoveFromPost, blockedUser));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotPostCreator() {
        User notPostCreator = TestHelpers.createMockNoAdminUser();
        notPostCreator.setUserId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.removeTagFromPost(postToRemoveTagFrom, tagToRemoveFromPost, notPostCreator));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotAdmin() {
        User notAdmin = TestHelpers.createMockNoAdminUser();
        notAdmin.setUserId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.removeTagFromPost(postToRemoveTagFrom, tagToRemoveFromPost, notAdmin));
    }

    @Test
    public void removeTagFromPost_Should_Throw_When_ThePostDoesNotContainTheTag() {
        User postCreator = TestHelpers.createMockNoAdminUser();
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();
        postToRemoveTagFrom.setTags(new TreeSet<Tag>());

        Assertions.assertThrows(InvalidUserInputException.class,
                () -> postService.removeTagFromPost(postToRemoveTagFrom, tagToRemoveFromPost, postCreator));
    }

    @Test
    public void removeTagFromPost_Should_Pass_When_AllArgumentsProvidedToTheMethodAreOk() {
        User postCreator = TestHelpers.createMockNoAdminUser();
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();
        Set<Tag> tags = new TreeSet<>();
        tags.add(tagToRemoveFromPost);
        postToRemoveTagFrom.setTags(tags);

        Mockito.when(postRepository.updatePost(postToRemoveTagFrom))
                .thenReturn(postToRemoveTagFrom);

        postService.removeTagFromPost(postToRemoveTagFrom, tagToRemoveFromPost, postCreator);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToRemoveTagFrom);
    }

    @Test
    public void removeCommentFromPost_Should_Throw_When_UserIsBlocked() {
        Post postToRemoveCommentFrom = TestHelpers.createMockPost1();
        Comment commentToBeRemoved = TestHelpers.createMockComment1();
        User blockedUser = TestHelpers.createMockNoAdminUser();
        blockedUser.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.removeCommentFromPost(
                        postToRemoveCommentFrom,
                        commentToBeRemoved,
                        blockedUser
                ));
    }


    @Test
    public void removeCommentFromPost_Should_Throw_When_UserTryingToRemoveCommentIsNotItsCreatorNorAdmin() {
        Post postToRemoveCommentFrom = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        nonBlockedUser.setUserId(777);
        Comment commentToRemove = TestHelpers.createMockComment1();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.removeCommentFromPost(postToRemoveCommentFrom, commentToRemove, nonBlockedUser));
    }

    @Test
    public void removeCommentFromPost_Should_Throw_When_TheCommentDoesNotBelongToThisPost() {
        Post postToRemoveCommentFrom = TestHelpers.createMockPost1();
        Set<Comment> comments = new TreeSet<>();
        postToRemoveCommentFrom.setRelatedComments(comments);
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        Comment commentToRemove = TestHelpers.createMockComment1();

        Assertions.assertThrows(InvalidOperationException.class,
                () -> postService.removeCommentFromPost(postToRemoveCommentFrom, commentToRemove, nonBlockedUser));
    }

    @Test
    public void removeCommentFromPost_Should_Pass_When_AllArgumentsAreOk() {
        Comment commentToRemove = TestHelpers.createMockComment1();
        Post postToRemoveCommentFrom = TestHelpers.createMockPost1();
        Set<Comment> relatedComments = new TreeSet<>();
        relatedComments.add(commentToRemove);
        postToRemoveCommentFrom.setRelatedComments(relatedComments);
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();

        Mockito.doNothing().when(commentService).deleteComment(commentToRemove);

        Mockito.when(postRepository.updatePost(postToRemoveCommentFrom))
                .thenReturn(postToRemoveCommentFrom);

        postService.removeCommentFromPost(postToRemoveCommentFrom, commentToRemove, nonBlockedUser);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToRemoveCommentFrom);
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsBlocked(){
        Post postToDelete = TestHelpers.createMockPost1();
        User userWhoTriesToDelete = TestHelpers.createMockNoAdminUser();
        userWhoTriesToDelete.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.deletePost(postToDelete, userWhoTriesToDelete));
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsNotAdminOrSameUser(){
        Post postToDelete = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        nonBlockedUser.setUserId(777);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.deletePost(postToDelete, nonBlockedUser));
    }

    @Test
    public void deletePost_Should_Pass_When_ArgumentsAreValid(){
        Post postToDelete = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        postToDelete.setCreatedBy(nonBlockedUser);

        postService.deletePost(postToDelete, nonBlockedUser);

        Mockito.verify(postRepository,Mockito.times(1))
                .softDeletePost(postToDelete);
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
        userToDislike.setUserId(888);

        Mockito.when(postRepository.updatePost(postToDislike))
                .thenReturn(postToDislike);

        postService.likePost(postToDislike, userToDislike);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToDislike);
    }

    @Test
    public void dislikePost_Should_Throw_When_UserIsPostCreator(){
        Post postToBeDisliked = TestHelpers.createMockPost1();
        User userTryingToDislike = TestHelpers.createMockNoAdminUser();
        postToBeDisliked.setCreatedBy(userTryingToDislike);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.dislikePost(postToBeDisliked, userTryingToDislike));
    }

    @Test
    public void addCommentToPost_Should_Throw_When_UserIsBlocked(){
        Post postToAddComment = TestHelpers.createMockPost1();
        User userToAddCommentToPost = TestHelpers.createMockNoAdminUser();
        Comment comment = TestHelpers.createMockComment1();
        userToAddCommentToPost.setBlocked(true);
        postToAddComment.setCreatedBy(userToAddCommentToPost);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addCommentToPost(postToAddComment, comment, userToAddCommentToPost));
    }

    @Test
    public void addCommentToPost_Should_Pass_When_ArgumentsAreValid(){
        Post postToAddComment = TestHelpers.createMockPost1();
        User userToAddCommentToPost = TestHelpers.createMockNoAdminUser();
        Comment comment = TestHelpers.createMockComment1();

        Mockito.when(postRepository.updatePost(postToAddComment))
                .thenReturn(postToAddComment);

        postService.addCommentToPost(postToAddComment, comment, userToAddCommentToPost);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToAddComment);
    }

    @Test
    public void updatePost_Throw_When_UserIsBlocked() {
        Post post = TestHelpers.createMockPost2();
        User creator = post.getCreatedBy();
        creator.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.updatePost(post, creator));
    }

    @Test
    public void updatePost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        User userToUpdate = TestHelpers.createMockNoAdminUser();
        userToUpdate.setUserId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.updatePost(post, userToUpdate));
    }

    @Test
    public void updatePost_CallRepository_When_ValidParametersPassed() {
        Post post = TestHelpers.createMockPost2();

        Mockito.when(postRepository.updatePost(post))
                .thenReturn(post);

        postService.updatePost(post, post.getCreatedBy());

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
        User userToAddTag = post.getCreatedBy();
        userToAddTag.setBlocked(true);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addTagToPost(post, tag,userToAddTag));
    }

    @Test
    public void addTagToPost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();
        User userToAddTag = TestHelpers.createMockNoAdminUser();
        userToAddTag.setUserId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addTagToPost(post, tag,userToAddTag));
    }

    @Test
    public void addTagToPost_AddTag_When_ValidParametersPassed() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();

        Mockito.when(tagService.createTag(tag,post.getCreatedBy()))
                .thenReturn(tag);

        postService.addTagToPost(post,tag,post.getCreatedBy());

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(post);
    }

    @Test
    public void getAllCommentsRelatedToPost_ReturnsList_WhenCalled() {
        Post post = TestHelpers.createMockPost1();

        List<Comment> listComments = postService.getAllCommentsRelatedToPost(post);

        Assertions.assertNotNull(listComments);
    }

    @Test
    public void dislikePost_Should_Throw_When_UserTryingToDislikeThePostIsPostCreator() {
        User user = TestHelpers.createMockNoAdminUser();
        Post post = TestHelpers.createMockPost1();
        post.setCreatedBy(user);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.dislikePost(post, user));
    }

    @Test
    public void dislikePost_Should_Throw_When_UserTryingToDislikeThePostHasAlreadyDislikedIt() {
        User user = TestHelpers.createMockNoAdminUser();
        user.setUserId(100);
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
        user.setUserId(100);
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
        Long returnValue = 1L;
        Mockito.when(postRepository.getAllPostsCount())
                .thenReturn(returnValue);

        postService.getAllPostsCount();

        Mockito.verify(postRepository, Mockito.times(1))
                .getAllPostsCount();
    }

    @Test
    public void getMostRecentlyCreatedPosts_Should_Pass(){
        List<Post> returnedPosts = new ArrayList<>();
        Mockito.when(postRepository.getMostRecentlyCreatedPosts())
                .thenReturn(returnedPosts);

        postService.getMostRecentlyCreatedPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostRecentlyCreatedPosts();
    }

    @Test
    public void getPostsByCreator_Should_Call_Repository(){
        Post postToBeFound = TestHelpers.createMockPost1();
        User userToCreatePost = TestHelpers.createMockAdminUser();
        postToBeFound.setCreatedBy(userToCreatePost);

        postService.getPostsByCreator(userToCreatePost);

        Mockito.verify(postRepository, Mockito.times(1))
                .getPostsByCreator(userToCreatePost);
    }

    @Test
    public void getMostCommentedPosts_Should_Call_Repository(){
        postService.getMostCommentedPosts();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostCommentedPosts();
    }
}

