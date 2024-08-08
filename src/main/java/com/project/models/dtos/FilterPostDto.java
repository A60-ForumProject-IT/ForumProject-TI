package com.project.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterPostDto {
    private Integer minLikes;
    private Integer maxLikes;
    private Integer minDislikes;
    private Integer maxDislikes;
    private String title;
    private String content;
    private String postedBy;
    private String sortBy;
    private String sortOrder;
    private String createdBefore;
    private String createdAfter;

    public FilterPostDto() {
    }
}
