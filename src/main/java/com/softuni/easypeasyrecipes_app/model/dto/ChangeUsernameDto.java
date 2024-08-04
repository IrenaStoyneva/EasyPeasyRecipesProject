package com.softuni.easypeasyrecipes_app.model.dto;

import com.softuni.easypeasyrecipes_app.model.validation.UniqueUsername;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ChangeUsernameDto {
    @NotEmpty(message = "New username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    @UniqueUsername(message = "This username is already taken.")
    private String newUsername;

    @NotEmpty(message = "Current password cannot be empty.")
    private String currentPassword;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
