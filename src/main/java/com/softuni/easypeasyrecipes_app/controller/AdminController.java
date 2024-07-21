package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.entity.*;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RecipeService recipeService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final UserRoleRepository userRoleRepository;


    public AdminController(UserService userService, RecipeService recipeService, CommentService commentService, CategoryService categoryService, UserRoleRepository userRoleRepository) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String viewUsers(Model model) {
        List<User> users = userService.findAllUsers();
        List<UserRole> allRoles = userRoleRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("allRoles", allRoles);
        return "admin/users";
    }
    @DeleteMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }


    @GetMapping("/comments")
    public String viewComments(Model model) {
        List<Comment> comments = commentService.findAllComments();
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    @DeleteMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/admin/comments";
    }

    @GetMapping("/recipes")
    public String viewRecipes(Model model) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        return "admin/recipes";
    }

    @PostMapping("/recipes/approve/{id}")
    public String approveRecipe(@PathVariable Long id) {
        recipeService.approveRecipe(id);
        return "redirect:/admin/recipes";
    }

    @DeleteMapping("/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/admin/recipes";
    }

    @GetMapping("/categories")
    public String viewCategories(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @GetMapping("/categories/add")
    public String showAddCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categoryEnums", Arrays.asList(CategoryEnum.values()));
        return "admin/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model) {
        Optional<Category> optionalCategory = categoryService.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            model.addAttribute("category", category);
            return "admin/edit-category";
        } else {
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/categories/edit")
    public String editCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id, @RequestParam("roles") Set<RoleEnum> roles) {
        userService.updateUserRoles(id, roles);
        return "redirect:/admin/users";
    }
}