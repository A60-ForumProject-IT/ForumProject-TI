package com.project.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto {
    @NotEmpty(message = "Tag name cannot be null!")
    @Size(min = 3, max = 20, message = "Tag name should be between 3 and 20 characters!")
    @Pattern(regexp = "^#.*", message = "Tag name must start with '#'")
    private String tagName;

    public TagDto() {
    }

    public TagDto(String tagName) {
        this.tagName = tagName;
    }
}
