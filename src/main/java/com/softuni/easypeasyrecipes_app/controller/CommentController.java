package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @ModelAttribute("commentDto")
    public CommentDto commentDto() {
        return new CommentDto();
    }

    @PostMapping("/recipe/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentDto", commentDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentDto", bindingResult);
            return "redirect:/recipe/" + id;
        }

        try {
            commentService.addComment(id, commentDto);
            logger.info("User {} added a comment to recipe {}", authentication.getName(), id);
        } catch (Exception e) {
            logger.error("Error adding comment to recipe {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Could not add comment. Please try again.");
            return "redirect:/recipe/" + id;
        }

        return "redirect:/recipe/" + id;
    }
}
