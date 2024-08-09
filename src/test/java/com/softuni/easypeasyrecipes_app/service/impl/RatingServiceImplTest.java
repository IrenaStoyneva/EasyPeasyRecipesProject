package com.softuni.easypeasyrecipes_app.service.impl;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.model.entity.Rating;
import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import com.softuni.easypeasyrecipes_app.model.entity.User;
import com.softuni.easypeasyrecipes_app.repository.RatingRepository;
import com.softuni.easypeasyrecipes_app.repository.RecipeRepository;
import com.softuni.easypeasyrecipes_app.repository.UserRepository;
import com.softuni.easypeasyrecipes_app.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Captor
    private ArgumentCaptor<Rating> ratingCaptor;

    private User testUser;
    private Recipe testRecipe;
    private RatingDto ratingDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testRecipe = new Recipe();
        testRecipe.setId(1L);

        ratingDto = new RatingDto();
        ratingDto.setValue(5);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(authentication.isAuthenticated()).thenReturn(true);
        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        lenient().when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
    }

    @Test
    public void testCalculateAverageRating() {
        Rating rating1 = new Rating();
        rating1.setValue(4);

        Rating rating2 = new Rating();
        rating2.setValue(5);

        when(ratingRepository.findByRecipeId(1L)).thenReturn(List.of(rating1, rating2));

        double averageRating = ratingService.calculateAverageRating(1L);

        assertEquals(4.5, averageRating);
    }

    @Test
    public void testFindUserRatingForRecipe() {
        Rating rating = new Rating();
        rating.setValue(5);

        when(ratingRepository.findFirstByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.of(rating));

        int userRating = ratingService.findUserRatingForRecipe(1L, 1L);

        assertEquals(5, userRating);
    }

    @Test
    public void testFindUserRatingForRecipeNotFound() {
        when(ratingRepository.findFirstByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        int userRating = ratingService.findUserRatingForRecipe(1L, 1L);

        assertEquals(0, userRating);
    }

    @Test
    public void testAddRating() {
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(ratingRepository.findFirstByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        ratingService.addRating(1L, ratingDto);

        verify(ratingRepository).save(ratingCaptor.capture());
        Rating savedRating = ratingCaptor.getValue();

        assertEquals(5, savedRating.getValue());
        assertEquals(testUser, savedRating.getUser());
        assertEquals(testRecipe, savedRating.getRecipe());
    }

    @Test
    public void testAddRatingUpdateExisting() {
        Rating existingRating = new Rating();
        existingRating.setValue(3);

        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(testRecipe));
        when(ratingRepository.findFirstByRecipeIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingRating));

        ratingService.addRating(1L, ratingDto);

        verify(ratingRepository).save(ratingCaptor.capture());
        Rating savedRating = ratingCaptor.getValue();

        assertEquals(5, savedRating.getValue());
    }

    @Test
    public void testCountVotes() {
        when(ratingRepository.countVotesByRecipeId(1L)).thenReturn(5);

        int votes = ratingService.countVotes(1L);

        assertEquals(5, votes);
    }
}
