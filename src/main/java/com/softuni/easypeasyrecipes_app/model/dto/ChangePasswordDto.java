package com.softuni.easypeasyrecipes_app.model.dto;

import com.softuni.easypeasyrecipes_app.model.validation.FieldMatch;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@FieldMatch(firstField = "newPassword", secondField = "confirmPassword", message = "The password fields must match")
public class ChangePasswordDto {

    @NotEmpty(message = "Current password cannot be empty.")
    private String currentPassword;

    @NotEmpty(message = "New password cannot be empty.")
    @Size(min = 5, message = "New password must be at least 5 characters long.")
    private String newPassword;

    @NotEmpty(message = "Confirm password cannot be empty.")
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
