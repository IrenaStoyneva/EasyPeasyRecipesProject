package com.softuni.easypeasyrecipes_app.scheduling;

import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecipeCleanupScheduler {
    private final RecipeService recipeService;

    public RecipeCleanupScheduler(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void deleteOldRecipesWithoutRatings() {
        recipeService.deleteOldRecipesWithoutRatings();

        System.out.println("Scheduled task executed: Deleted old recipes without ratings.");
    }

}
