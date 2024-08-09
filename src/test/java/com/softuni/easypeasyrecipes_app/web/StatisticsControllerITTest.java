package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.controller.StatisticsController;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.service.StatisticsService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
public class StatisticsControllerITTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void testViewStatistics() throws Exception {
        long userCount = 100;
        long recipeCount = 200;
        long commentCount = 300;
        List<Recipe> topRecipes = Arrays.asList(
                new Recipe("Recipe1", "Description1", "Ingredients1", "Instructions1", "imageUrl1"),
                new Recipe("Recipe2", "Description2", "Ingredients2", "Instructions2", "imageUrl2"),
                new Recipe("Recipe3", "Description3", "Ingredients3", "Instructions3", "imageUrl3")
        );

        when(statisticsService.getUserCount()).thenReturn(userCount);
        when(statisticsService.getRecipeCount()).thenReturn(recipeCount);
        when(statisticsService.getCommentCount()).thenReturn(commentCount);
        when(statisticsService.getTopRecipes()).thenReturn(topRecipes);

        mockMvc.perform(get("/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/statistics"))
                .andExpect(model().attribute("userCount", is(userCount)))
                .andExpect(model().attribute("recipeCount", is(recipeCount)))
                .andExpect(model().attribute("commentCount", is(commentCount)))
                .andExpect(model().attribute("topRecipes", hasSize(3)))
                .andExpect(model().attribute("topRecipes", contains(
                        hasProperty("name", is("Recipe1")),
                        hasProperty("name", is("Recipe2")),
                        hasProperty("name", is("Recipe3"))
                )));
    }
}
