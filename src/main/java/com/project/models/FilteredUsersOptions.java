package com.project.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class FilteredUsersOptions {

    private Optional<String> username;
    private Optional<String> email;
    private Optional<String> firstName;

    public FilteredUsersOptions(String username,
                                String email,
                                String firstName) {
        this.username = Optional.ofNullable(username);
        this.email = Optional.ofNullable(email);
        this.firstName = Optional.ofNullable(firstName);
    }
}
