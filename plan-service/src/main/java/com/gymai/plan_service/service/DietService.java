// package com.gymai.plan_service.service;

// import com.gymai.plan_service.dto.MealPlanResponse;
// import com.gymai.plan_service.feign.SpoonacularClient;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.concurrent.CompletableFuture;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class DietService {

// private final SpoonacularClient spoonacularClient;

// @Value("${spoonacular.api.key}")
// private String spoonacularApiKey;

// @Async("planExecutor")
// public CompletableFuture<List<String>> getMealPlan(int calories, String diet)
// {
// log.info("🍽️ Requesting meal plan: calories={} diet={}", calories, diet);
// try {
// String dietParam = (diet == null || diet.isBlank()) ? null :
// diet.toLowerCase();
// MealPlanResponse response = spoonacularClient.getMealPlan("day", calories,
// dietParam, spoonacularApiKey);

// if (response == null || response.getMeals() == null ||
// response.getMeals().isEmpty()) {
// log.warn("Spoonacular returned empty for calories={} diet={}", calories,
// diet);
// return CompletableFuture.completedFuture(fallbackMeals());
// }

// List<String> meals = response.getMeals().stream()
// .map(m -> String.format("%s (%d min)", m.getTitle(), m.getReadyInMinutes()))
// .collect(Collectors.toList());

// log.info("✅ Spoonacular returned {} meals", meals.size());
// return CompletableFuture.completedFuture(meals);
// } catch (Exception ex) {
// log.error("❌ Spoonacular error", ex);
// return CompletableFuture.completedFuture(fallbackMeals());
// }
// }

// private List<String> fallbackMeals() {
// return List.of("Oats + Fruit (10 min)", "Grilled Chicken Salad (20 min)",
// "Veg Stir-fry (25 min)");
// }
// }
