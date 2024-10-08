package com.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment implements Comparable<Comment>{

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int commentId;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "creator")
    private User userId;

    @Column(name = "content_type")
    private String content;

    @Column(name = "comment_time_created")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post commentedPost;

    public Comment() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return commentId == comment.commentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }

    @Override
    public int compareTo(Comment o) {
        return Integer.compare(this.getCommentId(), o.getCommentId());
    }
}