package com.softuni.easypeasyrecipes_app.controller;

import org.springframework.ui.Model;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private final RecipeService recipeService;

    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        List<Recipe> recentRecipes = recipeService.getRecentRecipes();
        model.addAttribute("recentRecipes", recentRecipes);
        return "home";
    }
}
