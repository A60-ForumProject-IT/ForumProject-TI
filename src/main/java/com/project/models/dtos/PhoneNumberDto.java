package com.project.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneNumberDto {
    @NotNull(message = "Phone number can't be empty.")
    @Size(min = 10, max = 500, message = "Phone number should be between 10 and 13 digits.")
    private String phoneNumber;

    public PhoneNumberDto() {
    }

    public PhoneNumberDto(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
