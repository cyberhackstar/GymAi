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
  @GetMapping("/user/profile-check")
  public ResponseEntity<UserProfileCheckDTO> checkUserProfile(@RequestParam String email) {
    log.info("Checking profile status for email: {}", email);

    try {
      Optional<User> userOpt = userRepository.findByEmail(email);

      if (!userOpt.isPresent()) {
        log.info("User not found with email: {}", email);
        return ResponseEntity.ok(UserProfileCheckDTO.notFound());
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      if (userDTO.isProfileComplete()) {
        log.info("Profile complete for email: {}", email);
        return ResponseEntity.ok(UserProfileCheckDTO.complete(userDTO));
      } else {
        log.info("Profile incomplete for email: {}", email);
        return ResponseEntity.ok(UserProfileCheckDTO.incomplete(userDTO));
      }

    } catch (Exception e) {
      log.error("Error checking user profile for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(UserProfileCheckDTO.notFound());
    }
  }

  // Get user plans (optimized) - fetches existing or generates if missing
  @GetMapping("/user/plans")
  public ResponseEntity<OptimizedPlansResponseDTO> getUserPlansOptimized(@RequestParam String email) {
    log.info("Fetching optimized plans for email: {}", email);

    try {
      Optional<User> userOpt = userRepository.findByEmail(email);

      if (!userOpt.isPresent()) {
        log.warn("User not found with email: {}", email);
        return ResponseEntity.notFound().build();
      }

      User user = userOpt.get();
      UserProfileDTO userDTO = userMapper.toDTO(user);

      // Check if profile is complete
      if (!userDTO.isProfileComplete()) {
        log.info("User profile incomplete for email: {}", email);
        OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
        response.setUser(userDTO);
        response.setPlansExist(false);
        response.setSummary("Profile incomplete - please complete your profile first");
        return ResponseEntity.ok(response);
      }

      // Fetch existing plans
      var existingDietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
      var existingWorkoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());

      SimpleDietPlanDTO dietPlanDTO = null;
      SimpleWorkoutPlanDTO workoutPlanDTO = null;

      // Generate missing plans
      if (existingDietPlan == null) {
        log.info("Generating new diet plan for user: {}", email);
        var newDietPlan = dietPlanService.generateCustomDietPlan(user);
        dietPlanDTO = dietPlanMapper.toDTO(newDietPlan);
      } else {
        dietPlanDTO = dietPlanMapper.toDTO(existingDietPlan);
      }

      if (existingWorkoutPlan == null) {
        log.info("Generating new workout plan for user: {}", email);
        var newWorkoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
        workoutPlanDTO = workoutPlanMapper.toDTO(newWorkoutPlan);
      } else {
        workoutPlanDTO = workoutPlanMapper.toDTO(existingWorkoutPlan);
      }

      // Get nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      NutritionAnalysis nutritionAnalysis = new NutritionAnalysis();
      nutritionAnalysis.setUserId(user.getUserId());
      nutritionAnalysis.setDailyCalories(needs.calories);
      nutritionAnalysis.setDailyProtein(needs.protein);
      nutritionAnalysis.setDailyCarbs(needs.carbs);
      nutritionAnalysis.setDailyFat(needs.fat);
      nutritionAnalysis.setBmr(calculateBMR(user));
      nutritionAnalysis.setTdee(needs.calories);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userDTO);
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userDTO, dietPlanDTO, workoutPlanDTO));

      log.info("Successfully fetched/generated plans for email: {}", email);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error fetching plans for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Create/Update user profile and generate plans
  @PostMapping("/user/complete-profile")
  public ResponseEntity<OptimizedPlansResponseDTO> completeUserProfile(
      @RequestParam String email,
      @RequestBody UserProfileDTO userProfile) {
    log.info("Completing profile and generating plans for email: {}", email);

    try {
      // Find or create user
      User user = userRepository.findByEmail(email).orElse(new User());

      // Update user details from DTO
      user.setEmail(email);
      user.setName(userProfile.getName());
      user.setAge(userProfile.getAge());
      user.setHeight(userProfile.getHeight());
      user.setWeight(userProfile.getWeight());
      user.setGender(userProfile.getGender());
      user.setGoal(userProfile.getGoal());
      user.setActivityLevel(userProfile.getActivityLevel());
      user.setPreference(userProfile.getPreference());

      user = userRepository.save(user);
      log.info("User profile saved for email: {} with userId: {}", email, user.getUserId());

      // Generate fresh plans (delete existing ones)
      var dietPlan = dietPlanService.regenerateDietPlan(user);
      var workoutPlan = workoutPlanService.regenerateWorkoutPlan(user);

      // Convert to DTOs
      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      // Get nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      NutritionAnalysis nutritionAnalysis = new NutritionAnalysis();
      nutritionAnalysis.setUserId(user.getUserId());
      nutritionAnalysis.setDailyCalories(needs.calories);
      nutritionAnalysis.setDailyProtein(needs.protein);
      nutritionAnalysis.setDailyCarbs(needs.carbs);
      nutritionAnalysis.setDailyFat(needs.fat);
      nutritionAnalysis.setBmr(calculateBMR(user));
      nutritionAnalysis.setTdee(needs.calories);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userMapper.toDTO(user));
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userMapper.toDTO(user), dietPlanDTO, workoutPlanDTO));

      log.info("Successfully completed profile and generated plans for email: {}", email);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error completing profile for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Update user profile only (without regenerating plans)
  @PutMapping("/user/update-profile")
  public ResponseEntity<UserProfileDTO> updateUserProfile(
      @RequestParam String email,
      @RequestBody UserProfileDTO userProfile) {
    log.info("Updating profile for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      // Update user details
      user.setName(userProfile.getName());
      user.setAge(userProfile.getAge());
      user.setHeight(userProfile.getHeight());
      user.setWeight(userProfile.getWeight());
      user.setGender(userProfile.getGender());
      user.setGoal(userProfile.getGoal());
      user.setActivityLevel(userProfile.getActivityLevel());
      user.setPreference(userProfile.getPreference());

      user = userRepository.save(user);
      UserProfileDTO updatedProfile = userMapper.toDTO(user);

      log.info("Profile updated successfully for email: {}", email);
      return ResponseEntity.ok(updatedProfile);

    } catch (Exception e) {
      log.error("Error updating profile for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate diet plan only
  @PostMapping("/user/regenerate-diet")
  public ResponseEntity<SimpleDietPlanDTO> regenerateDietPlan(@RequestParam String email) {
    log.info("Regenerating diet plan for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      var dietPlan = dietPlanService.regenerateDietPlan(user);
      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);

      log.info("Diet plan regenerated successfully for email: {}", email);
      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating diet plan for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Regenerate workout plan only
  @PostMapping("/user/regenerate-workout")
  public ResponseEntity<SimpleWorkoutPlanDTO> regenerateWorkoutPlan(@RequestParam String email) {
    log.info("Regenerating workout plan for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      var workoutPlan = workoutPlanService.regenerateWorkoutPlan(user);
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      log.info("Workout plan regenerated successfully for email: {}", email);
      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error regenerating workout plan for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Update profile and regenerate all plans
  @PutMapping("/user/update-and-regenerate")
  public ResponseEntity<OptimizedPlansResponseDTO> updateProfileAndRegeneratePlans(
      @RequestParam String email,
      @RequestBody UserProfileDTO userProfile) {
    log.info("Updating profile and regenerating plans for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      // Update user details
      user.setName(userProfile.getName());
      user.setAge(userProfile.getAge());
      user.setHeight(userProfile.getHeight());
      user.setWeight(userProfile.getWeight());
      user.setGender(userProfile.getGender());
      user.setGoal(userProfile.getGoal());
      user.setActivityLevel(userProfile.getActivityLevel());
      user.setPreference(userProfile.getPreference());

      user = userRepository.save(user);

      // Regenerate plans with updated profile
      var dietPlan = dietPlanService.regenerateDietPlan(user);
      var workoutPlan = workoutPlanService.regenerateWorkoutPlan(user);

      // Convert to DTOs
      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);
      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);

      // Get updated nutrition analysis
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      NutritionAnalysis nutritionAnalysis = new NutritionAnalysis();
      nutritionAnalysis.setUserId(user.getUserId());
      nutritionAnalysis.setDailyCalories(needs.calories);
      nutritionAnalysis.setDailyProtein(needs.protein);
      nutritionAnalysis.setDailyCarbs(needs.carbs);
      nutritionAnalysis.setDailyFat(needs.fat);
      nutritionAnalysis.setBmr(calculateBMR(user));
      nutritionAnalysis.setTdee(needs.calories);

      OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
      response.setUser(userMapper.toDTO(user));
      response.setDietPlan(dietPlanDTO);
      response.setWorkoutPlan(workoutPlanDTO);
      response.setNutritionAnalysis(nutritionAnalysis);
      response.setPlansExist(true);
      response.setSummary(generateSummary(userMapper.toDTO(user), dietPlanDTO, workoutPlanDTO));

      log.info("Successfully updated profile and regenerated plans for email: {}", email);
      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("Error updating profile and regenerating plans for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get nutrition analysis only
  @GetMapping("/user/nutrition-analysis")
  public ResponseEntity<NutritionAnalysis> getNutritionAnalysis(@RequestParam String email) {
    log.info("Getting nutrition analysis for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);

      NutritionAnalysis analysis = new NutritionAnalysis();
      analysis.setUserId(user.getUserId());
      analysis.setDailyCalories(needs.calories);
      analysis.setDailyProtein(needs.protein);
      analysis.setDailyCarbs(needs.carbs);
      analysis.setDailyFat(needs.fat);
      analysis.setBmr(calculateBMR(user));
      analysis.setTdee(needs.calories);

      return ResponseEntity.ok(analysis);

    } catch (Exception e) {
      log.error("Error getting nutrition analysis for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only diet plan
  @GetMapping("/user/diet-plan")
  public ResponseEntity<SimpleDietPlanDTO> getDietPlan(@RequestParam String email) {
    log.info("Fetching diet plan for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      var dietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
      if (dietPlan == null) {
        dietPlan = dietPlanService.generateCustomDietPlan(user);
      }

      SimpleDietPlanDTO dietPlanDTO = dietPlanMapper.toDTO(dietPlan);
      return ResponseEntity.ok(dietPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching diet plan for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get only workout plan
  @GetMapping("/user/workout-plan")
  public ResponseEntity<SimpleWorkoutPlanDTO> getWorkoutPlan(@RequestParam String email) {
    log.info("Fetching workout plan for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      var workoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
      if (workoutPlan == null) {
        workoutPlan = workoutPlanService.generateCustomWorkoutPlan(user);
      }

      SimpleWorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.toDTO(workoutPlan);
      return ResponseEntity.ok(workoutPlanDTO);

    } catch (Exception e) {
      log.error("Error fetching workout plan for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Delete user plans (for testing or user request)
  @DeleteMapping("/user/plans")
  public ResponseEntity<String> deleteUserPlans(@RequestParam String email) {
    log.info("Deleting plans for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      dietPlanService.deleteUserPlans(user.getUserId());
      workoutPlanService.deleteUserPlans(user.getUserId());

      log.info("Plans deleted successfully for email: {}", email);
      return ResponseEntity.ok("Plans deleted successfully");

    } catch (Exception e) {
      log.error("Error deleting plans for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error deleting plans");
    }
  }

  // Health check endpoint
  @GetMapping("/health")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("Fitness Plan Service is running");
  }

  // Get user profile only
  @GetMapping("/user/profile")
  public ResponseEntity<UserProfileDTO> getUserProfile(@RequestParam String email) {
    log.info("Fetching user profile for email: {}", email);

    try {
      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

      UserProfileDTO userDTO = userMapper.toDTO(user);
      return ResponseEntity.ok(userDTO);

    } catch (Exception e) {
      log.error("Error fetching user profile for email: {}", email, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // ===== LEGACY ENDPOINTS (for backward compatibility) =====

  // Legacy: Generate complete plan with User object
  @PostMapping("/generate-complete-plan")
  public ResponseEntity<CompleteFitnessPlan> generateCompletePlan(@RequestBody User user) {
    log.info("Legacy endpoint: Generating complete fitness plan for user: {}", user.getEmail());

    try {
      User savedUser = userRepository.save(user);

      var dietPlan = dietPlanService.generateCustomDietPlan(savedUser);
      var workoutPlan = workoutPlanService.generateCustomWorkoutPlan(savedUser);

      CompleteFitnessPlan completePlan = new CompleteFitnessPlan();
      completePlan.setUser(savedUser);
      completePlan.setDietPlan(dietPlan);
      completePlan.setWorkoutPlan(workoutPlan);
      completePlan.setGeneratedDate(java.time.LocalDate.now());
      completePlan.setSummary(String.format("Complete fitness plan for %s", savedUser.getName()));

      return ResponseEntity.ok(completePlan);

    } catch (Exception e) {
      log.error("Error in legacy generate complete plan for user: {}", user.getEmail(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // ===== HELPER METHODS =====

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
}