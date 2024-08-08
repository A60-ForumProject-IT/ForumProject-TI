package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.TestHelpers;
import com.project.models.Comment;
import com.project.repositories.contracts.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.project.models.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTests {

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    CommentServiceImpl commentService;


    @Test
    public void getById_Should_ReturnComment_When_MatchExist() {
        Comment comment = TestHelpers.createMockComment1();

        Mockito.when(commentRepository.getCommentById(1))
                .thenReturn(comment);

        Comment result = commentService.getCommentById(1);

        Assertions.assertEquals(1, result.getCommentId());
        Assertions.assertEquals("Mock comment random content.", result.getContent());
    }

    @Test
    public void updateComment_Should_Throw_When_CommentExist() {
        Comment existingComment = TestHelpers.createMockComment1();
        existingComment.setContent("Content");
        User user = TestHelpers.createMockAdminUser();
        existingComment.setUserId(user);

        Comment commentToUpdate = TestHelpers.createMockComment1();
        commentToUpdate.setContent("Content");
        commentToUpdate.setUserId(user);

        Mockito.when(commentRepository.getCommentByContent(existingComment.getContent()))
                .thenReturn(existingComment);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> commentService.updateComment(commentToUpdate, user));
    }

    @Test
    public void updateComment_Should_Throw_When_UserIsNotAdminOrSameUser() {
        Comment commentToUpdate = TestHelpers.createMockComment1();
        User blockedUser = TestHelpers.createMockNoAdminUser();
        blockedUser.setId(777);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> commentService.updateComment(commentToUpdate, blockedUser));
    }

    @Test
    public void updateComment_Should_Pass_When_UserIsValid() {
        Comment comment = TestHelpers.createMockComment1();
        User user = TestHelpers.createMockNoAdminUser();

        Mockito.when(commentRepository.getCommentByContent(comment.getContent()))
                .thenThrow(EntityNotFoundException.class);

        commentService.updateComment(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .update(comment);
    }

    @Test
    public void deleteComment_Should_DeleteComment_When_UserIsNotBlocked() {
        Comment comment = TestHelpers.createMockComment1();
        User user = TestHelpers.createMockAdminUser();

        commentService.deleteComment(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .deleteComment(comment);
    }


    @Test
    public void getAllComments_Should_CallRepository() {
        Post post = TestHelpers.createMockPost1();
        Comment comment = TestHelpers.createMockComment1();
        post.getComments().add(comment);

        FilteredCommentsOptions commentFilterOptions = TestHelpers.createCommentFilterOptions();

        commentService.getAllCommentsFromPost(post, commentFilterOptions);

        Mockito.verify(commentRepository, Mockito.times(1))
                .getAllCommentsFromPost(post, commentFilterOptions);
    }

    @Test
    public void getAllComments_Should_Throw_When_PostHasNoComments() {
        Post post = TestHelpers.createMockPost1();
        FilteredCommentsOptions commentFilterOptions = TestHelpers.createCommentFilterOptions();

        Mockito.when(commentRepository.getAllCommentsFromPost(post, commentFilterOptions))
                .thenThrow(new EntityNotFoundException("Comment", "postId", String.valueOf(post.getPostId())));

        Assertions.assertThrows(EntityNotFoundException.class, () -> commentService.getAllCommentsFromPost(post, commentFilterOptions));
    }

    @Test
    public void createComment_Should_CallRepository_When_ArgumentsAreValid() {
        Comment comment = TestHelpers.createMockComment1();
        User user = TestHelpers.createMockNoAdminUser();

        Mockito.when(commentRepository.getCommentByContent(comment.getContent()))
                        .thenThrow(EntityNotFoundException.class);

        commentService.createComment(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .createComment(comment);
    }

    @Test
    public void createComment_Should_Throw_When_CommentContentExists(){
        Comment comment = TestHelpers.createMockComment1();
        User user = TestHelpers.createMockNoAdminUser();
        comment.setCreatedOn(LocalDateTime.now());
        comment.setUserId(user);

        Mockito.when(commentRepository.getCommentByContent(comment.getContent()))
                .thenReturn(comment);

        Assertions.assertThrows(DuplicateEntityException.class, () -> commentService.createComment(comment, user));
    }
}
