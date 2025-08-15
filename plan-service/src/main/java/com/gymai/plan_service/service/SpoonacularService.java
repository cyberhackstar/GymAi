package com.gymai.plan_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.gymai.plan_service.dto.DietRequest;
import com.gymai.plan_service.dto.MealDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SpoonacularService {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    // ===========================
    // FETCH MEALS (Null safe)
    // ===========================
    public List<MealDto> fetchMeals(DietRequest userInfo, int targetCalories, String dayName) {
        // Build Spoonacular meal plan endpoint
        String url = String.format(
                "https://api.spoonacular.com/mealplanner/generate?timeFrame=day&targetCalories=%d&diet=%s&apiKey=%s",
                targetCalories,
                userInfo.getPreference() != null ? userInfo.getPreference() : "",
                apiKey);

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode root = response.getBody();
        List<MealDto> meals = new ArrayList<>();

        if (root == null || !root.has("meals") || root.get("meals").isNull()) {
            log.warn("No meals found in Spoonacular response for url: {}", url);
            return meals;
        }

        for (JsonNode mealNode : root.get("meals")) {
            long recipeId = (mealNode.has("id") && !mealNode.get("id").isNull())
                    ? mealNode.get("id").asLong()
                    : -1;
            if (recipeId == -1) {
                log.warn("Invalid meal id in response: {}", mealNode);
                continue;
            }

            String detailUrl = String.format(
                    "https://api.spoonacular.com/recipes/%d/information?includeNutrition=true&apiKey=%s",
                    recipeId, apiKey);

            ResponseEntity<JsonNode> detailResp = restTemplate.getForEntity(detailUrl, JsonNode.class);
            JsonNode details = detailResp.getBody();

            int protein = 0, carbs = 0, fat = 0, calories = 0;
            if (details != null && details.has("nutrition") && details.get("nutrition").has("nutrients")) {
                for (JsonNode nutrient : details.get("nutrition").get("nutrients")) {
                    String title = (nutrient.has("title") && !nutrient.get("title").isNull())
                            ? nutrient.get("title").asText()
                            : "";
                    int amount = (nutrient.has("amount") && !nutrient.get("amount").isNull())
                            ? nutrient.get("amount").asInt()
                            : 0;

                    switch (title) {
                        case "Protein" -> protein = amount;
                        case "Carbohydrates" -> carbs = amount;
                        case "Fat" -> fat = amount;
                        case "Calories" -> calories = amount;
                    }
                }
            }

            MealDto meal = new MealDto();
            meal.setTime("12:00 PM");

            // Title: prefer details.title, fallback mealNode.title, else default
            if (details != null && details.has("title") && !details.get("title").isNull()) {
                meal.setMeal(details.get("title").asText());
            } else if (mealNode.has("title") && !mealNode.get("title").isNull()) {
                meal.setMeal(mealNode.get("title").asText());
            } else {
                meal.setMeal("Unknown Meal");
            }

            meal.setProtein(protein);
            meal.setCarbs(carbs);
            meal.setFat(fat);
            meal.setCalories(calories);

            // Image fallback
            if (details != null && details.has("image") && !details.get("image").isNull()) {
                meal.setImage(details.get("image").asText());
            } else {
                meal.setImage("");
            }

            meals.add(meal);
        }
        return meals;
    }

    // ===========================
    // CALCULATE CALORIES (Null safe)
    // ===========================
    public int calculateTargetCalories(DietRequest req) {
        // Defaults to prevent NPE
        String gender = (req.getGender() != null) ? req.getGender().toLowerCase() : "male";
        String activityLevel = (req.getActivityLevel() != null)
                ? req.getActivityLevel().toLowerCase()
                : "sedentary";
        String goal = (req.getGoal() != null) ? req.getGoal().toLowerCase() : "maintain";

        // Calculate BMR (Mifflin-St Jeor)
        double bmr = 10 * req.getWeight() + 6.25 * req.getHeight() - 5 * req.getAge();
        bmr += gender.equals("male") ? 5 : -161;

        double activityFactor = switch (activityLevel) {
            case "sedentary" -> 1.2;
            case "active" -> 1.55;
            case "very active" -> 1.725;
            default -> 1.375;
        };

        int calories = (int) Math.round(bmr * activityFactor);

        // Apply goal adjustment
        switch (goal) {
            case "lose" -> calories -= 500;
            case "gain" -> calories += 500;
            case "maintain" -> {
            } // no change
        }

        return calories;
    }
}
