
package com.gymai.plan_service.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DietPlanMapper {

  // Helper method to round to 1 decimal place
  private double roundTo1Decimal(double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

  public SimpleDietPlanDTO toDTO(DietPlan dietPlan) {
    if (dietPlan == null)
      return null;

    SimpleDietPlanDTO dto = new SimpleDietPlanDTO();
    dto.setId(dietPlan.getId());
    dto.setUserId(dietPlan.getUserId());
    dto.setDailyCalorieTarget(roundTo1Decimal(dietPlan.getDailyCalorieTarget()));
    dto.setDailyProteinTarget(roundTo1Decimal(dietPlan.getDailyProteinTarget()));
    dto.setDailyCarbsTarget(roundTo1Decimal(dietPlan.getDailyCarbsTarget()));
    dto.setDailyFatTarget(roundTo1Decimal(dietPlan.getDailyFatTarget()));
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
    dto.setTotalDailyCalories(roundTo1Decimal(dayPlan.getTotalDailyCalories()));
    dto.setTotalDailyProtein(roundTo1Decimal(dayPlan.getTotalDailyProtein()));
    dto.setTotalDailyCarbs(roundTo1Decimal(dayPlan.getTotalDailyCarbs()));
    dto.setTotalDailyFat(roundTo1Decimal(dayPlan.getTotalDailyFat()));

    List<SimpleMealDTO> mealsDTO = dayPlan.getMeals().stream()
        .map(this::toMealDTO)
        .collect(Collectors.toList());
    dto.setMeals(mealsDTO);

    return dto;
  }

  private SimpleMealDTO toMealDTO(Meal meal) {
    SimpleMealDTO dto = new SimpleMealDTO();
    dto.setMealType(meal.getMealType());
    dto.setTotalCalories(roundTo1Decimal(meal.getTotalCalories()));
    dto.setTotalProtein(roundTo1Decimal(meal.getTotalProtein()));
    dto.setTotalCarbs(roundTo1Decimal(meal.getTotalCarbs()));
    dto.setTotalFat(roundTo1Decimal(meal.getTotalFat()));

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
    dto.setQuantity(roundTo1Decimal(foodItem.getQuantity()));
    dto.setCalories(roundTo1Decimal(foodItem.getCalories()));
    dto.setProtein(roundTo1Decimal(foodItem.getProtein()));
    dto.setCarbs(roundTo1Decimal(foodItem.getCarbs()));
    dto.setFat(roundTo1Decimal(foodItem.getFat()));
    dto.setFiber(roundTo1Decimal(foodItem.getFiber()));
    return dto;
  }
}