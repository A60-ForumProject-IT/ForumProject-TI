package com.project.services.contracts;

import com.project.models.Comment;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.User;

import java.util.List;

public interface CommentService {
    List<Comment> getAllCommentsFromPost(Post post, FilteredCommentsOptions filteredCommentsOptions);

    void createComment(Comment comment, User user);

    void updateComment(Comment comment, User user);

    void deleteComment(Comment comment, User user);

    Comment getCommentById(int commentId);


    List<Comment> getAllCommentsFromUser(int id, FilteredCommentsOptions filteredCommentsOptions);
}
