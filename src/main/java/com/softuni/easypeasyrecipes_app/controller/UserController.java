package com.softuni.easypeasyrecipes_app.controller;

import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("registerUserDto")
    public RegisterUserDto registerUserDto() {
        return new RegisterUserDto();
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerUserDto") RegisterUserDto registerUserDto,
                               BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerUserDto", bindingResult);
            redirectAttributes.addFlashAttribute("registerUserDto", registerUserDto);
            return "redirect:/register";
        }

        try {
            userService.registerUser(registerUserDto);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("globalError", e.getMessage());
            redirectAttributes.addFlashAttribute("registerUserDto", registerUserDto);
            return "redirect:/register";
        }

        redirectAttributes.addFlashAttribute("success", "You are successfully registered, please login!");
        return "redirect:/login";
    }
}
