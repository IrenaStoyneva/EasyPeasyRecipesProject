package com.softuni.easypeasyrecipes_app.service.impl;


import com.softuni.easypeasyrecipes_app.model.dto.ChangePasswordDto;
import com.softuni.easypeasyrecipes_app.model.dto.ChangeUsernameDto;
import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.entity.UserRole;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import com.softuni.easypeasyrecipes_app.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void registerUser(RegisterUserDto registerUserDto) {
        if (registerUserDto == null) {
            throw new IllegalArgumentException("UserRegisterDto cannot be null");
        }

        Optional<User> existingUser = userRepository.findByUsernameOrEmail(registerUserDto.getUsername(), registerUserDto.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username or email already exists.");
        }

        User user = modelMapper.map(registerUserDto, User.class);
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));

        UserRole userRole = userRoleRepository.findByRole(RoleEnum.USER)
                .orElseThrow(() -> new IllegalStateException("Role not found."));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }


    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public boolean changeUsername(String currentUsername, ChangeUsernameDto changeUsernameDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(changeUsernameDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid current password");
        }


        if (userRepository.existsByUsername(changeUsernameDto.getNewUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setUsername(changeUsernameDto.getNewUsername());
        userRepository.save(user);

        return true;
    }

    @Override
    @Transactional
    public boolean changePassword(String currentUsername, ChangePasswordDto changePasswordDto) {
        Optional<User> userOptional = userRepository.findByUsername(currentUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid current password.");
            }

            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateUserRoles(Long userId, Set<RoleEnum> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<UserRole> currentRoles = user.getRoles();

        Set<UserRole> newRoles = roles.stream()
                .map(roleEnum -> userRoleRepository.findByRole(roleEnum)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

        currentRoles.addAll(newRoles);

        user.setRoles(currentRoles);
        userRepository.save(user);
    }
    public void removeUserRole(Long userId, RoleEnum role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserRole roleToRemove = userRoleRepository.findByRole(role)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        user.getRoles().remove(roleToRemove);
        userRepository.save(user);
    }

    @Override
    public long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user.getId();
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        } else {
            return Optional.empty();
        }
    }

    public List<String> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(role -> STR."ROLE_\{role.getRole().name()}")
                .collect(Collectors.toList());
    }
    @Override
    public long countUsers() {
        return userRepository.count();
    }

}
