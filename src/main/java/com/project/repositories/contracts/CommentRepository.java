package com.project.repositories.contracts;

import com.project.models.Comment;
import com.project.models.FilteredCommentsOptions;

import java.util.List;

public interface CommentRepository {
    List<Comment> getAllCommentsFromPost(int id, FilteredCommentsOptions filteredCommentsOptions);
    
    Comment getCommentByContent(String content);

    void createComment(Comment comment);

    Comment getCommentById(int id);

    void update(Comment comment);

    void deleteComment(Comment comment);
}
