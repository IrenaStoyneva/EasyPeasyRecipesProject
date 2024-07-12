package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;

public interface RatingService {
    void addRating(Long recipeId, RatingDto ratingDto);

    Object findUserRatingForRecipe(Long id, Long id1);
    double calculateAverageRating(Long recipeId);

    Integer countVotes(Long id);
}
