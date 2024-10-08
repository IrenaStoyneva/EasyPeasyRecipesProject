package com.softuni.easypeasyrecipes_app.model.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    @NotBlank
    private String content;
    private LocalDateTime createdOn;
    private Long authorId;
    private Long recipeId;

    public CommentDto(Long id, @NotBlank String content, LocalDateTime createdOn, Long authorId, Long recipeId) {
        this.id = id;
        this.content = content;
        this.createdOn = createdOn;
        this.authorId = authorId;
        this.recipeId = recipeId;
    }

    public CommentDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getContent() {
        return content;
    }

    public void setContent(@NotBlank String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
}
