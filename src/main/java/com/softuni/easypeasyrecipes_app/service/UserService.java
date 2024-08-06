package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.ChangePasswordDto;
import com.softuni.easypeasyrecipes_app.model.dto.ChangeUsernameDto;
import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    void registerUser(RegisterUserDto registerUserDto);

    Optional<User> findById(Long id);


    List<User> findAllUsers();

    void deleteUser(Long id);

    Optional<User> findByUsername(String username);

    void updateUserRoles(Long id, Set<RoleEnum> roles);

    long findUserIdByUsername(String username);


    Optional<User> getCurrentUser();

    Object getUserRoles(User user);

    boolean changeUsername(String currentUsername, ChangeUsernameDto changeUsernameDto);

    boolean changePassword(String currentUsername, ChangePasswordDto changePasswordDto);
}
