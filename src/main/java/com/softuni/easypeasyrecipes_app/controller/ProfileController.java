package com.softuni.easypeasyrecipes_app.controller;


import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.EditRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final RecipeService recipeService;
    private final UserSession userSession;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProfileController(UserService userService, RecipeService recipeService, UserSession userSession, PasswordEncoder passwordEncoder, CategoryService categoryService, ModelMapper modelMapper) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.userSession = userSession;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public String viewProfile(Model model) {
        Long userId = userSession.id();
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Recipe> approvedRecipes = recipeService.findApprovedByUserId(userId);
            List<Recipe> pendingRecipes = recipeService.findPendingByUserId(userId);
            model.addAttribute("user", user);
            model.addAttribute("recipes", approvedRecipes);
            model.addAttribute("pendingRecipes", pendingRecipes);
            model.addAttribute("hasApprovedRecipes", !approvedRecipes.isEmpty());
            model.addAttribute("hasPendingRecipes", !pendingRecipes.isEmpty());
            return "profile";
        } else {
            return "error/404";
        }
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model) {
        Optional<User> userOptional = userService.findById(userSession.id());
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "edit-profile";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit")
    public String editProfile(@ModelAttribute User user, @RequestParam String currentPassword, Model model) {
        Optional<User> userOptional = userService.findById(userSession.id());
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
                existingUser.setUsername(user.getUsername());
                existingUser.setEmail(user.getEmail());
                userService.updateUser(existingUser);
                return "redirect:/profile";
            } else {
                model.addAttribute("error", "Invalid password");
                model.addAttribute("user", existingUser);
                return "edit-profile";
            }
        } else {
            return "error/404";
        }
    }

    @GetMapping("/edit-recipe/{id}")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        Optional<Recipe> recipeOptional = recipeService.findById(id);
        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            EditRecipeDto editRecipeDto = modelMapper.map(recipe, EditRecipeDto.class);
            model.addAttribute("editRecipeDto", editRecipeDto);
            model.addAttribute("categories", categoryService.findAll());
            return "edit-recipe";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit-recipe")
    public String editRecipe(@ModelAttribute EditRecipeDto editRecipeDto) {
        Optional<User> userOptional = userService.findById(userSession.id());
        if (userOptional.isPresent()) {
            Recipe recipe = recipeService.findById(editRecipeDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + editRecipeDto.getId()));

            modelMapper.map(editRecipeDto, recipe); // използване на ModelMapper за мапинг от DTO към Entity

            recipe.setAddedBy(userOptional.get());

            Optional<Category> categoryOptional = categoryService.findById(editRecipeDto.getCategoryId());
            if (categoryOptional.isPresent()) {
                recipe.setCategory(categoryOptional.get());
                recipeService.updateRecipe(recipe);
                return "redirect:/profile";
            } else {
                return "error/404";
            }
        } else {
            return "error/404";
        }
    }

    @DeleteMapping("/delete-recipe/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/profile";
    }
}