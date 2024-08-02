package com.softuni.easypeasyrecipes_app.scheduling;

import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class RecipeCleanupSchedulerTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeCleanupScheduler recipeCleanupScheduler;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();

        Recipe oldRecipeWithoutRating = new Recipe();
        oldRecipeWithoutRating.setName("Old Recipe");

        oldRecipeWithoutRating.setCreatedOn(LocalDateTime.now().minusYears(2));
        oldRecipeWithoutRating.setApproved(true);
        oldRecipeWithoutRating.setDescription("Old recipe description");
        oldRecipeWithoutRating.setIngredients("Old recipe ingredients");
        oldRecipeWithoutRating.setInstructions("Old recipe instructions");

        User user = new User();
        user.setId(1L);
        oldRecipeWithoutRating.setAddedBy(user);

        Category category = new Category();
        category.setId(1L);
        oldRecipeWithoutRating.setCategory(category);

        recipeRepository.save(oldRecipeWithoutRating);

        Recipe newRecipeWithRating = new Recipe();
        newRecipeWithRating.setName("New Recipe with Rating");
        newRecipeWithRating.setCreatedOn(LocalDateTime.now().minusMonths(6));
        newRecipeWithRating.setApproved(true);
        newRecipeWithRating.setDescription("New recipe description");
        newRecipeWithRating.setIngredients("New recipe ingredients");
        newRecipeWithRating.setInstructions("New recipe instructions");
        newRecipeWithRating.setAddedBy(user);
        newRecipeWithRating.setCategory(category);
        recipeRepository.save(newRecipeWithRating);
    }

    @Test
    void testDeleteOldRecipesWithoutRatings() {

        recipeCleanupScheduler.deleteOldRecipesWithoutRatings();

        Optional<Recipe> oldRecipe = recipeRepository.findByName("Old Recipe");
        assertTrue(oldRecipe.isEmpty(), "Старата рецепта без рейтинг трябва да бъде изтрита.");

        Optional<Recipe> newRecipe = recipeRepository.findByName("New Recipe with Rating");
        assertTrue(newRecipe.isPresent(), "Новата рецепта с рейтинг трябва да остане.");
    }
}
