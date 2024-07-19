package com.project.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment implements Comparable<Comment>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "creator")
    private User userId;

    @Column(name = "content_type")
    private String content;

    @Column(name = "comment_time_created")
    private LocalDateTime createdOn;

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
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Comment o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
