package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {

    @ModelAttribute("loginDto")
    public UserLoginDto loginDto() {
        return new UserLoginDto();
    }
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
