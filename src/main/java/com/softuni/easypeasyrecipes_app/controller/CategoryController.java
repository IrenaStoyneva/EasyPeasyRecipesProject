package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/recipes")
public class CategoryController {

    private final RecipeService recipeService;

    public CategoryController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    @GetMapping("/{category}")
    public String viewRecipesByCategory(@PathVariable("category") CategoryEnum category, Model model) {
        List<Recipe> recipes = recipeService.findRecipesByCategory(category);
        model.addAttribute("recipes", recipes);
        model.addAttribute("category", category.name());
        model.addAttribute("hasRecipes", !recipes.isEmpty());
        return "category-recipes";
    }
}
