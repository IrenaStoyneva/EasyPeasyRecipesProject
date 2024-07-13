package com.softuni.easypeasyrecipes_app.service;

import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> findById(Long categoryId);

    List<Category> findAll();

}

