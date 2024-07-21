package com.project.services.contracts;

import com.project.models.Comment;
import com.project.models.User;

import java.util.List;

public interface CommentService {
    List<Comment> getAllCommentsFromPost(int id);

    void createComment(Comment comment, User user);
}
