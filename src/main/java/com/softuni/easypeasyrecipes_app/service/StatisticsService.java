package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.entity.Recipe;

import java.util.List;

public interface StatisticsService {
    long getUserCount();
    long getRecipeCount();
    long getCommentCount();
    List<Recipe> getTopRecipes();
}
