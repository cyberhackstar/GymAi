package com.gymai.plan_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.*;
import com.gymai.plan_service.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutPlanService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutPlanService.class);

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public WorkoutPlan generateCustomWorkoutPlan(User user) {
        log.info("Generating workout plan for userId={}, goal={}, activityLevel={}",
                user.getUserId(), user.getGoal(), user.getActivityLevel());

        // Check if user already has a workout plan using safe method
        WorkoutPlan existingPlan = getExistingWorkoutPlanSafe(user.getUserId());
        if (existingPlan != null) {
            log.info("Found existing workout plan for userId={}, returning cached plan", user.getUserId());
            return existingPlan;
        }

        return generateNewWorkoutPlan(user);
    }

    @Transactional
    public WorkoutPlan regenerateWorkoutPlan(User user) {
        log.info("Regenerating workout plan for userId={}", user.getUserId());

        // Delete existing plan
        workoutPlanRepository.deleteByUserId(user.getUserId());
        entityManager.flush(); // Ensure deletion is committed

        // Generate new plan
        return generateNewWorkoutPlan(user);
    }

    @Transactional(readOnly = true)
    public WorkoutPlan getExistingWorkoutPlan(Long userId) {
        log.info("Fetching existing workout plan for userId={}", userId);
        return getExistingWorkoutPlanSafe(userId);
    }

    @Transactional(readOnly = true)
    private WorkoutPlan getExistingWorkoutPlanSafe(Long userId) {
        // Step 1: Get basic workout plan
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserIdOrderByCreatedDateDesc(userId);
        if (plans.isEmpty()) {
            return null;
        }

        WorkoutPlan workoutPlan = plans.get(0);

        // Step 2: Manually load day workout plans
        List<DayWorkoutPlan> dayWorkoutPlans = entityManager.createQuery(
                "SELECT dwp FROM DayWorkoutPlan dwp WHERE dwp.workoutPlan.id = :planId ORDER BY dwp.dayNumber",
                DayWorkoutPlan.class)
                .setParameter("planId", workoutPlan.getId())
                .getResultList();

        // Step 3: Load workout exercises for each day plan
        for (DayWorkoutPlan dayPlan : dayWorkoutPlans) {
            List<WorkoutExercise> exercises = entityManager.createQuery(
                    "SELECT we FROM WorkoutExercise we JOIN FETCH we.exercise WHERE we.dayWorkoutPlan.id = :dayPlanId",
                    WorkoutExercise.class)
                    .setParameter("dayPlanId", dayPlan.getId())
                    .getResultList();

            dayPlan.getExercises().clear();
            dayPlan.getExercises().addAll(exercises);

            // Set back references
            for (WorkoutExercise exercise : exercises) {
                exercise.setDayWorkoutPlan(dayPlan);
            }
        }

        workoutPlan.getWeeklyPlan().clear();
        workoutPlan.getWeeklyPlan().addAll(dayWorkoutPlans);

        // Set back references
        for (DayWorkoutPlan dayPlan : dayWorkoutPlans) {
            dayPlan.setWorkoutPlan(workoutPlan);
        }

        log.debug("Successfully loaded workout plan with {} daily plans", dayWorkoutPlans.size());
        return workoutPlan;
    }

    @Transactional
    public void deleteUserPlans(Long userId) {
        log.info("Deleting workout plans for userId: {}", userId);
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserId(userId);
        if (!plans.isEmpty()) {
            workoutPlanRepository.deleteAll(plans);
            log.info("Deleted {} workout plans for userId: {}", plans.size(), userId);
        }
    }

    @Transactional
    private WorkoutPlan generateNewWorkoutPlan(User user) {
        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setUserId(user.getUserId());

        // Determine plan type
        String planType = determinePlanType(user.getGoal());
        workoutPlan.setPlanType(planType);
        log.debug("Determined planType={} for userId={}", planType, user.getUserId());

        // Determine difficulty
        String difficulty = determineDifficulty(user.getActivityLevel());
        workoutPlan.setDifficultyLevel(difficulty);
        log.debug("Determined difficulty={} for userId={}", difficulty, user.getUserId());

        // Save workout plan first to get ID
        workoutPlan = workoutPlanRepository.save(workoutPlan);

        // Generate 7-day plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        List<DayWorkoutPlan> weeklyPlan = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DayWorkoutPlan dayPlan = generateDayWorkout(i + 1, dayNames.get(i), user, planType, difficulty);
            dayPlan.setWorkoutPlan(workoutPlan);
            weeklyPlan.add(dayPlan);
            log.debug("Generated workout for {} (Day {}): focusArea={}, restDay={}",
                    dayNames.get(i), i + 1, dayPlan.getFocusArea(), dayPlan.isRestDay());
        }

        workoutPlan.setWeeklyPlan(weeklyPlan);

        // Save the complete plan with cascaded entities
        workoutPlan = workoutPlanRepository.save(workoutPlan);

        log.info("Completed workout plan generation for userId={} with planId={}", user.getUserId(),
                workoutPlan.getId());
        return workoutPlan;
    }

    private String determinePlanType(String goal) {
        if (goal == null)
            return "MIXED";

        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return "WEIGHT_LOSS";
            case "MUSCLE_GAIN":
                return "MUSCLE_GAIN";
            case "WEIGHT_GAIN":
            case "STRENGTH":
                return "STRENGTH";
            case "MAINTENANCE":
            default:
                return "MIXED";
        }
    }

    private String determineDifficulty(String activityLevel) {
        if (activityLevel == null)
            return "BEGINNER";

        switch (activityLevel.toUpperCase()) {
            case "SEDENTARY":
            case "LIGHTLY_ACTIVE":
                return "BEGINNER";
            case "MODERATELY_ACTIVE":
                return "INTERMEDIATE";
            case "VERY_ACTIVE":
            case "EXTREMELY_ACTIVE":
                return "ADVANCED";
            default:
                return "BEGINNER";
        }
    }

    private DayWorkoutPlan generateDayWorkout(int dayNumber, String dayName, User user,
            String planType, String difficulty) {
        String focusArea = determineFocusArea(dayNumber, planType);

        if ("REST".equals(focusArea)) {
            log.debug("Day {} ({}) is marked as REST day for userId={}", dayNumber, dayName, user.getUserId());
            DayWorkoutPlan restDay = new DayWorkoutPlan(dayNumber, dayName, focusArea);
            restDay.setRestDay(true);
            return restDay;
        }

        log.debug("Generating workout for Day {} ({}), focusArea={}, planType={}, difficulty={}",
                dayNumber, dayName, focusArea, planType, difficulty);

        DayWorkoutPlan dayPlan = new DayWorkoutPlan(dayNumber, dayName, focusArea);

        // Get exercises for focus area
        List<Exercise> exercises = getExercisesForFocusArea(focusArea, planType, difficulty);
        log.debug("Fetched {} exercises for focusArea={} (difficulty={})",
                exercises.size(), focusArea, difficulty);

        for (Exercise exercise : exercises) {
            WorkoutExercise workoutExercise = createWorkoutExercise(exercise, planType, difficulty);
            dayPlan.addExercise(workoutExercise);
            log.trace("Added exercise={} to Day {} plan", exercise.getName(), dayNumber);
        }

        return dayPlan;
    }

    private String determineFocusArea(int dayNumber, String planType) {
        switch (planType.toUpperCase()) {
            case "WEIGHT_LOSS":
                switch (dayNumber) {
                    case 1:
                        return "CARDIO";
                    case 2:
                        return "UPPER_BODY";
                    case 3:
                        return "CARDIO";
                    case 4:
                        return "LOWER_BODY";
                    case 5:
                        return "FULL_BODY";
                    case 6:
                        return "CARDIO";
                    case 7:
                        return "REST";
                }
                break;
            case "MUSCLE_GAIN":
            case "STRENGTH":
                switch (dayNumber) {
                    case 1:
                        return "UPPER_BODY";
                    case 2:
                        return "LOWER_BODY";
                    case 3:
                        return "REST";
                    case 4:
                        return "UPPER_BODY";
                    case 5:
                        return "LOWER_BODY";
                    case 6:
                        return "FULL_BODY";
                    case 7:
                        return "REST";
                }
                break;
            case "MIXED":
            default:
                switch (dayNumber) {
                    case 1:
                        return "UPPER_BODY";
                    case 2:
                        return "CARDIO";
                    case 3:
                        return "LOWER_BODY";
                    case 4:
                        return "REST";
                    case 5:
                        return "FULL_BODY";
                    case 6:
                        return "CARDIO";
                    case 7:
                        return "REST";
                }
        }
        return "FULL_BODY"; // fallback
    }

    private List<Exercise> getExercisesForFocusArea(String focusArea, String planType, String difficulty) {
        List<Exercise> exercises = new ArrayList<>();

        switch (focusArea.toUpperCase()) {
            case "CARDIO":
                exercises.addAll(exerciseRepository.findByCategoryAndDifficulty("CARDIO", difficulty));
                break;
            case "UPPER_BODY":
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("CHEST", difficulty));
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("BACK", difficulty));
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("ARMS", difficulty));
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("SHOULDERS", difficulty));
                break;
            case "LOWER_BODY":
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("LEGS", difficulty));
                break;
            case "FULL_BODY":
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("FULL_BODY", difficulty));
                exercises.addAll(exerciseRepository.findByMuscleGroupAndDifficulty("CORE", difficulty));
                break;
        }

        // Shuffle and limit to avoid repetitive plans
        Collections.shuffle(exercises);
        return exercises.stream().limit(6).collect(Collectors.toList());
    }

    private WorkoutExercise createWorkoutExercise(Exercise exercise, String planType, String difficulty) {
        int sets, reps, duration, rest;

        if ("CARDIO".equalsIgnoreCase(exercise.getCategory())) {
            sets = 1;
            reps = 0;
            duration = getDurationForDifficulty(difficulty);
            rest = 60;
        } else {
            sets = getSetsForDifficulty(difficulty);
            reps = getRepsForGoal(planType);
            duration = 0;
            rest = getRestForDifficulty(difficulty);
        }

        log.trace("Created workoutExercise: exercise={}, sets={}, reps={}, duration={}, rest={}",
                exercise.getName(), sets, reps, duration, rest);

        return new WorkoutExercise(exercise, sets, reps, duration, 0, rest);
    }

    private int getDurationForDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "BEGINNER":
                return 15;
            case "INTERMEDIATE":
                return 25;
            case "ADVANCED":
                return 35;
            default:
                return 20;
        }
    }

    private int getSetsForDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "BEGINNER":
                return 2;
            case "INTERMEDIATE":
                return 3;
            case "ADVANCED":
                return 4;
            default:
                return 3;
        }
    }

    private int getRepsForGoal(String planType) {
        switch (planType.toUpperCase()) {
            case "MUSCLE_GAIN":
            case "STRENGTH":
                return 8;
            case "WEIGHT_LOSS":
                return 15;
            case "MIXED":
            default:
                return 12;
        }
    }

    private int getRestForDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "BEGINNER":
                return 90;
            case "INTERMEDIATE":
                return 60;
            case "ADVANCED":
                return 45;
            default:
                return 60;
        }
    }
}