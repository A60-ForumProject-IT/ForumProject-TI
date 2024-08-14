package com.project.repositories.contracts;

import com.project.models.Comment;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAllCommentsFromPost(Post post, FilteredCommentsOptions filteredCommentsOptions);

    List<Comment> getAllCommentsFromUser(int userId);
    
    Comment getCommentByContent(String content);

    void createComment(Comment comment);

    Comment getCommentById(int id);

    void update(Comment comment);

    void deleteComment(Comment comment);

    List<Comment> getAllUserComments(int userId);
}
