package com.project.services;

import com.project.exceptions.*;
import com.project.helpers.TestHelpers;
import com.project.models.*;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
import com.project.repositories.contracts.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void createPost_Should_CreatePost_When_PostCreatorIsNotBlockedAndValidPostIsPassed() {
        Post postToBeCreated = TestHelpers.createMockPost1();
        postToBeCreated.setTitle("Different title");

        Mockito.when(postRepository.getPostByTitle(postToBeCreated.getTitle()))
                .thenThrow(new EntityNotFoundException("Post", "title", postToBeCreated.getTitle()));

        postService.createPost(postToBeCreated);

        Mockito.verify(postRepository, Mockito.times(1))
                .createPost(postToBeCreated);
    }

    @Test
    public void createPost_Should_Throw_When_PostWithSameNameExists() {
        Post post = TestHelpers.createMockPost1();

        Mockito.when(postRepository.getPostByTitle(post.getTitle()))
                .thenReturn(post);

        Assertions.assertThrows(DuplicateEntityException.class, () -> postService.createPost(post));
    }

    @Test
    public void createPost_Should_Throw_When_PostCreatorIsBlocked() {
        User postCreator = TestHelpers.createMockNoAdminUser();
        postCreator.setBlocked(true);
        Post postToBeCreated = TestHelpers.createMockPost1();
        postToBeCreated.setPostedBy(postCreator);

        Assertions.assertThrows(BlockedException.class,
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

        postService.getAllPosts(filterOptions);

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
        userTryingToLikeThePost.setId(777);
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
        userTryingToLikeThePost.setId(777);
        Post postToLike = TestHelpers.createMockPost1();
        Set<User> usersWhoLikedThePost = new HashSet<>();
        Set<User> usersWhoDislikedThePost = new HashSet<>();
        postToLike.setUsersWhoLikedPost(usersWhoLikedThePost);
        postToLike.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        postService.likePost(postToLike, userTryingToLikeThePost);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(postToLike);
    }

    @Test
    public void likePost_Should_DecrementDislikeCount_When_UserPreviouslyDislikedPost() {
        User user = TestHelpers.createMockAdminUser();
        User postOwner = TestHelpers.createMockNoAdminUser();
        Post post = TestHelpers.createMockPost2();
        post.setPostedBy(postOwner);
        post.setLikes(0);
        post.setDislikes(1);

        Set<User> usersWhoLikedPost = new HashSet<>();
        post.setUsersWhoLikedPost(usersWhoLikedPost);

        Set<User> usersWhoDislikedPost = new HashSet<>();
        usersWhoDislikedPost.add(user);
        post.setUsersWhoDislikedPost(usersWhoDislikedPost);

        postService.likePost(post, user);

        assertEquals(1, post.getLikes());
        assertEquals(0, post.getDislikes());
        assertTrue(post.getUsersWhoLikedPost().contains(user));
        assertFalse(post.getUsersWhoDislikedPost().contains(user));
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotPostCreator() {
        User notPostCreator = TestHelpers.createMockNoAdminUser();
        notPostCreator.setId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.deleteTagFromPost(notPostCreator, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_UserTryingToRemoveIsNotAdmin() {
        User notAdmin = TestHelpers.createMockNoAdminUser();
        notAdmin.setId(777);
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> postService.deleteTagFromPost(notAdmin, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_ThePostDoesNotContainTheTag() {
        User postCreator = TestHelpers.createMockAdminUser();
        Post postToRemoveTagFrom = TestHelpers.createMockPost1();
        Tag tagToRemoveFromPost = TestHelpers.createMockTag();

        postToRemoveTagFrom.setPostTags(new HashSet<>());

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.deleteTagFromPost(postCreator, postToRemoveTagFrom, tagToRemoveFromPost));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void deleteTagFromPost_Should_Pass_When_ThePostDoesNotContainTheTag() {
        User user = TestHelpers.createMockAdminUser();
        Post post = TestHelpers.createMockPost1();
        post.setPostedBy(user);

        Tag existingTag = new Tag();
        existingTag.setTag("Existing Tag");

        Set<Tag> tags = new HashSet<>();
        tags.add(existingTag);
        post.setPostTags(tags);

        when(tagRepository.getTagByName(existingTag.getTag()))
                .thenThrow(new EntityNotFoundException("Tag", "name", existingTag.getTag()));

        postService.deleteTagFromPost(user, post, existingTag);

        assertFalse(post.getPostTags().contains(existingTag));
        verify(postRepository, times(1)).deleteTagFromPost(post);
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsBlocked(){
        Post postToDelete = TestHelpers.createMockPost1();
        User userWhoTriesToDelete = TestHelpers.createMockNoAdminUser();
        userWhoTriesToDelete.setBlocked(true);

        Assertions.assertThrows(BlockedException.class,
                ()-> postService.deletePost(userWhoTriesToDelete, postToDelete));
    }

    @Test
    public void deletePost_Should_Throw_When_UserIsNotAdminOrSameUser(){
        Post postToDelete = TestHelpers.createMockPost1();
        User nonBlockedUser = TestHelpers.createMockNoAdminUser();
        nonBlockedUser.setId(777);

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
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void dislikePost_Should_Pass_When_Valid(){
        Post postToDislike = TestHelpers.createMockPost1();
        User userToDislike = TestHelpers.createMockNoAdminUser();
        userToDislike.setId(888);

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

        Assertions.assertThrows(BlockedException.class,
                ()-> postService.updatePost(creator, post));
    }

    @Test
    public void updatePost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        User userToUpdate = TestHelpers.createMockNoAdminUser();
        userToUpdate.setId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.updatePost(userToUpdate, post));
    }

    @Test
    public void updatePost_CallRepository_When_ValidParametersPassed() {
        Post post = TestHelpers.createMockPost2();

        Mockito.when(postRepository.getPostByTitle(post.getTitle()))
                .thenThrow(new EntityNotFoundException("Post", "title", post.getTitle()));

        postService.updatePost(post.getPostedBy(), post);

        Mockito.verify(postRepository, Mockito.times(1))
                .updatePost(post);
    }

    @Test
    public void updatePost_Should_Throw_When_PostWithSameNameExists() {
        Post post = TestHelpers.createMockPost1();
        User user = TestHelpers.createMockAdminUser();
        post.setPostedBy(user);
        post.setTitle("Mock");

        Mockito.when(postRepository.getPostByTitle(post.getTitle()))
                .thenReturn(post);

        Assertions.assertThrows(DuplicateEntityException.class, () -> postService.updatePost(user, post));
    }

    @Test
    public void getPostById_Should_ReturnPost_When_MethodCalled() {
        Mockito.when(postRepository.getPostById(1))
                .thenReturn(TestHelpers.createMockPost1());

        Post post = postService.getPostById(1);

        Assertions.assertEquals(1, post.getPostId());
    }

    @Test
    public void addTagToPost_Throw_When_UserIsNotAdminAndNotCreator() {
        Post post = TestHelpers.createMockPost2();
        Tag tag = TestHelpers.createMockTag();
        User userToAddTag = TestHelpers.createMockNoAdminUser();
        userToAddTag.setId(100);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                ()-> postService.addTagToPost(userToAddTag, post, tag));
    }

    @Test
    public void dislikePost_Should_DecrementLikeCount_When_UserPreviouslyLikedPost() {
        User user = TestHelpers.createMockAdminUser();
        User postOwner = TestHelpers.createMockNoAdminUser();
        Post post = TestHelpers.createMockPost2();
        post.setPostedBy(postOwner);
        post.setLikes(1);
        post.setDislikes(0);

        Set<User> usersWhoLikedPost = new HashSet<>();
        usersWhoLikedPost.add(user);
        post.setUsersWhoLikedPost(usersWhoLikedPost);

        Set<User> usersWhoDislikedPost = new HashSet<>();
        post.setUsersWhoDislikedPost(usersWhoDislikedPost);

        postService.dislikePost(post, user);

        assertEquals(0, post.getLikes());
        assertEquals(1, post.getDislikes());
        assertFalse(post.getUsersWhoLikedPost().contains(user));
        assertTrue(post.getUsersWhoDislikedPost().contains(user));
        verify(postRepository, times(1)).updatePost(post);
    }

    @Test
    public void addTagToPost_Should_Throw_When_TagAlreadyExistInPost() {
        Post post = TestHelpers.createMockPost2();
        User user = TestHelpers.createMockAdminUser();
        post.setPostedBy(user);
        Tag tag = TestHelpers.createMockTag();
        tag.setTag("Existing Tag");

        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        post.setPostTags(tags);

        Assertions.assertThrows(DuplicateEntityException.class, () -> postService.addTagToPost(user, post, tag));
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
        user.setId(100);
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
        user.setId(100);
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

    @Test
    public void addTagToPost_Should_CreateAndAddNewTag_When_TagDoesNotExist() {
        User user = TestHelpers.createMockAdminUser();
        Post post = TestHelpers.createMockPost1();
        post.setPostedBy(user);
        post.setPostTags(new HashSet<>());

        Tag newTag = new Tag();
        newTag.setTag("New Tag");

        when(tagRepository.getTagByName(newTag.getTag())).thenThrow(new EntityNotFoundException("Tag", "name", newTag.getTag()));
        doNothing().when(tagRepository).createTag(newTag);

        postService.addTagToPost(user, post, newTag);

        assertTrue(post.getPostTags().contains(newTag));
        verify(tagRepository, times(1)).createTag(newTag);
        verify(postRepository, times(1)).addTagToPost(post);
    }
}

