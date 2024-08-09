package com.softuni.easypeasyrecipes_app.web;
import com.softuni.easypeasyrecipes_app.controller.SearchController;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
public class SearchControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testSearchRecipes() throws Exception {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Chocolate Cake");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Vanilla Cake");

        List<Recipe> recipes = Arrays.asList(recipe1, recipe2);

        Mockito.when(recipeService.searchRecipesByName(anyString())).thenReturn(recipes);

        mockMvc.perform(get("/search").param("query", "Cake"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attribute("recipes", hasSize(2)))
                .andExpect(model().attribute("recipes", is(recipes)));
    }
}
