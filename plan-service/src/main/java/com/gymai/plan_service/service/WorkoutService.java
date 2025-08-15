// package com.gymai.plan_service.service;

// import com.gymai.plan_service.dto.UserDto;
// import com.gymai.plan_service.feign.WgerClient;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.concurrent.CompletableFuture;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class WorkoutService {

// private final WgerClient wgerClient;

// @Async("planExecutor")
// public CompletableFuture<List<String>> getWorkouts(UserDto profile) {
// String goal =
// Optional.ofNullable(profile.getGoal()).orElse("").toLowerCase();
// String activity =
// Optional.ofNullable(profile.getActivityLevel()).orElse("moderate").toLowerCase();
// int desired = "high".equals(activity) ? 8 : 5;

// log.info("🏋️ Fetching workouts (goal={}, activity={}, limit={})", goal,
// activity, desired);

// try {
// Map<String, Object> resp = wgerClient.getExercises(2, 100);
// List<Map<String, Object>> results = (List<Map<String, Object>>)
// resp.getOrDefault("results",
// Collections.emptyList());

// List<String> names = results.stream()
// .map(m -> Objects.toString(m.get("name"), ""))
// .filter(n -> !n.isBlank())
// .distinct()
// .collect(Collectors.toList());

// List<String> prioritized = prioritize(names, goal, desired);

// if (prioritized.size() < desired) {
// List<String> fill = names.stream()
// .filter(n -> !prioritized.contains(n))
// .limit(desired - prioritized.size())
// .collect(Collectors.toList());
// prioritized.addAll(fill);
// }

// log.info("✅ Selected {} workouts", prioritized.size());
// return CompletableFuture.completedFuture(prioritized);

// } catch (Exception ex) {
// log.error("❌ WGER error", ex);
// return CompletableFuture.completedFuture(fallbackWorkouts());
// }
// }

// private List<String> prioritize(List<String> names, String goal, int limit) {
// if (goal.contains("gain") || goal.contains("muscle")) {
// return names.stream()
// .filter(n ->
// n.toLowerCase().matches(".*(press|squat|dead|row|bench|pull).*"))
// .limit(limit)
// .collect(Collectors.toList());
// } else if (goal.contains("lose") || goal.contains("weight")) {
// return names.stream()
// .filter(n ->
// n.toLowerCase().matches(".*(burpee|jump|sprint|mountain|jumping).*"))
// .limit(limit)
// .collect(Collectors.toList());
// } else {
// return names.stream().limit(limit).collect(Collectors.toList());
// }
// }

// private List<String> fallbackWorkouts() {
// return List.of("Pushups", "Squats", "Plank", "Lunges", "Jumping Jacks");
// }
// }
