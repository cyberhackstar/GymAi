// OptimizedFitnessService.java
package com.gymai.plan_service.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.mapper.*;
import com.gymai.plan_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class OptimizedFitnessService {

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

  public UserProfileCheckDTO checkUserProfile(String email) {
    log.info("Checking user profile for email: {}", email);

    Optional<User> userOpt = userRepository.findByEmail(email);
    if (!userOpt.isPresent()) {
      return UserProfileCheckDTO.notFound();
    }

    User user = userOpt.get();
    UserProfileDTO userDTO = userMapper.toDTO(user);

    if (userDTO.isProfileComplete()) {
      return UserProfileCheckDTO.complete(userDTO);
    } else {
      return UserProfileCheckDTO.incomplete(userDTO);
    }
  }

  public OptimizedPlansResponseDTO getUserPlansOptimized(String email) {
    log.info("Fetching optimized plans for email: {}", email);

    Optional<User> userOpt = userRepository.findByEmail(email);
    if (!userOpt.isPresent()) {
      throw new RuntimeException("User not found with email: " + email);
    }

    User user = userOpt.get();
    UserProfileDTO userDTO = userMapper.toDTO(user);

    // Check if plans exist
    SimpleDietPlanDTO dietPlan = null;
    SimpleWorkoutPlanDTO workoutPlan = null;

    var existingDietPlan = dietPlanService.getExistingDietPlan(user.getUserId());
    if (existingDietPlan != null) {
      dietPlan = dietPlanMapper.toDTO(existingDietPlan);
    }

    var existingWorkoutPlan = workoutPlanService.getExistingWorkoutPlan(user.getUserId());
    if (existingWorkoutPlan != null) {
      workoutPlan = workoutPlanMapper.toDTO(existingWorkoutPlan);
    }

    // Get nutrition analysis
    NutritionAnalysis nutritionAnalysis = null;
    if (userDTO.isProfileComplete()) {
      var needs = nutritionCalculatorService.calculateNutritionalNeeds(user);
      nutritionAnalysis = new NutritionAnalysis();
      nutritionAnalysis.setUserId(user.getUserId());
      nutritionAnalysis.setDailyCalories(needs.calories);
      nutritionAnalysis.setDailyProtein(needs.protein);
      nutritionAnalysis.setDailyCarbs(needs.carbs);
      nutritionAnalysis.setDailyFat(needs.fat);
      nutritionAnalysis.setBmr(calculateBMR(user));
      nutritionAnalysis.setTdee(needs.calories);
    }

    OptimizedPlansResponseDTO response = new OptimizedPlansResponseDTO();
    response.setUser(userDTO);
    response.setDietPlan(dietPlan);
    response.setWorkoutPlan(workoutPlan);
    response.setNutritionAnalysis(nutritionAnalysis);
    response.setPlansExist(dietPlan != null && workoutPlan != null);
    response.setSummary(generateSummary(userDTO, dietPlan, workoutPlan));

    return response;
  }

  public OptimizedPlansResponseDTO generatePlansForUser(String email, UserProfileDTO userProfile) {
    log.info("Generating plans for email: {}", email);

    // Create or update user
    User user = userMapper.toEntity(userProfile);
    if (user.getUserId() == null) {
      Optional<User> existingUser = userRepository.findByEmail(email);
      if (existingUser.isPresent()) {
        user.setUserId(existingUser.get().getUserId());
      }
    }
    user.setEmail(email);
    user = userRepository.save(user);

    // Generate plans
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

    return response;
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

  private double calculateBMR(User user) {
    if ("MALE".equalsIgnoreCase(user.getGender())) {
      return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
    } else {
      return (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
    }
  }
}