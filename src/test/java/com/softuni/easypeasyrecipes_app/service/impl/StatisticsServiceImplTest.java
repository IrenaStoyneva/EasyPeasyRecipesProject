package com.softuni.easypeasyrecipes_app.service.impl;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.repository.CommentRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsServiceImpl(userRepository, recipeRepository, commentRepository);
    }

    @Test
    public void testGetUserCount() {
        long expectedCount = 10L;
        when(userRepository.count()).thenReturn(expectedCount);

        long actualCount = statisticsService.getUserCount();

        assertEquals(expectedCount, actualCount);
        verify(userRepository, times(1)).count();
    }

    @Test
    public void testGetRecipeCount() {
        long expectedCount = 20L;
        when(recipeRepository.count()).thenReturn(expectedCount);

        long actualCount = statisticsService.getRecipeCount();

        assertEquals(expectedCount, actualCount);
        verify(recipeRepository, times(1)).count();
    }

    @Test
    public void testGetCommentCount() {
        long expectedCount = 30L;
        when(commentRepository.count()).thenReturn(expectedCount);

        long actualCount = statisticsService.getCommentCount();

        assertEquals(expectedCount, actualCount);
        verify(commentRepository, times(1)).count();
    }

    @Test
    public void testGetTopRecipes() {
        List<Recipe> expectedTopRecipes = List.of(new Recipe(), new Recipe());
        when(recipeRepository.findTopRecipes()).thenReturn(expectedTopRecipes);

        List<Recipe> actualTopRecipes = statisticsService.getTopRecipes();

        assertEquals(expectedTopRecipes, actualTopRecipes);
        verify(recipeRepository, times(1)).findTopRecipes();
    }
}
