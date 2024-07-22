package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.PermissionHelper;
import com.project.models.Comment;
import com.project.models.Post;
import com.project.models.User;
import com.project.repositories.contracts.CommentRepository;
import com.project.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    public static final String UPDATE_THE_COMMENT_FOR_THIS_POST = "You are not the user, who is trying to update the comment for this post.";
    public static final String UNAUTHORIZED_DELETE_ERROR = "You are not authorized to delete this post";
    public static final String BLOCKED_USER_COMMENT_ERROR = "You are blocked and can't write comments!";

    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> getAllCommentsFromPost(int id) {
        try {
            return commentRepository.getAllCommentsFromPost(id);
        } catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Post", id);
        }
    }

    @Override
    public void createComment(Comment comment, User user) {
        PermissionHelper.isBlocked(user, BLOCKED_USER_COMMENT_ERROR);
        boolean duplicateExist = true;
        try {
            commentRepository.getCommentByContent(comment.getContent());
        } catch (EntityNotFoundException e){
            duplicateExist = false;
        }
        if (duplicateExist) {
            throw new DuplicateEntityException("Comment", "content", comment.getContent());
        }
        commentRepository.createComment(comment);
    }

    @Override
    public void updateComment(Comment comment, User user) {
        PermissionHelper.isSameUser(user, comment.getUserId(), UPDATE_THE_COMMENT_FOR_THIS_POST);
        PermissionHelper.isBlocked(user, BLOCKED_USER_COMMENT_ERROR);
        boolean duplicateExists = true;
        try {
            commentRepository.getCommentByContent(comment.getContent());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Comment", "content", comment.getContent());
        }
        commentRepository.update(comment);
    }

    @Override
    public void deleteComment(Comment comment, User user) {
        PermissionHelper.isAdminOrSameUser(user, comment.getUserId(), UNAUTHORIZED_DELETE_ERROR);
        commentRepository.deleteComment(comment);
    }

    @Override
    public Comment getCommentById(int commentId) {
        return commentRepository.getCommentById(commentId);
    }
}
