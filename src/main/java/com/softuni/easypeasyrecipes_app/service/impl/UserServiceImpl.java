package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.config.UserSession;
import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.dto.UserLoginDto;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserSession userSession;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, ModelMapper modelMapper, UserSession userSession) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userSession = userSession;
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

        userSession.login(byUsername.get().getId(), userLoginDto.getUsername());

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

}
