package com.project.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterUserDto {
    private String username;
    private String firstName;
    private String email;
    private String sortBy;
    private String sortOrder;

    public FilterUserDto() {
    }
}
