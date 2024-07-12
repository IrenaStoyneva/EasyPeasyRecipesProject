package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.model.entity.Rating;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.RatingRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserSession userSession;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, RecipeRepository recipeRepository, UserRepository userRepository, UserSession userSession) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    public double calculateAverageRating(Long recipeId) {
        List<Rating> ratings = ratingRepository.findByRecipeId(recipeId);
        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    public Integer findUserRatingForRecipe(Long recipeId, Long userId) {
        return ratingRepository.findFirstByRecipeIdAndUserId(recipeId, userId)
                .map(Rating::getValue)
                .orElse(0);
    }

    @Override
    public void addRating(Long recipeId, RatingDto ratingDto) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            throw new IllegalArgumentException("Recipe not found");
        }

        Optional<User> userOptional = userRepository.findById(userSession.id());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Recipe recipe = recipeOptional.get();
        User user = userOptional.get();

        Optional<Rating> existingRatingOptional = ratingRepository.findFirstByRecipeIdAndUserId(recipeId, user.getId());
        Rating rating;
        if (existingRatingOptional.isPresent()) {
            rating = existingRatingOptional.get();
            rating.setValue(ratingDto.getValue());
        } else {
            rating = new Rating();
            rating.setValue(ratingDto.getValue());
            rating.setRecipe(recipe);
            rating.setUser(user);
        }

        ratingRepository.save(rating);
    }

    public Integer countVotes(Long recipeId) {
        return ratingRepository.countVotesByRecipeId(recipeId);
    }
}
