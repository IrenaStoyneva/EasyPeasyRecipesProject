package com.softuni.easypeasyrecipes_app.model.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentDto {

    @NotBlank
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
