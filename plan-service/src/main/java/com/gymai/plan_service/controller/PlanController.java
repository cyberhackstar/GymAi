// package com.gymai.plan_service.controller;

// import com.gymai.plan_service.dto.PlanResponse;
// import com.gymai.plan_service.dto.DietPlanResponse;
// import com.gymai.plan_service.dto.WorkoutPlanResponse;
// import com.gymai.plan_service.service.PlanService;
// import com.gymai.security.service.JwtService;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/plan")
// @RequiredArgsConstructor
// @Slf4j
// public class PlanController {

// private final PlanService planService;
// private final JwtService jwtService;

// // ------------------------ DIET PLAN ------------------------

// @PostMapping("/diet/generate")
// public ResponseEntity<DietPlanResponse> generateDietPlan(HttpServletRequest
// httpRequest) {
// String token = extractToken(httpRequest);
// Long userId = jwtService.extractUserId(token);

// log.info("📦 Generating DIET plan for userId: {}", userId);
// DietPlanResponse response = planService.generateDietPlanForUser(userId);

// log.info("✅ Diet plan generated successfully for userId: {}", userId);
// return ResponseEntity.ok(response);
// }

// @GetMapping("/diet")
// public ResponseEntity<DietPlanResponse> getDietPlan(HttpServletRequest
// httpRequest) {
// String token = extractToken(httpRequest);
// Long userId = jwtService.extractUserId(token);

// log.info("🔍 Fetching DIET plan for userId: {}", userId);
// DietPlanResponse response = planService.getDietPlanByUserId(userId);

// log.info("📦 Diet plan fetched successfully for userId: {}", userId);
// return ResponseEntity.ok(response);
// }

// // ------------------------ WORKOUT PLAN ------------------------

// @PostMapping("/workout/generate")
// public ResponseEntity<WorkoutPlanResponse>
// generateWorkoutPlan(HttpServletRequest httpRequest) {
// String token = extractToken(httpRequest);
// Long userId = jwtService.extractUserId(token);

// log.info("📦 Generating WORKOUT plan for userId: {}", userId);
// WorkoutPlanResponse response =
// planService.generateWorkoutPlanForUser(userId);

// log.info("✅ Workout plan generated successfully for userId: {}", userId);
// return ResponseEntity.ok(response);
// }

// @GetMapping("/workout")
// public ResponseEntity<WorkoutPlanResponse> getWorkoutPlan(HttpServletRequest
// httpRequest) {
// String token = extractToken(httpRequest);
// Long userId = jwtService.extractUserId(token);

// log.info("🔍 Fetching WORKOUT plan for userId: {}", userId);
// WorkoutPlanResponse response = planService.getWorkoutPlanByUserId(userId);

// log.info("📦 Workout plan fetched successfully for userId: {}", userId);
// return ResponseEntity.ok(response);
// }

// // ------------------------ FEEDBACK ------------------------

// @PutMapping("/feedback")
// public ResponseEntity<Void> updateFeedback(HttpServletRequest httpRequest,
// @RequestBody String feedback) {
// String token = extractToken(httpRequest);
// Long userId = jwtService.extractUserId(token);

// log.info("💬 Updating feedback for userId: {} -> {}", userId, feedback);
// planService.updateFeedback(userId, feedback);

// log.info("🟢 Feedback updated successfully for userId: {}", userId);
// return ResponseEntity.ok().build();
// }

// // ------------------------ DEBUG ------------------------

// @GetMapping("/profile")
// public ResponseEntity<?> getProfile(HttpServletRequest request) {
// String token = extractToken(request);
// Long userId = jwtService.extractUserId(token);
// String name = jwtService.extractName(token);

// return ResponseEntity.ok("Hello " + name + ", your userId is " + userId);
// }

// // ------------------------ TOKEN EXTRACTION ------------------------

// private String extractToken(HttpServletRequest request) {
// final String authHeader = request.getHeader("Authorization");
// if (authHeader != null && authHeader.startsWith("Bearer ")) {
// return authHeader.substring(7);
// }
// log.error("Authorization header is missing or malformed");
// throw new RuntimeException("JWT token is missing");
// }
// }
