package com.softuni.easypeasyrecipes_app.service.impl;


import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.Comment;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.CommentRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final RestClient restClient;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(@Qualifier("commentsRestClient") RestClient restClient, ModelMapper modelMapper) {
        this.restClient = restClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CommentDto> getAllComments() {
        logger.info("Fetching all comments from REST API...");
        CommentDto[] comments = restClient.get()
                .uri("/api/comments")
                .retrieve()
                .body(CommentDto[].class);
        logger.info("Fetched {} comments", comments.length);
        return Arrays.stream(comments)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long recipeId, CommentDto commentDto) {
        logger.info("Adding comment to recipe ID: {}", recipeId);
        CommentDto savedComment = restClient.post()
                .uri("/api/comments/recipe/{recipeId}", recipeId)
                .body(commentDto)
                .retrieve()
                .body(CommentDto.class);
        logger.info("Added comment with ID: {}", savedComment.getId());
        return savedComment;
    }

    @Override
    public void deleteComment(Long id) {
        logger.info("Deleting comment with ID: {}", id);
        restClient.delete()
                .uri("/api/comments/{id}", id)
                .retrieve()
                .body(Void.class);
        logger.info("Deleted comment with ID: {}", id);
    }
    @Override
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        logger.info("Updating comment with ID: {}", id);
        CommentDto updatedComment = restClient.put()
                .uri("/api/comments/{id}", id)
                .body(commentDto)
                .retrieve()
                .body(CommentDto.class);
        logger.info("Updated comment with ID: {}", updatedComment.getId());
        return updatedComment;
    }

    @Override
    public CommentDto getCommentById(Long id) {
        logger.info("Fetching comment with ID: {}", id);
        CommentDto commentDto = restClient.get()
                .uri("/api/comments/{id}", id)
                .retrieve()
                .body(CommentDto.class);
        logger.info("Fetched comment with ID: {}", commentDto.getId());
        return commentDto;
    }

}
