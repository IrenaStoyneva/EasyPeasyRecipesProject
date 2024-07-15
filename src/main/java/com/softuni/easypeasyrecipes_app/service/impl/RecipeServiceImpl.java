package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.CategoryRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserSession userSession;


    public RecipeServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, RecipeRepository recipeRepository, UserRepository userRepository, UserSession userSession) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    @Override
    @Transactional
    public void create(AddRecipeDto addRecipeDto, String imageUrl, long userId) {
        Optional<Category> categoryOptional = categoryRepository.findByName(addRecipeDto.getCategory());

        if (categoryOptional.isEmpty()) {
            throw new IllegalArgumentException("Category not found");
        }

        Recipe recipe = new Recipe();
        recipe.setName(addRecipeDto.getName());
        recipe.setDescription(addRecipeDto.getDescription());
        recipe.setIngredients(addRecipeDto.getIngredients());
        recipe.setInstructions(addRecipeDto.getInstructions());
        recipe.setCategory(categoryOptional.get());
        recipe.setImageUrl(imageUrl);
        recipe.setCreatedOn(LocalDateTime.now());

        Optional<User> userOptional = userRepository.findById(userSession.id());
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();
        recipe.setAddedBy(user);

        recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> searchRecipesByName(String query) {
        return recipeRepository.findByNameContainingIgnoreCase(query);
    }

    @Override
    public List<Recipe> getRecentRecipes() {
        return recipeRepository.findTop10ByOrderByCreatedOnDesc();
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> findByUserId(Long userId) {
        return recipeRepository.findByAddedBy_Id(userId);
    }


    @Override
    public Recipe updateRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipe(Long recipeId) {
        recipeRepository.deleteById(recipeId);
    }

    @Override
    public void approveRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + id));
        recipe.setApproved(true);
        recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> findApprovedRecipes() {
        return recipeRepository.findByApprovedTrue();
    }

    @Override
    public List<Recipe> findPendingByUserId(Long userId) {
        return recipeRepository.findByAddedByIdAndApprovedFalse(userId);
    }

    @Override
    public List<Recipe> findApprovedByUserId(Long id) {
        return recipeRepository.findByAddedByIdAndApprovedTrue(id);
    }
}




