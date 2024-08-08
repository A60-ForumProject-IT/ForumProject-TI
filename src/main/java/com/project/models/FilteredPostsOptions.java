package com.project.models;

import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Optional;

@Getter
public class FilteredPostsOptions {
    private Optional<Integer> minLikes;
    private Optional<Integer> minDislikes;
    private Optional<Integer> maxLikes;
    private Optional<Integer> maxDislikes;
    private Optional<String> title;
    private Optional<String> content;
    private Optional<String> postedBy;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;
    private Optional<LocalDate> createdBefore;
    private Optional<LocalDate> createdAfter;

    public FilteredPostsOptions(Integer minLikes,
                                Integer minDislikes,
                                Integer maxLikes,
                                Integer maxDislikes,
                                String title,
                                String content,
                                LocalDate createdBefore,
                                LocalDate createdAfter,
                                String postedBy,
                                String sortBy,
                                String sortOrder) {
        this.minLikes = Optional.ofNullable(minLikes);
        this.minDislikes = Optional.ofNullable(minDislikes);
        this.maxLikes = Optional.ofNullable(maxLikes);
        this.maxDislikes = Optional.ofNullable(maxDislikes);
        this.title = Optional.ofNullable(title);
        this.content = Optional.ofNullable(content);
        this.createdBefore = Optional.ofNullable(createdBefore);
        this.createdAfter = Optional.ofNullable(createdAfter);
        this.postedBy = Optional.ofNullable(postedBy);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
