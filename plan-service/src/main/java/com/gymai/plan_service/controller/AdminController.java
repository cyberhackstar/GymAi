package com.gymai.plan_service.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymai.plan_service.entity.Exercise;
import com.gymai.plan_service.entity.Food;
import com.gymai.plan_service.repository.ExerciseRepository;
import com.gymai.plan_service.repository.FoodRepository;

// Additional utility controller for food and exercise management
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    // Add custom food
    @PostMapping("/foods")
    public ResponseEntity<Food> addFood(@RequestBody Food food) {
        try {
            Food savedFood = foodRepository.save(food);
            return ResponseEntity.ok(savedFood);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Add custom exercise
    @PostMapping("/exercises")
    public ResponseEntity<Exercise> addExercise(@RequestBody Exercise exercise) {
        try {
            Exercise savedExercise = exerciseRepository.save(exercise);
            return ResponseEntity.ok(savedExercise);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all foods
    @GetMapping("/foods")
    public ResponseEntity<List<Food>> getAllFoods() {
        try {
            List<Food> foods = foodRepository.findAll();
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all exercises
    @GetMapping("/exercises")
    public ResponseEntity<List<Exercise>> getAllExercises() {
        try {
            List<Exercise> exercises = exerciseRepository.findAll();
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get foods by diet type
    @GetMapping("/foods/diet/{dietType}")
    public ResponseEntity<List<Food>> getFoodsByDietType(@PathVariable String dietType) {
        try {
            // Convert diet type to list for the query
            List<String> dietTypes = Arrays.asList(dietType.toUpperCase());
            List<Food> foods = foodRepository.findAll().stream()
                    .filter(food -> dietTypes.contains(food.getDietType()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(foods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get exercises by category
    @GetMapping("/exercises/category/{category}")
    public ResponseEntity<List<Exercise>> getExercisesByCategory(@PathVariable String category) {
        try {
            List<Exercise> exercises = exerciseRepository.findByCategory(category.toUpperCase());
            return ResponseEntity.ok(exercises);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Database stats endpoint
    @GetMapping("/stats")
    public ResponseEntity<DatabaseStats> getDatabaseStats() {
        try {
            DatabaseStats stats = new DatabaseStats();
            stats.setTotalFoods(foodRepository.count());
            stats.setTotalExercises(exerciseRepository.count());
            stats.setVegFoods(foodRepository.findAll().stream()
                    .filter(food -> "VEG".equals(food.getDietType()))
                    .count());
            stats.setNonVegFoods(foodRepository.findAll().stream()
                    .filter(food -> "NON_VEG".equals(food.getDietType()))
                    .count());
            stats.setVeganFoods(foodRepository.findAll().stream()
                    .filter(food -> "VEGAN".equals(food.getDietType()))
                    .count());
            stats.setCardioExercises(exerciseRepository.findByCategory("CARDIO").size());
            stats.setStrengthExercises(exerciseRepository.findByCategory("STRENGTH").size());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

class DatabaseStats {
    private long totalFoods;
    private long totalExercises;
    private long vegFoods;
    private long nonVegFoods;
    private long veganFoods;
    private long cardioExercises;
    private long strengthExercises;

    // Getters and Setters
    public long getTotalFoods() {
        return totalFoods;
    }

    public void setTotalFoods(long totalFoods) {
        this.totalFoods = totalFoods;
    }

    public long getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(long totalExercises) {
        this.totalExercises = totalExercises;
    }

    public long getVegFoods() {
        return vegFoods;
    }

    public void setVegFoods(long vegFoods) {
        this.vegFoods = vegFoods;
    }

    public long getNonVegFoods() {
        return nonVegFoods;
    }

    public void setNonVegFoods(long nonVegFoods) {
        this.nonVegFoods = nonVegFoods;
    }

    public long getVeganFoods() {
        return veganFoods;
    }

    public void setVeganFoods(long veganFoods) {
        this.veganFoods = veganFoods;
    }

    public long getCardioExercises() {
        return cardioExercises;
    }

    public void setCardioExercises(long cardioExercises) {
        this.cardioExercises = cardioExercises;
    }

    public long getStrengthExercises() {
        return strengthExercises;
    }

    public void setStrengthExercises(long strengthExercises) {
        this.strengthExercises = strengthExercises;
    }
}