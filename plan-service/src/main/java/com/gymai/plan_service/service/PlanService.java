// package com.gymai.plan_service.service;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;

// import com.gymai.plan_service.dto.PlanResponse;
// import com.gymai.plan_service.entity.Plan;
// import com.gymai.plan_service.feign.UserServiceClient;
// import com.gymai.plan_service.feign.dto.DietDTO;
// import com.gymai.plan_service.feign.dto.UserProfileDTO;
// import com.gymai.plan_service.feign.dto.WorkoutDTO;
// import com.gymai.plan_service.repository.PlanRepository;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class PlanService {

// private final PlanRepository planRepository;
// private final UserServiceClient userClient;
// private final DietService dietApiService;
// private final WorkoutService workoutApiService;

// /**
// * Generate a complete plan (diet + workout) for a user
// */
// @Transactional
// public PlanResponse generatePlanForUser(Long userId) {
// log.info("🔄 Generating full plan for userId: {}", userId);

// // 1️⃣ Get user profile from user-service
// UserProfileDTO profile = userClient.getUserProfile(userId);
// log.debug("User profile fetched: {}", profile);

// // 2️⃣ Get diet from Spoonacular
// DietDTO dietPlan = dietApiService.generateDiet(profile);

// // 3️⃣ Get workout from WGER
// WorkoutDTO workoutPlan = workoutApiService.generateWorkout(profile);

// // 4️⃣ Save to DB
// Plan plan = new Plan();
// plan.setUserId(userId);
// plan.setDietPlan(dietPlan);
// plan.setWorkoutPlan(workoutPlan);
// planRepository.save(plan);

// log.info("✅ Plan saved successfully for userId {}", userId);

// return new PlanResponse(dietPlan, workoutPlan);
// }

// /**
// * Get complete plan for a user
// */
// public PlanResponse getPlanByUserId(Long userId) {
// log.info("Fetching full plan for userId: {}", userId);
// Plan plan = planRepository.findByUserId(userId)
// .orElseThrow(() -> new RuntimeException("Plan not found for userId: " +
// userId));
// return new PlanResponse(plan.getDietPlan(), plan.getWorkoutPlan());
// }

// /**
// * Get only diet plan for a user
// */
// public DietDTO getDietByUserId(Long userId) {
// log.info("Fetching diet for userId: {}", userId);
// Plan plan = planRepository.findByUserId(userId)
// .orElseThrow(() -> new RuntimeException("Diet not found for userId: " +
// userId));
// return plan.getDietPlan();
// }

// /**
// * Get only workout plan for a user
// */
// public WorkoutDTO getWorkoutByUserId(Long userId) {
// log.info("Fetching workout for userId: {}", userId);
// Plan plan = planRepository.findByUserId(userId)
// .orElseThrow(() -> new RuntimeException("Workout not found for userId: " +
// userId));
// return plan.getWorkoutPlan();
// }

// /**
// * Update feedback
// */
// @Transactional
// public void updateFeedback(Long userId, String feedback) {
// log.info("Updating feedback for userId: {}", userId);
// Plan plan = planRepository.findByUserId(userId)
// .orElseThrow(() -> new RuntimeException("Plan not found for userId: " +
// userId));
// plan.setFeedback(feedback);
// planRepository.save(plan);
// log.debug("Feedback updated for userId: {}", userId);
// }
// }
