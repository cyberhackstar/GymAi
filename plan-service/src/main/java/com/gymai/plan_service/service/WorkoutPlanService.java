package com.gymai.plan_service.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gymai.plan_service.entity.DayWorkoutPlan;
import com.gymai.plan_service.entity.Exercise;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.entity.WorkoutExercise;
import com.gymai.plan_service.entity.WorkoutPlan;
import com.gymai.plan_service.repository.ExerciseRepository;

// WorkoutPlanService.java
@Service
public class WorkoutPlanService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    public WorkoutPlan generateCustomWorkoutPlan(User user) {
        WorkoutPlan workoutPlan = new WorkoutPlan();
        workoutPlan.setUserId(user.getUserId());

        // Determine plan type based on user goal
        String planType = determinePlanType(user.getGoal());
        workoutPlan.setPlanType(planType);

        // Determine difficulty level based on activity level
        String difficulty = determineDifficulty(user.getActivityLevel());
        workoutPlan.setDifficultyLevel(difficulty);

        // Generate 7-day workout plan
        List<String> dayNames = Arrays.asList("Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday");

        for (int i = 0; i < 7; i++) {
            DayWorkoutPlan dayPlan = generateDayWorkout(i + 1, dayNames.get(i), user, planType, difficulty);
            workoutPlan.getWeeklyPlan().add(dayPlan);
        }

        return workoutPlan;
    }

    private String determinePlanType(String goal) {
        switch (goal.toUpperCase()) {
            case "WEIGHT_LOSS":
                return "WEIGHT_LOSS";
            case "MUSCLE_GAIN":
                return "MUSCLE_GAIN";
            case "WEIGHT_GAIN":
                return "STRENGTH";
            case "MAINTENANCE":
            default:
                return "MIXED";
        }
    }

    private String determineDifficulty(String activityLevel) {
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

        // Define workout schedule based on plan type
        String focusArea = determineFocusArea(dayNumber, planType);

        if ("REST".equals(focusArea)) {
            DayWorkoutPlan restDay = new DayWorkoutPlan(dayNumber, dayName, focusArea);
            restDay.setRestDay(true);
            return restDay;
        }

        DayWorkoutPlan dayPlan = new DayWorkoutPlan(dayNumber, dayName, focusArea);

        // Generate exercises based on focus area and plan type
        List<Exercise> exercises = getExercisesForFocusArea(focusArea, planType, difficulty);

        // Add exercises to the day plan
        for (Exercise exercise : exercises) {
            WorkoutExercise workoutExercise = createWorkoutExercise(exercise, planType, difficulty);
            dayPlan.addExercise(workoutExercise);
        }

        return dayPlan;
    }

    private String determineFocusArea(int dayNumber, String planType) {
        switch (planType.toUpperCase()) {
            case "WEIGHT_LOSS":
                // More cardio-focused plan
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
                    default:
                        return "CARDIO";
                }
            case "MUSCLE_GAIN":
            case "STRENGTH":
                // More strength-focused plan
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
                    default:
                        return "UPPER_BODY";
                }
            case "MIXED":
            default:
                // Balanced plan
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
                    default:
                        return "FULL_BODY";
                }
        }
    }

    private List<Exercise> getExercisesForFocusArea(String focusArea, String planType, String difficulty) {
        List<Exercise> exercises = new ArrayList<>();

        switch (focusArea.toUpperCase()) {
            case "CARDIO":
                exercises.addAll(exerciseRepository.findByCategoryAndDifficulty("CARDIO", difficulty));
                if (exercises.size() > 4) {
                    Collections.shuffle(exercises);
                    exercises = exercises.subList(0, 4);
                }
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

        // Shuffle and limit exercises
        Collections.shuffle(exercises);
        return exercises.stream().limit(6).collect(Collectors.toList());
    }

    private WorkoutExercise createWorkoutExercise(Exercise exercise, String planType, String difficulty) {
        int sets = 3;
        int reps = 12;
        int duration = 0;
        int rest = 60;

        if ("CARDIO".equals(exercise.getCategory())) {
            // Cardio exercises
            sets = 1;
            reps = 0;
            duration = getDurationForDifficulty(difficulty);
            rest = 60;
        } else {
            // Strength exercises
            sets = getSetsForDifficulty(difficulty);
            reps = getRepsForGoal(planType);
            duration = 0;
            rest = getRestForDifficulty(difficulty);
        }

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
                return 8; // Lower reps for strength
            case "WEIGHT_LOSS":
                return 15; // Higher reps for endurance
            case "MIXED":
            default:
                return 12; // Moderate reps
        }
    }

    private int getRestForDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "BEGINNER":
                return 90; // More rest for beginners
            case "INTERMEDIATE":
                return 60;
            case "ADVANCED":
                return 45; // Less rest for advanced
            default:
                return 60;
        }
    }
}