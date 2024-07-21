package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    void registerUser(RegisterUserDto registerUserDto);


    boolean login(UserLoginDto userLoginDto);

    Optional<User> findById(Long id);

    User updateUser(User user);

    List<User> findAllUsers();

    void deleteUser(Long id);

    Optional<User> findByUsername(String username);

    UserDetails loadUserByUsername(String username);

    void updateUserRoles(Long id, Set<RoleEnum> roles);

    long findUserIdByUsername(String username);
}
