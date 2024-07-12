package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;

public interface UserService {

    void registerUser(RegisterUserDto registerUserDto);


    boolean login(UserLoginDto userLoginDto);
}
