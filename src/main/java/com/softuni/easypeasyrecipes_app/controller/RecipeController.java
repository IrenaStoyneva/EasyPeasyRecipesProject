package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.service.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class RecipeController {
    private final RecipeService recipeService;
    private final ModelMapper modelMapper;
    private final CommentService commentService;
    private final RatingService ratingService;
    private final UserService userService;

    @Autowired
    public RecipeController(RecipeService recipeService, ModelMapper modelMapper, CommentService commentService, RatingService ratingService, UserService userService) {
        this.recipeService = recipeService;
        this.modelMapper = modelMapper;
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @ModelAttribute("addRecipeDto")
    public AddRecipeDto recipeData() {
        return new AddRecipeDto();
    }

    @GetMapping("/create/recipe")
    public String showCreateRecipeForm(Model model) {
        model.addAttribute("addRecipeDto", new AddRecipeDto());
        return "create-recipe";
    }

    @PostMapping("/create/recipe")
    public String addRecipe(@Valid @ModelAttribute("addRecipeDto") AddRecipeDto addRecipeDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("addRecipeDto", addRecipeDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addRecipeDto", bindingResult);
            return "redirect:/create/recipe";
        }

        try {
            String imageUrl = saveImage(addRecipeDto.getImageFile());
            addRecipeDto.setImageUrl(imageUrl);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            long userId = userService.findUserIdByUsername(username);

            recipeService.create(addRecipeDto, imageUrl, userId);
        } catch (IOException e) {
            bindingResult.rejectValue("imageFile", "error.addRecipeDto", "Failed to save image");
            redirectAttributes.addFlashAttribute("addRecipeDto", addRecipeDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addRecipeDto", bindingResult);
            return "redirect:/create/recipe";
        }

        return "redirect:/recipes";
    }

    private String saveImage(MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            byte[] bytes = image.getBytes();
            Path path = Paths.get("uploads/" + image.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
            return "/uploads/" + image.getOriginalFilename();
        }
        return null;
    }

    @GetMapping("/recipes")
    public String viewRecipes(Model model) {
        List<Recipe> recipes = recipeService.findApprovedRecipes();
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    @GetMapping("/recipe/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            String[] ingredientsArray = recipe.getIngredients().split(",");
            model.addAttribute("recipe", recipe);
            model.addAttribute("ingredientsList", ingredientsArray);
            model.addAttribute("ratingDto", new RatingDto());
            model.addAttribute("commentDto", new CommentDto());
            model.addAttribute("comments", recipe.getComments().stream().map(comment -> {
                comment.setFormattedDate(comment.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return comment;
            }).collect(Collectors.toList()));

            Double averageRating = ratingService.calculateAverageRating(id);
            Integer totalVotes = ratingService.countVotes(id);

            model.addAttribute("averageRating", averageRating);
            model.addAttribute("totalVotes", totalVotes);

            return "view-recipe";
        } else {
            return "error/404";
        }
    }
}
