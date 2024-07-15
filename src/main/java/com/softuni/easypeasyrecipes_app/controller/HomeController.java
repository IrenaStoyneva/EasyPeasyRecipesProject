package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import org.springframework.ui.Model;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    private final RecipeService recipeService;


    public HomeController(RecipeService recipeService) {
        this.recipeService = recipeService;

    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("recentRecipes", recipeService.findApprovedRecipes());
        return "index";
    }

    @GetMapping("/home")
    public String showHome(Model model) {
        model.addAttribute("recentRecipes", recipeService.findApprovedRecipes());
        return "home";
    }
}
