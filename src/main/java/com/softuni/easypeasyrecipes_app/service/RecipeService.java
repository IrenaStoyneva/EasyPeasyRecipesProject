package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface RecipeService {

    void create(AddRecipeDto addRecipeDto, String imageUrl, long userId);

    List<Recipe> searchRecipesByName(String query);

    List<Recipe> getRecentRecipes();

    Optional<Recipe> findById(Long id);

    List<Recipe> getAllRecipes();

}

