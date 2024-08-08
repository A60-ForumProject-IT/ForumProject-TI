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
    private String content;
    private String postedBy;
    private String sortBy;
    private String sortOrder;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdBefore;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAfter;

    public FilterPostDto() {
    }
}
