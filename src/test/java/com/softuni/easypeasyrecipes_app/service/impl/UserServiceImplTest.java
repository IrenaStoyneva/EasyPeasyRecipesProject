package com.softuni.easypeasyrecipes_app.service.impl;

import com.softuni.easypeasyrecipes_app.model.dto.ChangePasswordDto;
import com.softuni.easypeasyrecipes_app.model.dto.ChangeUsernameDto;
import com.softuni.easypeasyrecipes_app.model.dto.RegisterUserDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.entity.UserRole;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterUserDto registerUserDto;
    private ChangeUsernameDto changeUsernameDto;
    private ChangePasswordDto changePasswordDto;

    @BeforeEach
    void setUp() {
        registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("testuser");
        registerUserDto.setEmail("testuser@example.com");
        registerUserDto.setPassword("testpassword");

        changeUsernameDto = new ChangeUsernameDto();
        changeUsernameDto.setCurrentPassword("testpassword");
        changeUsernameDto.setNewUsername("newusername");

        changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword("testpassword");
        changePasswordDto.setNewPassword("newpassword");
    }

    @Test
    public void testFindAllUsers() {

        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        List<User> mockUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(mockUsers);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
    }

    @Test
    public void testFindByUsername_UserExists() {

        String username = "testuser";
        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    public void testFindByUsername_UserDoesNotExist() {

        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());


        Optional<User> result = userService.findByUsername(username);


        assertTrue(result.isEmpty());
    }

    @Test
    public void testUpdateUserRoles_AddRoles() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(new HashSet<>());

        UserRole userRoleUser = new UserRole();
        userRoleUser.setRole(RoleEnum.USER);

        UserRole userRoleAdmin = new UserRole();
        userRoleAdmin.setRole(RoleEnum.ADMIN);

        Set<RoleEnum> newRoles = Set.of(RoleEnum.USER, RoleEnum.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByRole(RoleEnum.USER)).thenReturn(Optional.of(userRoleUser));
        when(userRoleRepository.findByRole(RoleEnum.ADMIN)).thenReturn(Optional.of(userRoleAdmin));

        userService.updateUserRoles(1L, newRoles);

        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(userRoleUser));
        assertTrue(user.getRoles().contains(userRoleAdmin));
        verify(userRepository).save(user);
    }


    @Test
    public void testUpdateUserRoles_UserNotFound() {
        Set<RoleEnum> newRoles = Set.of(RoleEnum.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserRoles(1L, newRoles);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserRoles_RoleNotFound() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(new HashSet<>());

        Set<RoleEnum> newRoles = Set.of(RoleEnum.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRoleRepository.findByRole(RoleEnum.USER)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserRoles(1L, newRoles);
        });

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    public void testRegisterUserUsernameOrEmailExists() {
        when(userRepository.findByUsernameOrEmail(registerUserDto.getUsername(), registerUserDto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerUserDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testChangeUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changeUsernameDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(userRepository.existsByUsername(changeUsernameDto.getNewUsername())).thenReturn(false);

        boolean result = userService.changeUsername("testuser", changeUsernameDto);

        assertTrue(result);
        assertEquals(changeUsernameDto.getNewUsername(), user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    public void testChangePassword() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordDto.getNewPassword())).thenReturn("newencodedpassword");

        boolean result = userService.changePassword("testuser", changePasswordDto);

        assertTrue(result);
        assertEquals("newencodedpassword", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(user.getUsername(), foundUser.get().getUsername());
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }


    @Test
    public void testFindUserIdByUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        long userId = userService.findUserIdByUsername("testuser");

        assertEquals(1L, userId);
    }

    @Test
    public void testFindUserIdByUsernameNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.findUserIdByUsername("nonexistent"));
    }
    @Test
    public void testGetCurrentUser_WhenUserIsAuthenticated() {

        User user = new User();
        user.setUsername("testuser");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities(new ArrayList<>())
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> currentUser = userService.getCurrentUser();

        assertTrue(currentUser.isPresent());
        assertEquals("testuser", currentUser.get().getUsername());
    }

    @Test
    public void testGetCurrentUser_WhenNoUserIsAuthenticated() {

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        Optional<User> currentUser = userService.getCurrentUser();

        assertFalse(currentUser.isPresent());
    }

    @Test
    public void testGetUserRoles() {

        User user = new User();
        UserRole roleUser = new UserRole();
        roleUser.setRole(RoleEnum.USER);
        UserRole roleAdmin = new UserRole();
        roleAdmin.setRole(RoleEnum.ADMIN);
        user.setRoles(Set.of(roleUser, roleAdmin));

        List<String> roles = userService.getUserRoles(user);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    public void testGetUserRoles_WhenUserHasNoRoles() {
        User user = new User();
        user.setRoles(new HashSet<>());

        List<String> roles = userService.getUserRoles(user);

        assertEquals(0, roles.size());
    }
}
