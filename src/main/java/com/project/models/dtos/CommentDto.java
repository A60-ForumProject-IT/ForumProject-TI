package com.project.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    @NotNull(message = "Comment can't be empty.")
    @Size(min = 10, max = 500, message = "Comment should be between 10 and 500 symbols.")
    private String comment;

    public CommentDto() {
    }

    public CommentDto(String comment) {
        this.comment = comment;
    }

}
