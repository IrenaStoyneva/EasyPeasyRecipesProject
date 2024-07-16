package com.softuni.easypeasyrecipes_app.repository;

import com.softuni.easypeasyrecipes_app.model.entity.Category;
import com.softuni.easypeasyrecipes_app.model.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(CategoryEnum name);

    Optional<Category> findById(long id);

}
