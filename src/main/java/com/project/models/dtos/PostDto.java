package com.project.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostDto {

    @NotNull
    @Size(min = 16, max = 64, message = "Title must be between 16 and 64 symbols.")
    private String title;

    @NotNull
    @Size(min = 32, max = 8192, message = "Content must be between 32 and 8192 symbols.")
    private String content;

    public PostDto() {
    }

    public PostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
