package com.gymai.plan_service.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymai.plan_service.dto.CompleteFitnessPlan;
import com.gymai.plan_service.dto.NutritionAnalysis;
import com.gymai.plan_service.dto.UserPlansResponse;
import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.entity.WorkoutPlan;
import com.gymai.plan_service.repository.UserRepository;
import com.gymai.plan_service.service.DietPlanService;
import com.gymai.plan_service.service.NutritionCalculatorService;
import com.gymai.plan_service.service.WorkoutPlanService;

// FitnessController.java
@RestController
@RequestMapping("/api/fitness")
@CrossOrigin(origins = "*")
public class FitnessController {

    @Autowired
    private DietPlanService dietPlanService;

    @Autowired
    private WorkoutPlanService workoutPlanService;

    @Autowired
    private UserRepository userRepository;

    // Generate complete fitness plan (diet + workout)
    @PostMapping("/generate-complete-plan")
    public ResponseEntity<CompleteFitnessPlan> generateCompletePlan(@RequestBody User user) {
        try {
            // Save or update user
            User savedUser = userRepository.save(user);

            // Generate diet plan
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(savedUser);

            // Generate workout plan
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);

            // Combine into complete plan
            CompleteFitnessPlan completePlan = new CompleteFitnessPlan();
            completePlan.setUser(savedUser);
            completePlan.setDietPlan(dietPlan);
            completePlan.setWorkoutPlan(workoutPlan);
            completePlan.setGeneratedDate(LocalDate.now());

            return ResponseEntity.ok(completePlan);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Generate only diet plan
    @PostMapping("/generate-diet-plan")
    public ResponseEntity<DietPlan> generateDietPlan(@RequestBody User user) {
        try {
            User savedUser = userRepository.save(user);
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(savedUser);
            return ResponseEntity.ok(dietPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Generate only workout plan
    @PostMapping("/generate-workout-plan")
    public ResponseEntity<WorkoutPlan> generateWorkoutPlan(@RequestBody User user) {
        try {
            User savedUser = userRepository.save(user);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);
            return ResponseEntity.ok(workoutPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Get user's existing plans
    @GetMapping("/user/{userId}/plans")
    public ResponseEntity<UserPlansResponse> getUserPlans(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Generate fresh plans
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(user);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);

            UserPlansResponse response = new UserPlansResponse();
            response.setUser(user);
            response.setDietPlan(dietPlan);
            response.setWorkoutPlan(workoutPlan);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Update user profile and regenerate plans
    @PutMapping("/user/{userId}/update-and-regenerate")
    public ResponseEntity<CompleteFitnessPlan> updateUserAndRegeneratePlans(
            @PathVariable Long userId, @RequestBody User updatedUser) {
        try {
            updatedUser.setUserId(userId);
            User savedUser = userRepository.save(updatedUser);

            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(savedUser);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);

            CompleteFitnessPlan completePlan = new CompleteFitnessPlan();
            completePlan.setUser(savedUser);
            completePlan.setDietPlan(dietPlan);
            completePlan.setWorkoutPlan(workoutPlan);
            completePlan.setGeneratedDate(LocalDate.now());

            return ResponseEntity.ok(completePlan);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Get nutrition analysis for a user
    @GetMapping("/user/{userId}/nutrition-analysis")
    public ResponseEntity<NutritionAnalysis> getNutritionAnalysis(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            NutritionCalculatorService nutritionCalculator = new NutritionCalculatorService();
            NutritionCalculatorService.NutritionalNeeds needs = nutritionCalculator.calculateNutritionalNeeds(user);

            NutritionAnalysis analysis = new NutritionAnalysis();
            analysis.setUserId(userId);
            analysis.setDailyCalories(needs.calories);
            analysis.setDailyProtein(needs.protein);
            analysis.setDailyCarbs(needs.carbs);
            analysis.setDailyFat(needs.fat);
            analysis.setBmr(calculateBMR(user));
            analysis.setTdee(needs.calories
                    + (user.getGoal().equals("WEIGHT_LOSS") ? 500 : user.getGoal().equals("WEIGHT_GAIN") ? -500 : 0));

            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private double calculateBMR(User user) {
        if ("MALE".equalsIgnoreCase(user.getGender())) {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
        } else {
            return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
        }
    }
}
