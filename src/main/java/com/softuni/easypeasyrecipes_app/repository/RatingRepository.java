package com.softuni.easypeasyrecipes_app.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.softuni.easypeasyrecipes_app.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRecipeId(Long recipeId);

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.recipe.id = :recipeId")
    Double calculateAverageRating(@Param("recipeId") Long recipeId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.recipe.id = :recipeId")
    Integer countVotesByRecipeId(@Param("recipeId") Long recipeId);

    Optional<Rating> findFirstByRecipeIdAndUserId(Long recipeId, Long userId);
}


