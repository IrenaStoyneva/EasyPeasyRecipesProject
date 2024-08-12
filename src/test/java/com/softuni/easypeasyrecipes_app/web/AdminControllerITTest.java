package com.softuni.easypeasyrecipes_app.web;

import com.softuni.easypeasyrecipes_app.controller.AdminController;
import com.softuni.easypeasyrecipes_app.model.dto.CommentDto;
import com.softuni.easypeasyrecipes_app.model.entity.*;
import com.softuni.easypeasyrecipes_app.model.enums.RoleEnum;
import com.softuni.easypeasyrecipes_app.repository.UserRoleRepository;
import com.softuni.easypeasyrecipes_app.service.CategoryService;
import com.softuni.easypeasyrecipes_app.service.CommentService;
import com.softuni.easypeasyrecipes_app.service.RecipeService;
import com.softuni.easypeasyrecipes_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AdminController.class)
class AdminControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    void testViewUsers() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.singletonList(new User()));
        when(userRoleRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users", "allRoles"))
                .andExpect(view().name("admin/users"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/delete/{id}", 1L))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/users"));
    }

    @Test
    public void testViewComments() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("Test content");
        commentDto.setAuthorId(1L);
        commentDto.setRecipeId(1L);
        commentDto.setCreatedOn(LocalDateTime.now());

        List<CommentDto> commentDtos = List.of(commentDto);
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setContent(commentDto.getContent());
        comment.setCreatedOn(commentDto.getCreatedOn());
        User author = new User();
        author.setId(commentDto.getAuthorId());
        author.setUsername("TestUser");
        comment.setAuthor(author);

        when(commentService.getAllComments()).thenReturn(commentDtos);
        when(modelMapper.map(commentDto, Comment.class)).thenReturn(comment);

        mockMvc.perform(get("/admin/comments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/comments"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attribute("comments", hasSize(1)))
                .andExpect(model().attribute("comments", hasItem(
                        hasProperty("content", is("Test content"))
                )));

        verify(commentService, times(1)).getAllComments();
    }
    @Test
    void testDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/comments/delete/{id}", 1L))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/comments"));
    }

    @Test
    void testShowEditCommentForm() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);  // Вместо username използваме authorId
        when(commentService.getCommentById(1L)).thenReturn(commentDto);
        mockMvc.perform(get("/admin/comments/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("comment"))
                .andExpect(view().name("admin/edit-comment"));
    }
    @Test
    public void testUpdateComment() throws Exception {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setContent("Updated content");

        when(commentService.updateComment(eq(commentId), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/admin/comments/edit/" + commentId)
                        .flashAttr("comment", commentDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/comments"));

        verify(commentService, times(1)).updateComment(eq(commentId), eq(commentDto));
    }
    @Test
    void testViewRecipes() throws Exception {
        when(recipeService.getAllRecipes()).thenReturn(Collections.singletonList(new Recipe()));

        mockMvc.perform(get("/admin/recipes"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipes"))
                .andExpect(view().name("admin/recipes"));
    }

    @Test
    void testApproveRecipe() throws Exception {
        doNothing().when(recipeService).approveRecipe(1L);

        mockMvc.perform(post("/admin/recipes/approve/{id}", 1L))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/recipes"));
    }

    @Test
    void testDeleteRecipe() throws Exception {
        doNothing().when(recipeService).deleteRecipe(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/recipes/delete/{id}", 1L))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/recipes"));
    }

    @Test
    void testUpdateUserRoles() throws Exception {
        doNothing().when(userService).updateUserRoles(1L, Set.of(RoleEnum.ADMIN));

        mockMvc.perform(post("/admin/users/{id}/roles", 1L)
                        .param("roles", "ADMIN"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/users"));
    }
    @Test
    void testViewStatistics() throws Exception {

        when(userService.countUsers()).thenReturn(10L);
        when(recipeService.countRecipes()).thenReturn(5L);
        when(commentService.countComments()).thenReturn(20L);

        Recipe recipe = new Recipe();
        recipe.setName("Test Recipe");

        Rating rating1 = new Rating();
        rating1.setValue(4);
        Rating rating2 = new Rating();
        rating2.setValue(5);

        recipe.setRatings(Set.of(rating1, rating2));

        when(recipeService.getTopRecipesByRating(5)).thenReturn(List.of(recipe));

        Map<String, Long> recipesByCategory = Map.of(
                "Dessert", 3L,
                "Dinner", 2L
        );
        when(recipeService.getRecipesCountByCategory()).thenReturn(recipesByCategory);

        mockMvc.perform(get("/admin/statistics"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/statistics"))
                .andExpect(model().attribute("userCount", 10L))
                .andExpect(model().attribute("recipeCount", 5L))
                .andExpect(model().attribute("commentCount", 20L))
                .andExpect(model().attribute("topRecipes", hasSize(1)))
                .andExpect(model().attribute("topRecipes", hasItem(
                        allOf(
                                hasProperty("name", is("Test Recipe")),
                                hasProperty("averageRating", is(4.5)),
                                hasProperty("totalVotes", is(2))
                        )
                )))
                .andExpect(model().attribute("categoryNames", hasSize(2)))
                .andExpect(model().attribute("categoryNames", containsInAnyOrder("Dessert", "Dinner")))
                .andExpect(model().attribute("categoryCounts", hasSize(2)))
                .andExpect(model().attribute("categoryCounts", containsInAnyOrder(3L, 2L)));
    }

}