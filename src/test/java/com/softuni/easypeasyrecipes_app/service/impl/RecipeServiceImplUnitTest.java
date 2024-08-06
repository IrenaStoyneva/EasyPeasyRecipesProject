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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceImplUnitTest {
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

    @BeforeEach
    public void setUp() {

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        Category testCategory = new Category();
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

        // Мокирайте поведението на recipeRepository
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // Извикайте метода на сървиса
        recipeService.approveRecipe(recipeId);

        // Проверете дали рецептата е одобрена
        assertTrue(recipe.isApproved());

        // Проверете дали рецептата е запазена
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    public void testApproveRecipeNotFound() {
        Long recipeId = 1L;

        // Мокирайте поведението на recipeRepository
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Проверете дали се хвърля изключение
        assertThrows(IllegalArgumentException.class, () -> {
            recipeService.approveRecipe(recipeId);
        });
    }
    @Test
    public void testFindApprovedRecipes() {
        Recipe recipe1 = new Recipe();
        recipe1.setApproved(true);

        // Мокирайте поведението на recipeRepository
        when(recipeRepository.findByApprovedTrue()).thenReturn(List.of(recipe1));

        // Извикайте метода на сървиса
        List<Recipe> approvedRecipes = recipeService.findApprovedRecipes();

        // Проверете резултатите
        assertEquals(1, approvedRecipes.size());
        assertTrue(approvedRecipes.get(0).isApproved());
    }
    @Test
    public void testFindApprovedByUserId() {
        Long userId = 1L;
        Recipe recipe1 = new Recipe();
        recipe1.setApproved(true);

        // Мокирайте поведението на recipeRepository
        when(recipeRepository.findByAddedByIdAndApprovedTrue(userId)).thenReturn(List.of(recipe1));

        // Извикайте метода на сървиса
        List<Recipe> approvedRecipes = recipeService.findApprovedByUserId(userId);

        // Проверете резултатите
        assertEquals(1, approvedRecipes.size());
        assertTrue(approvedRecipes.get(0).isApproved());
    }
}
