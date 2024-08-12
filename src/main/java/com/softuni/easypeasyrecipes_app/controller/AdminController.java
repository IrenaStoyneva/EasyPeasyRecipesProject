package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.*;

import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RecipeService recipeService;
    private final CommentService commentService;
    private final CategoryService categoryService;
    private final UserRoleRepository userRoleRepository;
    private final ModelMapper modelMapper;

    public AdminController(UserService userService, RecipeService recipeService, CommentService commentService, CategoryService categoryService, UserRoleRepository userRoleRepository, ModelMapper modelMapper) {
        this.userService = userService;
        this.recipeService = recipeService;
        this.commentService = commentService;
        this.categoryService = categoryService;
        this.userRoleRepository = userRoleRepository;
        this.modelMapper = modelMapper;
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
    public String viewComments(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("errorMessage", "Comment not found.");
        }

        List<CommentDto> commentDtos = commentService.getAllComments();
        List<Comment> comments = commentDtos.stream()
                .map(commentDto -> modelMapper.map(commentDto, Comment.class))
                .collect(Collectors.toList());
        model.addAttribute("comments", comments);
        return "admin/comments";
    }

    @DeleteMapping("/comments/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return "redirect:/admin/comments";
    }

    @GetMapping("/comments/edit/{id}")
    public String showEditCommentForm(@PathVariable Long id, Model model) {
        CommentDto comment = commentService.getCommentById(id);
        if (comment == null) {
            return "redirect:/admin/comments?error=CommentNotFound";
        }
        model.addAttribute("comment", comment);
        return "admin/edit-comment";
    }

    @PostMapping("/comments/edit/{id}")
    public String updateComment(@PathVariable Long id, @ModelAttribute("comment") CommentDto commentDto) {
        commentService.updateComment(id, commentDto);
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

    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id, @RequestParam("roles") Set<RoleEnum> roles, RedirectAttributes redirectAttributes) {
        userService.updateUserRoles(id, roles);
        redirectAttributes.addFlashAttribute("successMessage", "Roles updated successfully!");
        return "redirect:/admin/users";
    }
    @PostMapping("/users/remove-role/{id}")
    public String removeUserRole(@PathVariable Long id, @RequestParam String roleToRemove, RedirectAttributes redirectAttributes) {
        userService.removeUserRole(id, RoleEnum.valueOf(roleToRemove));
        redirectAttributes.addFlashAttribute("successMessage", "Role removed successfully!");
        return "redirect:/admin/users";
    }
    @GetMapping("/statistics")
    public String viewStatistics(Model model) {
        long userCount = userService.countUsers();
        long recipeCount = recipeService.countRecipes();
        long commentCount = commentService.countComments();
        List<Recipe> topRecipes = recipeService.getTopRecipesByRating(5);

        Map<String, Long> recipesByCategory = recipeService.getRecipesCountByCategory();
        List<String> categoryNames = new ArrayList<>(recipesByCategory.keySet());
        List<Long> categoryCounts = new ArrayList<>(recipesByCategory.values());

        model.addAttribute("userCount", userCount);
        model.addAttribute("recipeCount", recipeCount);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("topRecipes", topRecipes);
        model.addAttribute("categoryNames", categoryNames);
        model.addAttribute("categoryCounts", categoryCounts);

        return "admin/statistics";
    }


}