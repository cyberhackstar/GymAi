package com.gymai.plan_service.feign;

import com.gymai.plan_service.dto.MealPlanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "spoonacular-client", url = "${spoonacular.api-url}")
public interface SpoonacularClient {
    @GetMapping("/mealplanner/generate")
    MealPlanResponse getMealPlan(
        @RequestParam("timeFrame") String timeFrame,
        @RequestParam("targetCalories") int calories,
        @RequestParam("diet") String diet,
        @RequestParam("apiKey") String apiKey
    );
}
