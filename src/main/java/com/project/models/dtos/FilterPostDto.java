package com.project.models.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class FilterPostDto {
    private Integer minLikes;
    private Integer minDislikes;
    private Integer maxLikes;
    private Integer maxDislikes;
    private String title;
    private String tagName;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdBefore;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAfter;
    private String postedBy;
    private String sortBy;
    private String sortOrder;

    public FilterPostDto() {
    }

    public void sanitize() {
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }
        if (tagName != null && tagName.trim().isEmpty()) {
            tagName = null;
        }
        if (content != null && content.trim().isEmpty()) {
            content = null;
        }
        if (postedBy != null && postedBy.trim().isEmpty()) {
            postedBy = null;
        }
        if (sortBy != null && sortBy.trim().isEmpty()) {
            sortBy = null;
        }
        if (sortOrder != null && sortOrder.trim().isEmpty()) {
            sortOrder = null;
        }
    }
}
