package com.project.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
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


}
