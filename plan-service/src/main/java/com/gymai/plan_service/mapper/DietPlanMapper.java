// DietPlanMapper.java
package com.gymai.plan_service.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.*;

@Component
public class DietPlanMapper {

  public SimpleDietPlanDTO toDTO(DietPlan dietPlan) {
    if (dietPlan == null)
      return null;

    SimpleDietPlanDTO dto = new SimpleDietPlanDTO();
    dto.setId(dietPlan.getId());
    dto.setUserId(dietPlan.getUserId());
    dto.setDailyCalorieTarget(dietPlan.getDailyCalorieTarget());
    dto.setDailyProteinTarget(dietPlan.getDailyProteinTarget());
    dto.setDailyCarbsTarget(dietPlan.getDailyCarbsTarget());
    dto.setDailyFatTarget(dietPlan.getDailyFatTarget());
    dto.setCreatedDate(dietPlan.getCreatedDate());

    List<SimpleDayMealPlanDTO> dailyPlansDTO = dietPlan.getDailyPlans().stream()
        .map(this::toDayMealPlanDTO)
        .collect(Collectors.toList());
    dto.setDailyPlans(dailyPlansDTO);

    return dto;
  }

  private SimpleDayMealPlanDTO toDayMealPlanDTO(DayMealPlan dayPlan) {
    SimpleDayMealPlanDTO dto = new SimpleDayMealPlanDTO();
    dto.setDayNumber(dayPlan.getDayNumber());
    dto.setDayName(dayPlan.getDayName());
    dto.setTotalDailyCalories(dayPlan.getTotalDailyCalories());
    dto.setTotalDailyProtein(dayPlan.getTotalDailyProtein());
    dto.setTotalDailyCarbs(dayPlan.getTotalDailyCarbs());
    dto.setTotalDailyFat(dayPlan.getTotalDailyFat());

    List<SimpleMealDTO> mealsDTO = dayPlan.getMeals().stream()
        .map(this::toMealDTO)
        .collect(Collectors.toList());
    dto.setMeals(mealsDTO);

    return dto;
  }

  private SimpleMealDTO toMealDTO(Meal meal) {
    SimpleMealDTO dto = new SimpleMealDTO();
    dto.setMealType(meal.getMealType());
    dto.setTotalCalories(meal.getTotalCalories());
    dto.setTotalProtein(meal.getTotalProtein());
    dto.setTotalCarbs(meal.getTotalCarbs());
    dto.setTotalFat(meal.getTotalFat());

    List<SimpleFoodItemDTO> foodItemsDTO = meal.getFoodItems().stream()
        .map(this::toFoodItemDTO)
        .collect(Collectors.toList());
    dto.setFoodItems(foodItemsDTO);

    return dto;
  }

  private SimpleFoodItemDTO toFoodItemDTO(FoodItem foodItem) {
    SimpleFoodItemDTO dto = new SimpleFoodItemDTO();
    dto.setFoodName(foodItem.getFood().getName());
    dto.setCategory(foodItem.getFood().getCategory());
    dto.setQuantity(foodItem.getQuantity());
    dto.setCalories(foodItem.getCalories());
    dto.setProtein(foodItem.getProtein());
    dto.setCarbs(foodItem.getCarbs());
    dto.setFat(foodItem.getFat());
    dto.setFiber(foodItem.getFiber());
    return dto;
  }
}