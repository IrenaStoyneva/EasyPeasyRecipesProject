package com.softuni.easypeasyrecipes_app.repository;

import com.softuni.easypeasyrecipes_app.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findTop10ByOrderByCreatedOnDesc();

    Optional<Recipe> findById(Long id);

    List<Recipe> findByAddedBy_Id(Long userId);

    @Query("SELECT r FROM Recipe r ORDER BY (SELECT AVG(rt.value) FROM Rating rt WHERE rt.recipe = r) DESC")
    List<Recipe> findTopRecipes();

    List<Recipe> findByApprovedTrue();

    List<Recipe> findByAddedByIdAndApprovedFalse(Long userId);

    List<Recipe> findByAddedByIdAndApprovedTrue(Long userId);


}
