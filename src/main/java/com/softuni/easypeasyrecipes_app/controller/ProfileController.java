package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.ChangePasswordDto;
import com.softuni.easypeasyrecipes_app.model.dto.ChangeUsernameDto;
import com.softuni.easypeasyrecipes_app.model.dto.EditRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import com.softuni.easypeasyrecipes_app.service.session.UserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final RecipeService recipeService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final UserDetailsService userDetailsService;

    @Autowired
    public ProfileController(UserService userService, RecipeService recipeService, CategoryService categoryService, ModelMapper modelMapper, UserDetailsService userDetailService) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.userDetailsService = userDetailService;
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
    public String editProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("changeUsernameDto", new ChangeUsernameDto());
        model.addAttribute("changePasswordDto", new ChangePasswordDto());

        return "edit-profile";
    }

    @PostMapping("/change-username")
    public String changeUsername(@Valid @ModelAttribute("changeUsernameDto") ChangeUsernameDto changeUsernameDto,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changeUsernameDto", bindingResult);
            redirectAttributes.addFlashAttribute("changeUsernameDto", changeUsernameDto);
            return "redirect:/profile/edit";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        try {
            userService.changeUsername(currentUsername, changeUsernameDto);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("globalError", e.getMessage());
            redirectAttributes.addFlashAttribute("changeUsernameDto", changeUsernameDto);
            return "redirect:/profile/edit";
        }

        redirectAttributes.addFlashAttribute("success", "Username changed successfully! Please login again.");
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto changePasswordDto,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDto", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordDto", changePasswordDto);
            return "redirect:/profile/edit";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        try {
            userService.changePassword(currentUsername, changePasswordDto);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
            redirectAttributes.addFlashAttribute("changePasswordDto", changePasswordDto);
            return "redirect:/profile/edit";
        }

        redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully!");
        return "redirect:/profile/edit";
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
    public String editRecipe(@ModelAttribute EditRecipeDto editRecipeDto,
                             RedirectAttributes redirectAttributes) {
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

                redirectAttributes.addFlashAttribute("successMessage", "Recipe updated successfully!");
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
