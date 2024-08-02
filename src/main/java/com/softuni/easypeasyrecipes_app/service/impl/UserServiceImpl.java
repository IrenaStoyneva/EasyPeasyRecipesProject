package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.entity.UserRole;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

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
    public boolean login(UserLoginDto userLoginDto) {
        Optional<User> byUsername = userRepository.findByUsername(userLoginDto.getUsername());

        if (byUsername.isEmpty()) {
            return false;
        }

        boolean passMatch = passwordEncoder.matches(userLoginDto.getPassword(), byUsername.get().getPassword());

        if (!passMatch) {
            return false;
        }

        return true;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
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

        Set<UserRole> userRoles = roles.stream()
                .map(roleEnum -> userRoleRepository.findByRole(roleEnum)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

        user.setRoles(userRoles);
        userRepository.save(user);
    }

    @Override
    public long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user.getId();
    }

    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username);
        } else {
            return Optional.empty();
        }
    }

    public List<String> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(role -> "ROLE_" + role.getRole().name())
                .collect(Collectors.toList());
    }
}
