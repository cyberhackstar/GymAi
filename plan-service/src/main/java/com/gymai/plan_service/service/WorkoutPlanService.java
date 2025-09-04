package com.gymai.plan_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.*;
import com.gymai.plan_service.repository.*;
import com.gymai.plan_service.dto.SimpleWorkoutPlanDTO;
import com.gymai.plan_service.mapper.WorkoutPlanMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
public class WorkoutPlanService {

    private static final Logger log = LoggerFactory.getLogger(WorkoutPlanService.class);

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WorkoutPlanMapper workoutPlanMapper;

    @PersistenceContext
    private EntityManager entityManager;

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public WorkoutPlan generateCustomWorkoutPlan(User user) {
        log.info("Generating workout plan for userId={}, goal={}, activityLevel={}",
                user.getUserId(), user.getGoal(), user.getActivityLevel());

        // Validate user inputs
        validateUserInputs(user);

        // Check cache first for DTO, not entity
        SimpleWorkoutPlanDTO cachedPlan = cacheService.getCachedWorkoutPlan(user.getUserId());
        if (cachedPlan != null) {
            log.info("Found cached workout plan for userId={}, fetching full entity", user.getUserId());
            WorkoutPlan existingPlan = getExistingWorkoutPlanSafe(user.getUserId());
            if (existingPlan != null) {
                return existingPlan;
            }
        }

        // Check if user already has a workout plan using safe method
        WorkoutPlan existingPlan = getExistingWorkoutPlanSafe(user.getUserId());
        if (existingPlan != null) {
            log.info("Found existing workout plan for userId={}, caching DTO and returning plan", user.getUserId());
            SimpleWorkoutPlanDTO planDTO = workoutPlanMapper.toDTO(existingPlan);
            cacheService.cacheWorkoutPlan(user.getUserId(), planDTO);
            return existingPlan;
        }

        return generateNewWorkoutPlan(user);
    }

    @Transactional
    @CacheEvict(value = "workout-plans", key = "#user.userId")
    public WorkoutPlan regenerateWorkoutPlan(User user) {
        log.info("Regenerating workout plan for userId={}", user.getUserId());

        // Validate user inputs
        validateUserInputs(user);

        // Clear cache
        cacheService.invalidateUserPlansCache(user.getUserId());

        // Delete existing plan
        List<WorkoutPlan> plans = workoutPlanRepository.findByUserId(user.getUserId());

        for (WorkoutPlan plan : plans) {
            workoutPlanRepository.delete(plan); // Hibernate cascades delete
        }
        log.info("Deleted {} workout plans for userId={}", plans.size(), user.getUserId());
        entityManager.flush(); // Ensure deletion is committed

        // Generate new plan
        return generateNewWorkoutPlan(user);
    }

    @Transactional(readOnly = true)
    public WorkoutPlan getExistingWorkoutPlan(Long userId) {
        log.info("Fetching existing workout plan for userId={}", userId);

        // Check cache first for DTO, not entity
        SimpleWorkoutPlanDTO cachedPlan = cacheService.getCachedWorkoutPlan(userId);
        if (cachedPlan != null) {
            log.debug("Retrieved workout plan DTO from cache for userId={}, fetching full entity", userId);
        }

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
    @CacheEvict(value = "workout-plans", key = "#userId")
    public void deleteUserWorkoutPlans(Long userId) {
        log.info("Deleting workout plans for userId: {}", userId);

        cacheService.invalidateUserPlansCache(userId);

        List<WorkoutPlan> plans = workoutPlanRepository.findByUserId(userId);

        for (WorkoutPlan plan : plans) {
            workoutPlanRepository.delete(plan); // Hibernate cascades delete
        }
        log.info("Deleted {} workout plans for userId={}", plans.size(), userId);
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

        // Validate that we have exercises available
        validateExerciseAvailability(planType, difficulty);

        // Generate 7-day plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        List<DayWorkoutPlan> weeklyPlan = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            DayWorkoutPlan dayPlan = generateDayWorkout(i + 1, dayNames.get(i), user, planType, difficulty);
            dayPlan.setWorkoutPlan(workoutPlan);
            weeklyPlan.add(dayPlan);
            log.debug("Generated workout for {} (Day {}): focusArea={}, restDay={}, exerciseCount={}",
                    dayNames.get(i), i + 1, dayPlan.getFocusArea(), dayPlan.isRestDay(),
                    dayPlan.getExercises() != null ? dayPlan.getExercises().size() : 0);
        }

        workoutPlan.setWeeklyPlan(weeklyPlan);

        // Save the complete plan with cascaded entities
        workoutPlan = workoutPlanRepository.save(workoutPlan);

        // Cache the DTO result (not the entity)
        SimpleWorkoutPlanDTO planDTO = workoutPlanMapper.toDTO(workoutPlan);
        cacheService.cacheWorkoutPlan(user.getUserId(), planDTO);

        log.info("Completed workout plan generation and cached for userId={} with planId={}",
                user.getUserId(), workoutPlan.getId());
        return workoutPlan;
    }

    private void validateUserInputs(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (user.getGoal() == null || user.getGoal().trim().isEmpty()) {
            log.warn("Goal is null/empty for userId={}. Setting default to MAINTENANCE", user.getUserId());
            user.setGoal("MAINTENANCE");
        }
        if (user.getActivityLevel() == null || user.getActivityLevel().trim().isEmpty()) {
            log.warn("Activity level is null/empty for userId={}. Setting default to LIGHTLY_ACTIVE", user.getUserId());
            user.setActivityLevel("LIGHTLY_ACTIVE");
        }
    }

    private void validateExerciseAvailability(String planType, String difficulty) {
        // Check if we have exercises for the required focus areas
        List<String> requiredFocusAreas = getRequiredFocusAreasForPlanType(planType);
        for (String focusArea : requiredFocusAreas) {
            List<Exercise> exercises = getExercisesForFocusAreaValidation(focusArea, difficulty);
            if (exercises.isEmpty()) {
                log.warn("No exercises found for focusArea={}, difficulty={}. Plan quality may be reduced.",
                        focusArea, difficulty);
            }
        }
    }

    private List<String> getRequiredFocusAreasForPlanType(String planType) {
        switch (planType.toUpperCase()) {
            case "WEIGHT_LOSS":
                return Arrays.asList("CARDIO", "UPPER_BODY", "LOWER_BODY", "FULL_BODY");
            case "MUSCLE_GAIN":
            case "STRENGTH":
                return Arrays.asList("UPPER_BODY", "LOWER_BODY", "FULL_BODY");
            case "MIXED":
            default:
                return Arrays.asList("UPPER_BODY", "LOWER_BODY", "FULL_BODY", "CARDIO");
        }
    }

    private List<Exercise> getExercisesForFocusAreaValidation(String focusArea, String difficulty) {
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

        return exercises.stream()
                .filter(exercise -> exercise != null &&
                        exercise.getName() != null && !exercise.getName().trim().isEmpty())
                .collect(Collectors.toList());
    }

    private String determinePlanType(String goal) {
        if (goal == null || goal.trim().isEmpty()) {
            return "MIXED";
        }

        switch (goal.toUpperCase().trim()) {
            case "WEIGHT_LOSS":
            case "LOSE_WEIGHT":
            case "FAT_LOSS":
                return "WEIGHT_LOSS";
            case "MUSCLE_GAIN":
            case "BUILD_MUSCLE":
            case "BULK":
                return "MUSCLE_GAIN";
            case "WEIGHT_GAIN":
            case "GAIN_WEIGHT":
            case "STRENGTH":
            case "GET_STRONGER":
                return "STRENGTH";
            case "MAINTENANCE":
            case "MAINTAIN":
            case "GENERAL_FITNESS":
            case "MIXED":
            default:
                return "MIXED";
        }
    }

    private String determineDifficulty(String activityLevel) {
        if (activityLevel == null || activityLevel.trim().isEmpty()) {
            return "BEGINNER";
        }

        switch (activityLevel.toUpperCase().trim()) {
            case "SEDENTARY":
            case "INACTIVE":
            case "LIGHTLY_ACTIVE":
            case "LIGHT":
                return "BEGINNER";
            case "MODERATELY_ACTIVE":
            case "MODERATE":
                return "INTERMEDIATE";
            case "VERY_ACTIVE":
            case "ACTIVE":
            case "EXTREMELY_ACTIVE":
            case "VERY_ACTIVE_ATHLETE":
            case "ADVANCED":
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

        // Get exercises for focus area (with caching)
        List<Exercise> exercises = getExercisesForFocusArea(focusArea, planType, difficulty);
        log.debug("Fetched {} exercises for focusArea={} (difficulty={})",
                exercises.size(), focusArea, difficulty);

        if (exercises.isEmpty()) {
            log.warn("No exercises available for focusArea={}, difficulty={}. Creating empty day plan.",
                    focusArea, difficulty);
            return dayPlan;
        }

        for (Exercise exercise : exercises) {
            WorkoutExercise workoutExercise = createWorkoutExercise(exercise, planType, difficulty);
            dayPlan.addExercise(workoutExercise);
            log.trace("Added exercise={} to Day {} plan with sets={}, reps={}, duration={}, rest={}",
                    exercise.getName(), dayNumber, workoutExercise.getSets(),
                    workoutExercise.getReps(), workoutExercise.getDurationMinutes(), workoutExercise.getRestSeconds());
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

    @SuppressWarnings("unchecked")
    private List<Exercise> getExercisesForFocusArea(String focusArea, String planType, String difficulty) {
        // Check cache first
        Object cachedExercises = cacheService.getCachedExercisesByFocus(focusArea, difficulty);
        if (cachedExercises instanceof List) {
            log.debug("Retrieved exercises from cache for focusArea={}, difficulty={}", focusArea, difficulty);
            List<Exercise> exercises = (List<Exercise>) cachedExercises;
            // Shuffle and limit to avoid repetitive plans
            Collections.shuffle(exercises);
            return exercises.stream()
                    .filter(ex -> ex != null && ex.getName() != null && !ex.getName().trim().isEmpty())
                    .limit(6)
                    .collect(Collectors.toList());
        }

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

        // Filter out invalid exercises
        exercises = exercises.stream()
                .filter(exercise -> exercise != null &&
                        exercise.getName() != null && !exercise.getName().trim().isEmpty())
                .collect(Collectors.toList());

        // If no exercises found for specific difficulty, try with beginner difficulty
        // as fallback
        if (exercises.isEmpty() && !"BEGINNER".equals(difficulty)) {
            log.warn("No exercises found for focusArea={}, difficulty={}. Trying with BEGINNER difficulty as fallback.",
                    focusArea, difficulty);
            return getExercisesForFocusArea(focusArea, planType, "BEGINNER");
        }

        // Cache the result before shuffling
        cacheService.cacheExercisesByFocus(focusArea, difficulty, new ArrayList<>(exercises));

        // Shuffle and limit to avoid repetitive plans
        Collections.shuffle(exercises);
        return exercises.stream().limit(6).collect(Collectors.toList());
    }

    private WorkoutExercise createWorkoutExercise(Exercise exercise, String planType, String difficulty) {
        int sets, reps, duration, rest;
        double weight = 0.0;

        if ("CARDIO".equalsIgnoreCase(exercise.getCategory())) {
            sets = 1;
            reps = 0;
            duration = getDurationForDifficulty(difficulty);
            rest = 60;
            weight = 0.0;
        } else {
            sets = getSetsForDifficulty(difficulty);
            reps = getRepsForGoal(planType);
            duration = 0;
            rest = getRestForDifficulty(difficulty);
            weight = getDefaultWeightForExercise(exercise, difficulty);
        }

        log.trace("Created workoutExercise: exercise={}, sets={}, reps={}, duration={}, weight={}, rest={}",
                exercise.getName(), sets, reps, duration, weight, rest);

        return new WorkoutExercise(exercise, sets, reps, duration, roundTo1Decimal(weight), rest);
    }

    private double getDefaultWeightForExercise(Exercise exercise, String difficulty) {
        // This is a simple weight estimation - you might want to make this more
        // sophisticated
        double baseWeight;

        String muscleGroup = exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().toUpperCase() : "UNKNOWN";

        switch (muscleGroup) {
            case "CHEST":
            case "BACK":
                baseWeight = 20.0;
                break;
            case "LEGS":
                baseWeight = 30.0;
                break;
            case "SHOULDERS":
            case "ARMS":
                baseWeight = 15.0;
                break;
            case "CORE":
            case "FULL_BODY":
                baseWeight = 0.0; // Bodyweight exercises
                break;
            default:
                baseWeight = 10.0;
        }

        // Adjust based on difficulty
        switch (difficulty.toUpperCase()) {
            case "BEGINNER":
                return roundTo1Decimal(baseWeight * 0.7);
            case "INTERMEDIATE":
                return roundTo1Decimal(baseWeight);
            case "ADVANCED":
                return roundTo1Decimal(baseWeight * 1.5);
            default:
                return roundTo1Decimal(baseWeight);
        }
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