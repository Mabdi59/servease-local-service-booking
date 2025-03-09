package com.servease.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterUserDto {

    @NotEmpty(message = "Username cannot be empty.")
    private String username;

    @NotEmpty(message = "Full name is required.")
    private String fullName;

    @Email(message = "Invalid email format.")
    @NotEmpty(message = "Email is required.")
    private String email;

    @NotEmpty(message = "Phone number is required.")
    private String phone;

    @NotEmpty(message = "Password cannot be empty.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String password;

    @NotEmpty(message = "Confirm password cannot be empty.")
    private String confirmPassword;

    @NotEmpty(message = "Please select a role for this user.")
    private String role;
}
