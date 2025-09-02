package com.gymai.plan_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.mapper.*;
import com.gymai.plan_service.repository.UserRepository;
import com.gymai.plan_service.service.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/fitness")
@CrossOrigin(origins = "*")
@Slf4j
public class OptimizedFitnessController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DietPlanService dietPlanService;

  @Autowired
  private WorkoutPlanService workoutPlanService;

  @Autowired
  private NutritionCalculatorService nutritionCalculatorService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private DietPlanMapper dietPlanMapper;

  @Autowired
  private WorkoutPlanMapper workoutPlanMapper;

  // Check if user profile exists and is complete
  @PostMapping("/user/profile-check")
  public ResponseEntity<UserProfileCheckDTO> checkUserProfile(@RequestBody UserProfileDTO userRequest) {
    log.info("Checking profile status for email: {}", userRequest.getEmail());

    try {
      Optional<User> userOpt = userRepository.findByEmail(userRequest.getEmail());

      if (!userOpt.isPresent()) {
        log.info("User not found with email: {}", userRequest.getEmail());
        return ResponseEntity.ok(UserProfileCheckDTO.notFound());
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      if (userDTO.isProfileComplete()) {
        log.info("Profile complete for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(UserProfileCheckDTO.complete(userDTO));
      } else {
        log.info("Profile incomplete for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(UserProfileCheckDTO.incomplete(userDTO));
      }

    } catch (Exception e) {
      log.error("Error checking user profile for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(UserProfileCheckDTO.notFound());
    }
  }

  // Get user plans (optimized) - fetches existing or generates if missing
  @PostMapping("/user/plans")
  public ResponseEntity<OptimizedPlansResponseDTO> getUserPlansOptimized(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching optimized plans for email: {}", userRequest.getEmail());

    try {
      Optional<User> userOpt = userRepository.findByEmail(userRequest.getEmail());

      if (!userOpt.isPresent()) {
        log.warn("User not found with email: {}", userRequest.getEmail());
        return ResponseEntity.notFound().build();
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      // Check if profile is complete
      if (!userDTO.isProfileComplete()) {
        log.info("User profile incomplete for email: {}", userRequest.getEmail());
        OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
        response.setUser(userDTO);
        response.setPlansExist(false);
        response.setSummary("Profile incomplete - please complete your profile first");
        return ResponseEntity.ok(response);
      }

      // Use parallel execution for better performance
      CompletableFuture<SimpleDietPlanDTO> dietPlanFuture = CompletableFuture.supplyAsync(() -> {
        var existingDietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
        if (existingDietPlan == null) {
          log.info("Generating new diet plan for user: {}", userRequest.getEmail());
          var newDietPlan = dietPlanService.generateCustomDietPlan(user);
          return dietPlanMapper.toDTO(newDietPlan);
        }
        return dietPlanMapper.toDTO(existingDietPlan);
      });

      CompletableFuture<SimpleWorkoutPlanDTO> workoutPlanFuture = CompletableFuture.supplyAsync(() -> {
        var existingWorkoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
        if (existingWorkoutPlan == null) {
          log.info("Generating new workout plan for user: {}", userRequest.getEmail());
          var newWorkoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
          return workoutPlanMapper.toDTO(newWorkoutPlan);
        }
        return workoutPlanMapper.toDTO(existingWorkoutPlan);
      });

      // Wait for both plans to complete
      SimpleDietPlanDTO dietPlanDTO = dietPlanFuture.join();
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanFuture.join();

      // Get nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      NutritionAnalysis nutritionAnalysis = createNutritionAnalysis(user, needs);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userDTO);
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userDTO, dietPlanDTO, workoutPlanDTO));

      log.info("Successfully fetched/generated plans for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error fetching plans for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse(userRequest.getEmail(), "Error fetching plans"));
    }
  }

  // Create/Update user profile and generate plans
  @PostMapping("/user/complete-profile")
  public ResponseEntity<OptimizedPlansResponseDTO> completeUserProfile(@RequestBody UserProfileDTO userProfile) {
    log.info("Completing profile and generating plans for email: {}", userProfile.getEmail());

    try {
      // Find or create user
      User user = userRepository.findByEmail(userProfile.getEmail()).orElse(new User());

      // Update user details from DTO
      updateUserFromDTO(user, userProfile);
      final User savedUser = userRepository.save(user);
      log.info("User profile saved for email: {} with userId: {}", userProfile.getEmail(), savedUser.getUserId());

      // Generate fresh plans in parallel
      CompletableFuture<SimpleDietPlanDTO> dietPlanFuture = CompletableFuture.supplyAsync(() -> {
        var dietPlan = dietPlanService.regenerateDietPlan(savedUser);
        return dietPlanMapper.toDTO(dietPlan);
      });

      CompletableFuture<SimpleWorkoutPlanDTO> workoutPlanFuture = CompletableFuture.supplyAsync(() -> {
        var workoutPlan = workoutPlanService.regenerateWorkoutPlan(savedUser);
        return workoutPlanMapper.toDTO(workoutPlan);
      });

      // Wait for completion
      SimpleDietPlanDTO dietPlanDTO = dietPlanFuture.join();
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanFuture.join();

      // Get nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(savedUser);
      NutritionAnalysis nutritionAnalysis = createNutritionAnalysis(savedUser, needs);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userMapper.toDTO(savedUser));
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userMapper.toDTO(savedUser), dietPlanDTO, workoutPlanDTO));

      log.info("Successfully completed profile and generated plans for email: {}", userProfile.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error completing profile for email: {}", userProfile.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse(userProfile.getEmail(), "Error completing profile"));
    }
  }

  // Update user profile only (without regenerating plans)
  @PutMapping("/user/update-profile")
  public ResponseEntity<UserProfileDTO> updateUserProfile(@RequestBody UserProfileDTO userProfile) {
    log.info("Updating profile for email: {}", userProfile.getEmail());

    try {
      User user = userRepository.findByEmail(userProfile.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userProfile.getEmail()));

      // Update user details
      updateUserFromDTO(user, userProfile);
      user = userRepository.save(user);
      UserProfileDTO updatedProfile = userMapper.toDTO(user);

      log.info("Profile updated successfully for email: {}", userProfile.getEmail());
      return ResponseEntity.ok(updatedProfile);

    } catch (Exception e) {
      log.error("Error updating profile for email: {}", userProfile.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate diet plan only
  @PostMapping("/user/regenerate-diet")
  public ResponseEntity<SimpleDietPlanDTO> regenerateDietPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Regenerating diet plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      var dietPlan = dietPlanService.regenerateDietPlan(user);
      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);

      log.info("Diet plan regenerated successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating diet plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate workout plan only
  @PostMapping("/user/regenerate-workout")
  public ResponseEntity<SimpleWorkoutPlanDTO> regenerateWorkoutPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Regenerating workout plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      var workoutPlan = workoutPlanService.regenerateWorkoutPlan(user);
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      log.info("Workout plan regenerated successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating workout plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Update profile and regenerate all plans
  @PutMapping("/user/update-and-regenerate")
  public ResponseEntity<OptimizedPlansResponseDTO> updateProfileAndRegeneratePlans(
      @RequestBody UserProfileDTO userProfile) {
    log.info("Updating profile and regenerating plans for email: {}", userProfile.getEmail());

    try {
      User user = userRepository.findByEmail(userProfile.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userProfile.getEmail()));

      // Update user details
      updateUserFromDTO(user, userProfile);
      final User savedUser = userRepository.save(user);

      // Regenerate plans in parallel
      CompletableFuture<SimpleDietPlanDTO> dietPlanFuture = CompletableFuture.supplyAsync(() -> {
        var dietPlan = dietPlanService.regenerateDietPlan(savedUser);
        return dietPlanMapper.toDTO(dietPlan);
      });

      CompletableFuture<SimpleWorkoutPlanDTO> workoutPlanFuture = CompletableFuture.supplyAsync(() -> {
        var workoutPlan = workoutPlanService.regenerateWorkoutPlan(savedUser);
        return workoutPlanMapper.toDTO(workoutPlan);
      });

      SimpleDietPlanDTO dietPlanDTO = dietPlanFuture.join();
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanFuture.join();

      // Get updated nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(savedUser);
      NutritionAnalysis nutritionAnalysis = createNutritionAnalysis(savedUser, needs);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userMapper.toDTO(savedUser));
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userMapper.toDTO(savedUser), dietPlanDTO, workoutPlanDTO));

      log.info("Successfully updated profile and regenerated plans for email: {}", userProfile.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error updating profile and regenerating plans for email: {}", userProfile.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse(userProfile.getEmail(), "Error updating profile and regenerating plans"));
    }
  }

  // Get nutrition analysis only
  @PostMapping("/user/nutrition-analysis")
  public ResponseEntity<NutritionAnalysis> getNutritionAnalysis(@RequestBody UserProfileDTO userRequest) {
    log.info("Getting nutrition analysis for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      NutritionAnalysis analysis = createNutritionAnalysis(user, needs);

      return ResponseEntity.ok(analysis);

    } catch (Exception e) {
      log.error("Error getting nutrition analysis for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only diet plan
  @PostMapping("/user/diet-plan")
  public ResponseEntity<SimpleDietPlanDTO> getDietPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching diet plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      var dietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
      if (dietPlan == null) {
        dietPlan = dietPlanService.generateCustomDietPlan(user);
      }

      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);
      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching diet plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only workout plan
  @PostMapping("/user/workout-plan")
  public ResponseEntity<SimpleWorkoutPlanDTO> getWorkoutPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching workout plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      var workoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
      if (workoutPlan == null) {
        workoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
      }

      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);
      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching workout plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/user/plans/delete")
  public ResponseEntity<String> deleteUserPlans(@RequestBody UserProfileDTO userRequest) {
    log.info("Deleting plans for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Now this will work because cascade delete handles child records automatically
      CompletableFuture<Void> deleteDiet = CompletableFuture
          .runAsync(() -> dietPlanService.deleteUserPlans(user.getUserId()));
      CompletableFuture<Void> deleteWorkout = CompletableFuture
          .runAsync(() -> workoutPlanService.deleteUserPlans(user.getUserId()));

      CompletableFuture.allOf(deleteDiet, deleteWorkout).join();

      log.info("Plans deleted successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok("Plans deleted successfully");

    } catch (Exception e) {
      log.error("Error deleting plans for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting plans");
    }
  }

  // Get user profile only
  @PostMapping("/user/profile")
  public ResponseEntity<UserProfileDTO> getUserProfile(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching user profile for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      UserProfileDTO userDTO = userMapper.toDTO(user);
      return ResponseEntity.ok(userDTO);

    } catch (Exception e) {
      log.error("Error fetching user profile for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Health check endpoint (public)
  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Fitness Plan Service is running");
  }

  // ===== HELPER METHODS =====

  private void updateUserFromDTO(User user, UserProfileDTO userProfile) {
    user.setEmail(userProfile.getEmail());
    user.setName(userProfile.getName());
    user.setAge(userProfile.getAge());
    user.setHeight(userProfile.getHeight());
    user.setWeight(userProfile.getWeight());
    user.setGender(userProfile.getGender());
    user.setGoal(userProfile.getGoal());
    user.setActivityLevel(userProfile.getActivityLevel());
    user.setPreference(userProfile.getPreference());
  }

  private NutritionAnalysis createNutritionAnalysis(User user, NutritionCalculatorService.NutritionalNeeds needs) {
    NutritionAnalysis nutritionAnalysis = new NutritionAnalysis();
    nutritionAnalysis.setUserId(user.getUserId());
    nutritionAnalysis.setDailyCalories(needs.calories);
    nutritionAnalysis.setDailyProtein(needs.protein);
    nutritionAnalysis.setDailyCarbs(needs.carbs);
    nutritionAnalysis.setDailyFat(needs.fat);
    nutritionAnalysis.setBmr(calculateBMR(user));
    nutritionAnalysis.setTdee(needs.calories);
    return nutritionAnalysis;
  }

  private double calculateBMR(User user) {
    if ("MALE".equalsIgnoreCase(user.getGender())) {
      return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
    } else {
      return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
    }
  }

  private String generateSummary(UserProfileDTO user, SimpleDietPlanDTO dietPlan, SimpleWorkoutPlanDTO workoutPlan) {
    if (dietPlan == null || workoutPlan == null) {
      return String.format("Profile for %s", user.getName());
    }

    long workoutDays = workoutPlan.getWeeklyPlan().stream()
        .filter(day -> !day.isRestDay())
        .count();

    return String.format("Complete fitness plan for %s - Goal: %s | Daily Calories: %.0f | Workout Days: %d/week",
        user.getName(), user.getGoal(), dietPlan.getDailyCalorieTarget(), workoutDays);
  }

  private OptimizedPlansResponseDTO createErrorResponse(String email, String message) {
    OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
    response.setPlansExist(false);
    response.setSummary("Error: " + message + " for " + email);
    return response;
  }
}