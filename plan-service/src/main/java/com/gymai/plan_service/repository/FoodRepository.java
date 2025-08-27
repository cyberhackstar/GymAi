package com.gymai.plan_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gymai.plan_service.entity.Food;

// FoodRepository.java
@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByDietTypeAndMealType(String dietType, String mealType);

    List<Food> findByDietTypeInAndMealType(List<String> dietTypes, String mealType);

    List<Food> findByCategory(String category);

    List<Food> findByDietTypeAndCategory(String dietType, String category);

    @Query("SELECT f FROM Food f WHERE f.dietType IN :dietTypes AND f.category = :category")
    List<Food> findByDietTypesAndCategory(@Param("dietTypes") List<String> dietTypes,
            @Param("category") String category);

    @Query("SELECT f FROM Food f WHERE f.proteinPer100g >= :minProtein AND f.dietType IN :dietTypes")
    List<Food> findHighProteinFoods(@Param("minProtein") double minProtein,
            @Param("dietTypes") List<String> dietTypes);

    @Query("SELECT f FROM Food f WHERE f.caloriesPer100g <= :maxCalories AND f.dietType IN :dietTypes")
    List<Food> findLowCalorieFoods(@Param("maxCalories") double maxCalories,
            @Param("dietTypes") List<String> dietTypes);
}