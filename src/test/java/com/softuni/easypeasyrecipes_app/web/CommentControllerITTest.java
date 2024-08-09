package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.controller.CommentController;
import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
class CommentControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Мокнете UserDetails обект
        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(testUser.getUsername())
                        .password("password")
                        .authorities("ROLE_USER")
                        .build();

        Mockito.when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddCommentWithValidComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a valid comment");

        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/1"));

        Mockito.verify(commentService, Mockito.times(1)).addComment(eq(1L), any(CommentDto.class));
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddCommentException() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a valid comment");

        Mockito.doThrow(new RuntimeException("Service exception")).when(commentService).addComment(eq(1L), any(CommentDto.class));

        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(redirectedUrl("/recipe/1"));

        Mockito.verify(commentService, Mockito.times(1)).addComment(eq(1L), any(CommentDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteComment() throws Exception {
        Mockito.when(commentService.getRecipeIdByCommentId(1L)).thenReturn(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/1"));

        Mockito.verify(commentService, Mockito.times(1)).deleteComment(1L);
    }
    @Test
    void testAddCommentUnauthenticated() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false); // Симулира неаутентифициран потребител
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test comment content");

        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddCommentWithBindingErrors() throws Exception {
        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", new CommentDto())
                        .flashAttr("org.springframework.validation.BindingResult.commentDto", new org.springframework.validation.BeanPropertyBindingResult(new CommentDto(), "commentDto")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/1"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddCommentSuccessfully() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test comment content");

        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/recipe/1"));

        Mockito.verify(commentService).addComment(eq(1L), any(CommentDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAddCommentThrowsException() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Test comment content");

        Mockito.doThrow(new RuntimeException("Exception")).when(commentService).addComment(eq(1L), any(CommentDto.class));

        mockMvc.perform(post("/recipe/1/comments")
                        .flashAttr("commentDto", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(redirectedUrl("/recipe/1"));
    }

}
