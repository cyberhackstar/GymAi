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

import lombok.extern.slf4j.Slf4j;

// FitnessController.java
@RestController
@RequestMapping("/api/fitness")
@CrossOrigin(origins = "*")
@Slf4j
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
        log.info("Generating complete fitness plan for user: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(savedUser);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);

            CompleteFitnessPlan completePlan = new CompleteFitnessPlan();
            completePlan.setUser(savedUser);
            completePlan.setDietPlan(dietPlan);
            completePlan.setWorkoutPlan(workoutPlan);
            completePlan.setGeneratedDate(LocalDate.now());

            log.info("Successfully generated complete plan for userId={}", savedUser.getUserId());
            return ResponseEntity.ok(completePlan);

        } catch (Exception e) {
            log.error("Error generating complete fitness plan for user: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Generate only diet plan
    @PostMapping("/generate-diet-plan")
    public ResponseEntity<DietPlan> generateDietPlan(@RequestBody User user) {
        log.info("Generating diet plan for user: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(savedUser);
            log.info("Diet plan generated for userId={}", savedUser.getUserId());
            return ResponseEntity.ok(dietPlan);
        } catch (Exception e) {
            log.error("Error generating diet plan for user: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Generate only workout plan
    @PostMapping("/generate-workout-plan")
    public ResponseEntity<WorkoutPlan> generateWorkoutPlan(@RequestBody User user) {
        log.info("Generating workout plan for user: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);
            log.info("Workout plan generated for userId={}", savedUser.getUserId());
            return ResponseEntity.ok(workoutPlan);
        } catch (Exception e) {
            log.error("Error generating workout plan for user: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get user's existing plans
    @GetMapping("/user/{userId}/plans")
    public ResponseEntity<UserPlansResponse> getUserPlans(@PathVariable Long userId) {
        log.info("Fetching plans for userId={}", userId);
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                log.warn("User not found with id={}", userId);
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            DietPlan dietPlan = dietPlanService.generateCustomDietPlan(user);
            WorkoutPlan workoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);

            UserPlansResponse response = new UserPlansResponse();
            response.setUser(user);
            response.setDietPlan(dietPlan);
            response.setWorkoutPlan(workoutPlan);

            log.info("Plans successfully fetched for userId={}", userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching plans for userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Update user profile and regenerate plans
    @PutMapping("/user/{userId}/update-and-regenerate")
    public ResponseEntity<CompleteFitnessPlan> updateUserAndRegeneratePlans(
            @PathVariable Long userId, @RequestBody User updatedUser) {
        log.info("Updating userId={} and regenerating plans", userId);
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

            log.info("Successfully updated and regenerated plans for userId={}", userId);
            return ResponseEntity.ok(completePlan);

        } catch (Exception e) {
            log.error("Error updating user and regenerating plans for userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get nutrition analysis for a user
    @GetMapping("/user/{userId}/nutrition-analysis")
    public ResponseEntity<NutritionAnalysis> getNutritionAnalysis(@PathVariable Long userId) {
        log.info("Calculating nutrition analysis for userId={}", userId);
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                log.warn("User not found with id={}", userId);
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

            log.info("Nutrition analysis calculated for userId={}", userId);
            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            log.error("Error calculating nutrition analysis for userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
