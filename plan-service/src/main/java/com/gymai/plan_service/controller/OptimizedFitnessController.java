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
import java.math.BigDecimal;
import java.math.RoundingMode;

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
  private CacheService cacheService;

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private DietPlanMapper dietPlanMapper;

  @Autowired
  private WorkoutPlanMapper workoutPlanMapper;

  // Helper method to round to 1 decimal place
  private double roundTo1Decimal(double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }

  // Check if user profile exists and is complete (with caching)
  @PostMapping("/user/profile-check")
  public ResponseEntity<UserProfileCheckDTO> checkUserProfile(@RequestBody UserProfileDTO userRequest) {
    log.info("Checking profile status for email: {}", userRequest.getEmail());

    try {
      // Check cache first
      UserProfileDTO cachedProfile = cacheService.getCachedUserProfile(userRequest.getEmail());
      if (cachedProfile != null) {
        log.debug("Found cached profile for email: {}", userRequest.getEmail());
        if (cachedProfile.isProfileComplete()) {
          return ResponseEntity.ok(UserProfileCheckDTO.complete(cachedProfile));
        } else {
          return ResponseEntity.ok(UserProfileCheckDTO.incomplete(cachedProfile));
        }
      }

      Optional<User> userOpt = userRepository.findByEmail(userRequest.getEmail());

      if (!userOpt.isPresent()) {
        log.info("User not found with email: {}", userRequest.getEmail());
        return ResponseEntity.ok(UserProfileCheckDTO.notFound());
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      // Cache the profile
      cacheService.cacheUserProfile(userRequest.getEmail(), userDTO);

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

  // Get user plans (optimized with Redis caching) - fetches existing or generates
  // if missing
  @PostMapping("/user/plans")
  public ResponseEntity<OptimizedPlansResponseDTO> getUserPlansOptimized(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching optimized plans for email: {}", userRequest.getEmail());

    try {
      // Check complete plans cache first
      OptimizedPlansResponseDTO cachedResponse = cacheService.getCachedPlansResponse(userRequest.getEmail());
      if (cachedResponse != null) {
        log.info("Retrieved complete plans from cache for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(cachedResponse);
      }

      Optional<User> userOpt = userRepository.findByEmail(userRequest.getEmail());

      if (!userOpt.isPresent()) {
        log.warn("User not found with email: {}", userRequest.getEmail());
        return ResponseEntity.notFound().build();
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      // Cache user profile if not already cached
      cacheService.cacheUserProfile(userRequest.getEmail(), userDTO);

      // Check if profile is complete
      if (!userDTO.isProfileComplete()) {
        log.info("User profile incomplete for email: {}", userRequest.getEmail());
        OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
        response.setUser(userDTO);
        response.setPlansExist(false);
        response.setSummary("Profile incomplete - please complete your profile first");
        return ResponseEntity.ok(response);
      }

      // Use parallel execution for better performance with caching
      CompletableFuture<SimpleDietPlanDTO> dietPlanFuture = CompletableFuture.supplyAsync(() -> {
        try {
          // Check cache first for DTO
          SimpleDietPlanDTO cachedDietPlan = cacheService.getCachedDietPlan(user.getUserId());
          if (cachedDietPlan != null) {
            return cachedDietPlan;
          }

          var existingDietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
          if (existingDietPlan == null) {
            log.info("Generating new diet plan for user: {}", userRequest.getEmail());
            var newDietPlan = dietPlanService.generateCustomDietPlan(user);
            return dietPlanMapper.toDTO(newDietPlan);
          }
          SimpleDietPlanDTO planDTO = dietPlanMapper.toDTO(existingDietPlan);
          // Cache the DTO result
          cacheService.cacheDietPlan(user.getUserId(), planDTO);
          return planDTO;
        } catch (Exception e) {
          log.error("Error processing diet plan for user: {}", user.getUserId(), e);
          throw new RuntimeException("Error processing diet plan", e);
        }
      });

      CompletableFuture<SimpleWorkoutPlanDTO> workoutPlanFuture = CompletableFuture.supplyAsync(() -> {
        try {
          // Check cache first for DTO
          SimpleWorkoutPlanDTO cachedWorkoutPlan = cacheService.getCachedWorkoutPlan(user.getUserId());
          if (cachedWorkoutPlan != null) {
            return cachedWorkoutPlan;
          }

          var existingWorkoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
          if (existingWorkoutPlan == null) {
            log.info("Generating new workout plan for user: {}", userRequest.getEmail());
            var newWorkoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
            return workoutPlanMapper.toDTO(newWorkoutPlan);
          }
          SimpleWorkoutPlanDTO planDTO = workoutPlanMapper.toDTO(existingWorkoutPlan);
          // Cache the DTO result
          cacheService.cacheWorkoutPlan(user.getUserId(), planDTO);
          return planDTO;
        } catch (Exception e) {
          log.error("Error processing workout plan for user: {}", user.getUserId(), e);
          throw new RuntimeException("Error processing workout plan", e);
        }
      });

      // Wait for both plans to complete
      SimpleDietPlanDTO dietPlanDTO = dietPlanFuture.join();
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanFuture.join();

      // Get nutrition analysis (with caching)
      NutritionAnalysis nutritionAnalysis = getCachedOrCalculateNutrition(user);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userDTO);
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userDTO, dietPlanDTO, workoutPlanDTO));

      // Cache the complete response
      cacheService.cachePlansResponse(userRequest.getEmail(), response);

      log.info("Successfully fetched/generated and cached plans for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error fetching plans for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse(userRequest.getEmail(), "Error fetching plans"));
    }
  }

  @PostMapping("/user/complete-profile")
  public ResponseEntity<OptimizedPlansResponseDTO> completeUserProfile(@RequestBody UserProfileDTO userProfile) {
    log.info("Completing (create/update) profile and generating plans for email: {}", userProfile.getEmail());

    try {
      // Find existing user or create a new one
      User user = userRepository.findByEmail(userProfile.getEmail()).orElse(new User());

      // Update user details from DTO
      updateUserFromDTO(user, userProfile);
      final User savedUser = userRepository.save(user);
      log.info("User profile saved for email: {} with userId: {}", userProfile.getEmail(), savedUser.getUserId());

      // Invalidate old caches for this user (important before regenerating)
      cacheService.invalidateAllUserCache(userProfile.getEmail(), savedUser.getUserId());

      // Generate fresh diet + workout plans in parallel
      CompletableFuture<SimpleDietPlanDTO> dietPlanFuture = CompletableFuture
          .supplyAsync(() -> dietPlanMapper.toDTO(dietPlanService.regenerateDietPlan(savedUser)));

      CompletableFuture<SimpleWorkoutPlanDTO> workoutPlanFuture = CompletableFuture
          .supplyAsync(() -> workoutPlanMapper.toDTO(workoutPlanService.regenerateWorkoutPlan(savedUser)));

      // Wait for both to finish
      SimpleDietPlanDTO dietPlanDTO = dietPlanFuture.join();
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanFuture.join();

      // Nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(savedUser);
      NutritionAnalysis nutritionAnalysis = createNutritionAnalysis(savedUser, needs);

      // Cache nutrition analysis
      cacheService.cacheNutritionAnalysis(savedUser.getUserId(), nutritionAnalysis);

      // Build response
      UserProfileDTO updatedUserDTO = userMapper.toDTO(savedUser);
      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(updatedUserDTO);
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(updatedUserDTO, dietPlanDTO, workoutPlanDTO));

      // Cache everything
      cacheService.cacheUserProfile(userProfile.getEmail(), updatedUserDTO);
      cacheService.cachePlansResponse(userProfile.getEmail(), response);

      log.info("Successfully completed profile (create/update) and generated plans for email: {}",
          userProfile.getEmail());
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error completing profile for email: {}", userProfile.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(createErrorResponse(userProfile.getEmail(), "Error completing profile"));
    }
  }

  // Update user profile only (with cache update)
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

      // Update cache
      cacheService.cacheUserProfile(userProfile.getEmail(), updatedProfile);
      // Invalidate plans response cache since profile changed
      cacheService.invalidateUserCache(userProfile.getEmail());

      log.info("Profile updated successfully for email: {}", userProfile.getEmail());
      return ResponseEntity.ok(updatedProfile);

    } catch (Exception e) {
      log.error("Error updating profile for email: {}", userProfile.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate diet plan only (with cache invalidation)
  @PostMapping("/user/regenerate-diet")
  public ResponseEntity<SimpleDietPlanDTO> regenerateDietPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Regenerating diet plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Invalidate relevant caches
      cacheService.invalidateUserCache(userRequest.getEmail());
      cacheService.invalidateUserPlansCache(user.getUserId());

      var dietPlan = dietPlanService.regenerateDietPlan(user);
      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);

      log.info("Diet plan regenerated successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating diet plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate workout plan only (with cache invalidation)
  @PostMapping("/user/regenerate-workout")
  public ResponseEntity<SimpleWorkoutPlanDTO> regenerateWorkoutPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Regenerating workout plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Invalidate relevant caches
      cacheService.invalidateUserCache(userRequest.getEmail());
      cacheService.invalidateUserPlansCache(user.getUserId());

      var workoutPlan = workoutPlanService.regenerateWorkoutPlan(user);
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      log.info("Workout plan regenerated successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating workout plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only diet plan (with caching)
  @PostMapping("/user/diet-plan")
  public ResponseEntity<SimpleDietPlanDTO> getDietPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching diet plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Check cache first for DTO
      SimpleDietPlanDTO cachedPlan = cacheService.getCachedDietPlan(user.getUserId());
      if (cachedPlan != null) {
        log.debug("Retrieved diet plan from cache for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(cachedPlan);
      }

      var dietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
      if (dietPlan == null) {
        dietPlan = dietPlanService.generateCustomDietPlan(user);
      }

      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);

      // Cache the DTO result
      cacheService.cacheDietPlan(user.getUserId(), dietPlanDTO);

      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching diet plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only workout plan (with caching)
  @PostMapping("/user/workout-plan")
  public ResponseEntity<SimpleWorkoutPlanDTO> getWorkoutPlan(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching workout plan for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Check cache first for DTO
      SimpleWorkoutPlanDTO cachedPlan = cacheService.getCachedWorkoutPlan(user.getUserId());
      if (cachedPlan != null) {
        log.debug("Retrieved workout plan from cache for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(cachedPlan);
      }

      var workoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
      if (workoutPlan == null) {
        workoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
      }

      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      // Cache the DTO result
      cacheService.cacheWorkoutPlan(user.getUserId(), workoutPlanDTO);

      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching workout plan for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Delete user plans (with cache invalidation)
  @PostMapping("/user/plans/delete")
  public ResponseEntity<String> deleteUserPlans(@RequestBody UserProfileDTO userRequest) {
    log.info("Deleting plans for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      // Invalidate all caches first
      cacheService.invalidateAllUserCache(userRequest.getEmail(), user.getUserId());

      CompletableFuture<Void> deleteDiet = CompletableFuture
          .runAsync(() -> dietPlanService.deleteUserDietPlans(user.getUserId()));
      CompletableFuture<Void> deleteWorkout = CompletableFuture
          .runAsync(() -> workoutPlanService.deleteUserWorkoutPlans(user.getUserId()));

      CompletableFuture.allOf(deleteDiet, deleteWorkout).join();

      log.info("Plans deleted successfully for email: {}", userRequest.getEmail());
      return ResponseEntity.ok("Plans deleted successfully");

    } catch (Exception e) {
      log.error("Error deleting plans for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting plans");
    }
  }

  // Get user profile only (with caching)
  @PostMapping("/user/profile")
  public ResponseEntity<UserProfileDTO> getUserProfile(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching user profile for email: {}", userRequest.getEmail());

    try {
      // Check cache first
      UserProfileDTO cachedProfile = cacheService.getCachedUserProfile(userRequest.getEmail());
      if (cachedProfile != null) {
        log.debug("Retrieved user profile from cache for email: {}", userRequest.getEmail());
        return ResponseEntity.ok(cachedProfile);
      }

      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      UserProfileDTO userDTO = userMapper.toDTO(user);

      // Cache the result
      cacheService.cacheUserProfile(userRequest.getEmail(), userDTO);

      return ResponseEntity.ok(userDTO);

    } catch (Exception e) {
      log.error("Error fetching user profile for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Health check endpoint (also checks Redis connectivity)
  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    boolean redisHealthy = cacheService.isCacheAvailable();
    String healthStatus = "Fitness Plan Service is running";
    if (redisHealthy) {
      healthStatus += " - Redis Cache: OK";
    } else {
      healthStatus += " - Redis Cache: UNAVAILABLE";
    }
    return ResponseEntity.ok(healthStatus);
  }

  // Cache management endpoint (for debugging/admin)
  @PostMapping("/admin/cache/clear")
  public ResponseEntity<String> clearCache() {
    try {
      cacheService.clearAllCache();
      return ResponseEntity.ok("Cache cleared successfully");
    } catch (Exception e) {
      log.error("Error clearing cache", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error clearing cache");
    }
  }

  // Get nutrition analysis only (with caching)
  @PostMapping("/user/nutrition-analysis")
  public ResponseEntity<NutritionAnalysis> getNutritionAnalysis(@RequestBody UserProfileDTO userRequest) {
    log.info("Fetching nutrition analysis for email: {}", userRequest.getEmail());

    try {
      User user = userRepository.findByEmail(userRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("User not found with email: " + userRequest.getEmail()));

      NutritionAnalysis nutritionAnalysis = getCachedOrCalculateNutrition(user);
      return ResponseEntity.ok(nutritionAnalysis);

    } catch (Exception e) {
      log.error("Error fetching nutrition analysis for email: {}", userRequest.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // ===== HELPER METHODS =====

  private NutritionAnalysis getCachedOrCalculateNutrition(User user) {
    // Check cache first
    NutritionAnalysis cachedAnalysis = cacheService.getCachedNutritionAnalysis(user.getUserId());
    if (cachedAnalysis != null) {
      return cachedAnalysis;
    }

    // Calculate and cache
    var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
    NutritionAnalysis analysis = createNutritionAnalysis(user, needs);
    cacheService.cacheNutritionAnalysis(user.getUserId(), analysis);

    return analysis;
  }

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
    nutritionAnalysis.setDailyCalories(roundTo1Decimal(needs.calories));
    nutritionAnalysis.setDailyProtein(roundTo1Decimal(needs.protein));
    nutritionAnalysis.setDailyCarbs(roundTo1Decimal(needs.carbs));
    nutritionAnalysis.setDailyFat(roundTo1Decimal(needs.fat));
    nutritionAnalysis.setBmr(roundTo1Decimal(calculateBMR(user)));
    nutritionAnalysis.setTdee(roundTo1Decimal(needs.calories));
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

    return String.format("Complete fitness plan for %s - Goal: %s | Daily Calories: %.1f | Workout Days: %d/week",
        user.getName(), user.getGoal(), dietPlan.getDailyCalorieTarget(), workoutDays);
  }

  private OptimizedPlansResponseDTO createErrorResponse(String email, String message) {
    OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
    response.setPlansExist(false);
    response.setSummary("Error: " + message + " for " + email);
    return response;
  }
}