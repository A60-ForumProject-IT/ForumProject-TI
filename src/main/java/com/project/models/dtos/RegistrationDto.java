package com.project.models.dtos;

import com.project.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {

    @NotEmpty(message = "Username can't be empty")
    @Size(min = 4, max = 32, message = "Username should be between 4 and 32.")
    private String username;

    @NotEmpty(message = "Password can't be empty.")
    @Size(min = 8, max = 30)
    private String password;

    @NotEmpty(message = "Confirmation password can't be empty.")
    @Size(min = 8, max = 30)
    private String confirmPassword;

    @NotEmpty(message = "Email can't be empty.")
    private String email;

    @NotEmpty(message = "First name name can't be empty.")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32.")
    private String firstName;

    @NotEmpty(message = "Last name can't be empty.")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32.")
    private String lastName;


    public RegistrationDto() {
    }
    public RegistrationDto(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
