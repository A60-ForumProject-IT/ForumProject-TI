package com.project.models.dtos;

import jakarta.validation.constraints.NotEmpty;

public class LoginDto {

    @NotEmpty(message = "Username can't be empty")
    private String username;
    @NotEmpty(message = "Password can't be empty")
    private String password;

    public LoginDto() {
    }

    public @NotEmpty(message = "Username can't be empty") String getUsername() {
        return username;
    }

    public void setUsername(@NotEmpty(message = "Username can't be empty") String username) {
        this.username = username;
    }

    public @NotEmpty(message = "Password can't be empty") String getPassword() {
        return password;
    }

    public void setPassword(@NotEmpty(message = "Password can't be empty") String password) {
        this.password = password;
    }
}
