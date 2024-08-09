package com.softuni.easypeasyrecipes_app.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softuni.easypeasyrecipes_app.controller.RatingController;
import com.softuni.easypeasyrecipes_app.model.dto.RatingDto;
import com.softuni.easypeasyrecipes_app.service.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(SpringExtension.class)
@WebMvcTest(RatingController.class)
public class RatingControllerITTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddRatingWithValidData() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setValue(4);

        Mockito.doNothing().when(ratingService).addRating(Mockito.anyLong(), Mockito.any(RatingDto.class));
        Mockito.when(ratingService.calculateAverageRating(Mockito.anyLong())).thenReturn(4.5);

        mockMvc.perform(post("/recipe/1/rate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.newAverageRating").value(4.5));
    }

    @Test
    void testAddRatingWithInvalidData() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setValue(6); // Invalid value

        mockMvc.perform(post("/recipe/1/rate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("value", "6"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
