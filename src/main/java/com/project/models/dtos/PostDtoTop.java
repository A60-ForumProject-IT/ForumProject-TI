package com.project.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDtoTop {
    private String title;
    private String content;
    private LocalDateTime createdOn;
    private Integer likes;
    private Integer dislikes;
    private Long commentsCount;

    public PostDtoTop(String title, String content, LocalDateTime createdOn, Integer likes, Integer dislikes, Long commentsCount) {
        this.title = title;
        this.content = content;
        this.createdOn = createdOn;
        this.likes = likes;
        this.dislikes = dislikes;
        this.commentsCount = commentsCount;
    }
}
