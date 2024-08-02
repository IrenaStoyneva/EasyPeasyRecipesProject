package com.softuni.easypeasyrecipes_app.model.dto;

import com.softuni.easypeasyrecipes_app.model.validation.FieldMatch;
import com.softuni.easypeasyrecipes_app.model.validation.UniqueEmail;
import com.softuni.easypeasyrecipes_app.model.validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@FieldMatch(firstField = "password", secondField = "confirmPassword", message = "Passwords do not match")
public class RegisterUserDto {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username length must be between 3 and 20 characters.")
    @UniqueUsername
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 5, max = 20, message = "Password length must be between 5 and 20 characters.")
    private String password;

    @NotBlank(message = "Confirm Password cannot be empty")
    @Size(min = 5, max = 20, message = "Password length must be between 5 and 20 characters.")
    private String confirmPassword;

    @NotEmpty(message = "User email should be provided.")
    @Email(message = "User email should be valid.")
    @UniqueEmail(message = "User email should be unique.")
    private String email;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
