package com.project.models.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotNull(message = "Password can't be empty.")
    @Size(min = 8)
    private String password;

    @Email(message = "Email is invalid.")
    private String email;

    @NotNull(message = "First name name can't be empty.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32.")
    private String firstName;

    @NotNull(message = "Last name can't be empty.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32.")
    private String lastName;

    public UserDto() {}

    public UserDto( String password, String email, String firstName, String lastName) {
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
