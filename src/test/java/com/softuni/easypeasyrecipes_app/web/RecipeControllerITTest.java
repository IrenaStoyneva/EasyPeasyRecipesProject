package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.config.SecurityConfiguration;
import com.softuni.easypeasyrecipes_app.controller.RecipeController;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RecipeController.class)
@Import(SecurityConfiguration.class)
public class RecipeControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testAddRecipe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test data".getBytes());

        mockMvc.perform(multipart("/create/recipe")
                        .file(file)
                        .param("title", "Test Recipe")
                        .param("description", "Test Description")
                        .param("ingredients", "Ingredient1, Ingredient2")
                        .param("instructions", "Step1, Step2")
                        .param("name", "Test Recipe Name")
                        .param("category", "Dessert"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/recipes"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testViewRecipes() throws Exception {
        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"))
                .andExpect(model().attributeExists("recipes"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testViewRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe.setIngredients("Ingredient1, Ingredient2");

        Mockito.when(recipeService.findById(1L)).thenReturn(Optional.of(recipe));
        Mockito.when(ratingService.calculateAverageRating(1L)).thenReturn(4.5);
        Mockito.when(ratingService.countVotes(1L)).thenReturn(10);

        mockMvc.perform(get("/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("view-recipe"))
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeExists("ingredientsList"))
                .andExpect(model().attributeExists("ratingDto"))
                .andExpect(model().attributeExists("commentDto"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("averageRating", 4.5))
                .andExpect(model().attribute("totalVotes", 10));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testViewAllRecipes() throws Exception {
        mockMvc.perform(get("/recipes/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("recipes"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("showAll", true));
    }
}