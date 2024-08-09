package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.controller.CategoryController;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CategoryController.class)
class CategoryControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Test
    void testViewRecipesByCategory() throws Exception {
        CategoryEnum category = CategoryEnum.Breakfast;
        when(recipeService.findRecipesByCategory(category)).thenReturn(Collections.singletonList(new Recipe()));

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{category}", category))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipes", "category", "hasRecipes"))
                .andExpect(model().attribute("category", category.name()))
                .andExpect(model().attribute("hasRecipes", true))
                .andExpect(view().name("category-recipes"));
    }

    @Test
    void testViewRecipesByCategoryNoRecipes() throws Exception {
        CategoryEnum category = CategoryEnum.Breakfast;
        when(recipeService.findRecipesByCategory(category)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/recipes/{category}", category))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipes", "category", "hasRecipes"))
                .andExpect(model().attribute("category", category.name()))
                .andExpect(model().attribute("hasRecipes", false))
                .andExpect(view().name("category-recipes"));
    }
}
