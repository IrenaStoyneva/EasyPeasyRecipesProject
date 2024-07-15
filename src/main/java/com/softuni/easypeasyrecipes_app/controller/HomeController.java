package com.softuni.easypeasyrecipes_app.controller;

import org.springframework.ui.Model;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
