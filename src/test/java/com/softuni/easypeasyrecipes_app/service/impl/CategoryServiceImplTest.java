package com.softuni.easypeasyrecipes_app.service.impl;
import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import com.softuni.easypeasyrecipes_app.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void findAll() {
        Category category1 = new Category();
        category1.setId(1L);
        Category category2 = new Category();
        category2.setId(2L);

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category1, category2));

        List<Category> categories = categoryService.findAll();

        assertEquals(2, categories.size());
    }
    @Test
    void findById_CategoryFound() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName(CategoryEnum.Breakfast);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Optional<Category> foundCategory = categoryService.findById(categoryId);

        assertTrue(foundCategory.isPresent());
        assertEquals(categoryId, foundCategory.get().getId());
        assertEquals(CategoryEnum.Breakfast, foundCategory.get().getName());
    }

    @Test
    void findById_CategoryNotFound() {

        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        Optional<Category> foundCategory = categoryService.findById(categoryId);

        assertFalse(foundCategory.isPresent());
    }
    @Test
    void save() {
        Category category = new Category();
        category.setId(1L);

        categoryService.save(category);

        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void delete() {
        Long categoryId = 1L;

        categoryService.delete(categoryId);

        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void findByName() {
        Category category = new Category();
        category.setName(CategoryEnum.Breakfast);
        when(categoryRepository.findByName(CategoryEnum.Breakfast)).thenReturn(Optional.of(category));

        Category foundCategory = categoryService.findByName(CategoryEnum.Breakfast);

        assertNotNull(foundCategory);
        assertEquals(CategoryEnum.Breakfast, foundCategory.getName());
    }

    @Test
    void findByNameThrowsException() {
        when(categoryRepository.findByName(CategoryEnum.Lunch)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findByName(CategoryEnum.Lunch);
        });

        String expectedMessage = "Category not found: Lunch";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
