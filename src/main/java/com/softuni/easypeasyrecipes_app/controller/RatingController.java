package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @ModelAttribute("ratingDto")
    public RatingDto ratingDto() {
        return new RatingDto();
    }

    @PostMapping("/recipe/{id}/rate")
    public String addRating(@PathVariable Long id,
                            @Valid @ModelAttribute("ratingDto") RatingDto ratingDto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("ratingDto", ratingDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.ratingDto", bindingResult);
            return "redirect:/recipe/" + id;
        }

        ratingService.addRating(id, ratingDto);
        return "redirect:/recipe/" + id;
    }
}
