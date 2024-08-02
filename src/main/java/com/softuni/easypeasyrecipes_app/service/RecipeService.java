package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;

import java.util.List;
import java.util.Optional;

public interface RecipeService {

    void create(AddRecipeDto addRecipeDto, String imageUrl, long userId);

    List<Recipe> searchRecipesByName(String query);

    List<Recipe> getRecentRecipes();

    Optional<Recipe> findById(Long id);

    List<Recipe> getAllRecipes();

    List<Recipe> findByUserId(Long userId);

    Recipe updateRecipe(Recipe recipe);

    void deleteRecipe(Long id);

    void approveRecipe(Long id);

    List<Recipe> findApprovedRecipes();

    List<Recipe> findPendingByUserId(Long userId);

    List<Recipe> findApprovedByUserId(Long id);

    List<Recipe> findRecipesByCategory(CategoryEnum category);

    void deleteOldRecipesWithoutRatings();
}

