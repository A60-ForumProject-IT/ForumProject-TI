package com.project.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class FilteredUsersOptions {

    private Optional<String> username;
    private Optional<String> firstName;
    private Optional<String> email;
    private Optional<String> sortBy;
    private Optional<String> sortOrder;

    public FilteredUsersOptions(String username,
                                String firstName,
                                String email,
                                String sortBy,
                                String sortOrder
    ) {
        this.username = Optional.ofNullable(username);
        this.firstName = Optional.ofNullable(firstName);
        this.email = Optional.ofNullable(email);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }
}
