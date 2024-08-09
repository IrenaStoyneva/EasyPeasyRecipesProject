package com.softuni.easypeasyrecipes_app.service.impl;
import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.CategoryRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceImplTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    @Captor
    private ArgumentCaptor<Recipe> recipeCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> dateTimeCaptor;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setName(CategoryEnum.Dessert);
    }

    @Test
    public void testCreateRecipeCategoryNotFound() {
        AddRecipeDto recipeDto = new AddRecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setDescription("Test description");
        recipeDto.setIngredients("Test ingredients");
        recipeDto.setCategory(CategoryEnum.Lunch);

        String imageUrl = "http://example.com/image.jpg";

        when(categoryRepository.findByName(CategoryEnum.Lunch)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> recipeService.create(recipeDto, imageUrl, 1L));

        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    public void testCreateRecipeUserNotFound() {
        AddRecipeDto recipeDto = new AddRecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setDescription("Test description");
        recipeDto.setIngredients("Test ingredients");
        recipeDto.setCategory(CategoryEnum.Lunch);

        String imageUrl = "http://example.com/image.jpg";

        Recipe recipe = new Recipe();
        when(categoryRepository.findByName(CategoryEnum.Lunch)).thenReturn(Optional.of(testCategory));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(modelMapper.map(recipeDto, Recipe.class)).thenReturn(recipe);

        assertThrows(IllegalArgumentException.class, () -> recipeService.create(recipeDto, imageUrl, 1L));

        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    public void testCreateRecipeSuccess() {
        AddRecipeDto recipeDto = new AddRecipeDto();
        recipeDto.setName("Test Recipe");
        recipeDto.setDescription("Test description");
        recipeDto.setIngredients("Test ingredients");
        recipeDto.setCategory(CategoryEnum.Lunch);

        String imageUrl = "http://example.com/image.jpg";
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");
        recipe.setDescription("Test description");
        recipe.setIngredients("Test ingredients");
        recipe.setCategory(testCategory);
        recipe.setAddedBy(testUser);
        recipe.setImageUrl(imageUrl);

        when(categoryRepository.findByName(CategoryEnum.Lunch)).thenReturn(Optional.of(testCategory));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(modelMapper.map(recipeDto, Recipe.class)).thenReturn(recipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        recipeService.create(recipeDto, imageUrl, 1L);

        verify(recipeRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();
        assertEquals(recipeDto.getName(), savedRecipe.getName());
        assertEquals(imageUrl, savedRecipe.getImageUrl());
    }

    @Test
    public void testSearchRecipesByName() {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Apple Pie");

        when(recipeRepository.findByNameContainingIgnoreCase("Pie")).thenReturn(List.of(recipe1));

        List<Recipe> recipes = recipeService.searchRecipesByName("Pie");

        assertEquals(1, recipes.size(), "Expected one recipe to be found");
        assertEquals("Apple Pie", recipes.get(0).getName(), "Expected recipe name to be 'Apple Pie'");
    }

    @Test
    public void testGetRecentRecipes() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();

        when(recipeRepository.findTop10ByOrderByCreatedOnDesc()).thenReturn(List.of(recipe1, recipe2));

        List<Recipe> recentRecipes = recipeService.getRecentRecipes();

        assertEquals(2, recentRecipes.size());
    }

    @Test
    public void testFindByIdRecipeExists() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        recipe.setName("Test Recipe");

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Optional<Recipe> foundRecipe = recipeService.findById(1L);

        assertTrue(foundRecipe.isPresent(), "Recipe should be present");
        assertEquals(1L, foundRecipe.get().getId(), "Recipe ID should be 1");
        assertEquals("Test Recipe", foundRecipe.get().getName(), "Recipe name should be 'Test Recipe'");
    }

    @Test
    public void testFindByIdRecipeDoesNotExist() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Recipe> foundRecipe = recipeService.findById(1L);

        assertFalse(foundRecipe.isPresent());
    }

    @Test
    public void testUpdateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("Updated Recipe");

        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe updatedRecipe = recipeService.updateRecipe(recipe);

        assertEquals("Updated Recipe", updatedRecipe.getName());
    }

    @Test
    public void testDeleteRecipe() {
        doNothing().when(recipeRepository).deleteById(1L);

        recipeService.deleteRecipe(1L);

        verify(recipeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllRecipes() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();

        when(recipeRepository.findAll()).thenReturn(List.of(recipe1, recipe2));

        List<Recipe> recipes = recipeService.getAllRecipes();

        assertEquals(2, recipes.size());
        assertTrue(recipes.contains(recipe1));
        assertTrue(recipes.contains(recipe2));
    }

    @Test
    public void testApproveRecipe() {
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setApproved(false);

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.approveRecipe(recipeId);

        assertTrue(recipe.isApproved());
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    public void testApproveRecipeNotFound() {
        Long recipeId = 1L;

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> recipeService.approveRecipe(recipeId));
    }

    @Test
    public void testFindApprovedRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setApproved(true);

        when(recipeRepository.findByApprovedTrue()).thenReturn(List.of(recipe1));

        List<Recipe> approvedRecipes = recipeService.findApprovedRecipes();

        assertEquals(1, approvedRecipes.size());
        assertTrue(approvedRecipes.get(0).isApproved());
    }

    @Test
    public void testFindApprovedByUserId() {
        Long userId = 1L;
        Recipe recipe1 = new Recipe();
        recipe1.setApproved(true);

        when(recipeRepository.findByAddedByIdAndApprovedTrue(userId)).thenReturn(List.of(recipe1));

        List<Recipe> approvedRecipes = recipeService.findApprovedByUserId(userId);

        assertEquals(1, approvedRecipes.size());
        assertTrue(approvedRecipes.get(0).isApproved());
    }

    @Test
    public void testFindRecipesByCategory() {
        CategoryEnum categoryEnum = CategoryEnum.Dessert;
        Category category = new Category();
        category.setName(categoryEnum);

        Recipe recipe1 = new Recipe();
        recipe1.setCategory(category);

        when(categoryService.findByName(categoryEnum)).thenReturn(category);
        when(recipeRepository.findByCategory(category)).thenReturn(List.of(recipe1));

        List<Recipe> recipes = recipeService.findRecipesByCategory(categoryEnum);

        assertEquals(1, recipes.size());
        assertEquals(categoryEnum, recipes.get(0).getCategory().getName());
    }

    @Test
    public void testDeleteOldRecipesWithoutRatings() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();

        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        when(recipeRepository.findRecipesWithoutRatingOlderThan(any(LocalDateTime.class))).thenReturn(List.of(recipe1, recipe2));

        recipeService.deleteOldRecipesWithoutRatings();

        verify(recipeRepository, times(1)).deleteAll(List.of(recipe1, recipe2));
    }
    @Test
    public void testFindByUserId() {
        Long userId = 1L;
        Recipe recipe1 = new Recipe();
        recipe1.setAddedBy(testUser);

        when(recipeRepository.findByAddedBy_Id(userId)).thenReturn(List.of(recipe1));

        List<Recipe> recipes = recipeService.findByUserId(userId);

        assertEquals(1, recipes.size());
        assertEquals(testUser, recipes.get(0).getAddedBy());
    }
    @Test
    public void testFindPendingByUserId() {
        Long userId = 1L;
        Recipe recipe1 = new Recipe();
        recipe1.setApproved(false);

        when(recipeRepository.findByAddedByIdAndApprovedFalse(userId)).thenReturn(List.of(recipe1));

        List<Recipe> pendingRecipes = recipeService.findPendingByUserId(userId);

        assertEquals(1, pendingRecipes.size());
        assertFalse(pendingRecipes.get(0).isApproved());
    }
    @Test
    public void testFindAllRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setName("Recipe 1");

        Recipe recipe2 = new Recipe();
        recipe2.setName("Recipe 2");

        when(recipeRepository.findAll()).thenReturn(List.of(recipe1, recipe2));

        List<Recipe> recipes = recipeService.findAllRecipes();

        assertEquals(2, recipes.size(), "Expected to find 2 recipes");

        assertTrue(recipes.contains(recipe1), "Expected to find Recipe 1");
        assertTrue(recipes.contains(recipe2), "Expected to find Recipe 2");

        verify(recipeRepository, times(1)).findAll();
    }
}