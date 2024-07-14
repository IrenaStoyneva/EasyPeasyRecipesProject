package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void registerUser(RegisterUserDto registerUserDto);


    boolean login(UserLoginDto userLoginDto);

    Optional<User> findById(Long id);

    User updateUser(User user);

    List<User> findAllUsers();

    void deleteUser(Long id);
}
