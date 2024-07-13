package com.softuni.easypeasyrecipes_app.repository;

import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findTop10ByOrderByCreatedOnDesc();

    Optional<Recipe> findById(Long id);

    List<Recipe> findByAddedBy_Id(Long userId);

}
