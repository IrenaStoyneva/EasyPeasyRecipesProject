package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public String viewStatistics(Model model) {
        model.addAttribute("userCount", statisticsService.getUserCount());
        model.addAttribute("recipeCount", statisticsService.getRecipeCount());
        model.addAttribute("commentCount", statisticsService.getCommentCount());
        model.addAttribute("topRecipes", statisticsService.getTopRecipes());
        return "admin/statistics";
    }
}
