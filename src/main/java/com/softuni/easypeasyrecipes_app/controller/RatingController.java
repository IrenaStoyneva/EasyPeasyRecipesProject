package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.AddRecipeDto;
import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> addRating(@PathVariable Long id,
                                                         @Valid @ModelAttribute("ratingDto") RatingDto ratingDto,
                                                         BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }

        ratingService.addRating(id, ratingDto);
        double newAverageRating = ratingService.calculateAverageRating(id);
        response.put("success", true);
        response.put("newAverageRating", newAverageRating);
        return ResponseEntity.ok(response);
    }
}
