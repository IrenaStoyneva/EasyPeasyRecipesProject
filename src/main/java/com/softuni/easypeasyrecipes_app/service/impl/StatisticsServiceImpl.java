package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.repository.CommentRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;

    public StatisticsServiceImpl(UserRepository userRepository, RecipeRepository recipeRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public long getRecipeCount() {
        return recipeRepository.count();
    }

    @Override
    public long getCommentCount() {
        return commentRepository.count();
    }

    @Override
    public List<Recipe> getTopRecipes() {
        return recipeRepository.findTopRecipes();
    }
}