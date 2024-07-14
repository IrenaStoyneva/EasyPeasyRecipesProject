package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.Comment;

import java.util.List;

public interface CommentService {
    void addComment(Long recipeId, CommentDto commentDto);

    List<Comment> findCommentsByRecipeId(Long recipeId);

    List<Comment> findAllComments();

    void deleteComment(Long id);
}
