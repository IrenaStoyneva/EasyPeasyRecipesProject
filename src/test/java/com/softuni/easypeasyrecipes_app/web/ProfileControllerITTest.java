package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.controller.ProfileController;
import com.softuni.easypeasyrecipes_app.model.dto.ChangePasswordDto;
import com.softuni.easypeasyrecipes_app.model.dto.ChangeUsernameDto;
import com.softuni.easypeasyrecipes_app.model.dto.EditRecipeDto;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.model.validation.UniqueUsernameValidator;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import com.softuni.easypeasyrecipes_app.service.session.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileController.class)
public class ProfileControllerITTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UniqueUsernameValidator uniqueUsernameValidator;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        Mockito.when(uniqueUsernameValidator.isValid(Mockito.any(), Mockito.any())).thenReturn(true);

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testViewProfile() throws Exception {
        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        Mockito.when(recipeService.findApprovedByUserId(Mockito.anyLong())).thenReturn(List.of());
        Mockito.when(recipeService.findPendingByUserId(Mockito.anyLong())).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("recipes"))
                .andExpect(model().attributeExists("pendingRecipes"))
                .andExpect(model().attributeExists("hasApprovedRecipes"))
                .andExpect(model().attributeExists("hasPendingRecipes"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testEditProfile() throws Exception {
        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        mockMvc.perform(MockMvcRequestBuilders.get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-profile"))
                .andExpect(model().attributeExists("changeUsernameDto"))
                .andExpect(model().attributeExists("changePasswordDto"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testChangeUsername() throws Exception {
        ChangeUsernameDto changeUsernameDto = new ChangeUsernameDto();
        changeUsernameDto.setNewUsername("newUsername");
        changeUsernameDto.setCurrentPassword("currentPassword");

        Mockito.when(userService.changeUsername(Mockito.anyString(), Mockito.any(ChangeUsernameDto.class)))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/profile/change-username")
                        .flashAttr("changeUsernameDto", changeUsernameDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Mockito.verify(userService).changeUsername(Mockito.anyString(), Mockito.any(ChangeUsernameDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testChangePassword() throws Exception {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setNewPassword("newPassword");
        changePasswordDto.setConfirmPassword("newPassword");
        changePasswordDto.setCurrentPassword("currentPassword");

        Mockito.when(userService.changePassword(Mockito.anyString(), Mockito.any(ChangePasswordDto.class)))
                .thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/profile/change-password")
                        .flashAttr("changePasswordDto", changePasswordDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/edit"));

        Mockito.verify(userService).changePassword(Mockito.anyString(), Mockito.any(ChangePasswordDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteRecipe() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/profile/delete-recipe/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(recipeService).deleteRecipe(1L);
    }
}
