package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.PermissionHelper;
import com.project.models.Comment;
import com.project.models.User;
import com.project.repositories.contracts.CommentRepository;
import com.project.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    public static final String COMMENT_FOR_THIS_POST = "You are not the user, who is trying to create the comment for this post.";
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
        PermissionHelper.isSameUser(user, comment.getUserId(), COMMENT_FOR_THIS_POST);
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
}
