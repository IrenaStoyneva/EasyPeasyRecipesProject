package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    private final UserSession userSession;

    public LogoutController(UserSession userSession) {
        this.userSession = userSession;
    }

    @GetMapping("/logout")
    public String logout() {
        userSession.logout();
        return "redirect:/login";
    }
}
