package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.Comment;

import java.util.List;

public interface CommentService {
    List<CommentDto> getAllComments();

    CommentDto addComment(Long recipeId, CommentDto commentDto);
    void deleteComment(Long id);


}

