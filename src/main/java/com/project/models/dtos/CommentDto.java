package com.project.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
    @NotEmpty(message = "Comment can't be empty.")
    @Size(min = 10, max = 500, message = "Comment should be between 10 and 500 symbols.")
    private String content;

    public CommentDto() {
    }

    public CommentDto(String content) {
        this.content = content;
    }

}
