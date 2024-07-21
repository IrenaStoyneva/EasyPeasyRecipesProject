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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Recipe> approvedRecipes = recipeService.findApprovedByUserId(user.getId());
            List<Recipe> pendingRecipes = recipeService.findPendingByUserId(user.getId());
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "edit-profile";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/edit")
    public String editProfile(@ModelAttribute User user, @RequestParam String currentPassword, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
                existingUser.setUsername(user.getUsername());
                existingUser.setEmail(user.getEmail());
                userService.updateUser(existingUser);

                // Update the authentication object with the new username
                UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        userDetails, userDetails.getPassword(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            Recipe recipe = recipeService.findById(editRecipeDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid recipe Id:" + editRecipeDto.getId()));

            modelMapper.map(editRecipeDto, recipe);

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