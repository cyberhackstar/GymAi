package com.gymai.plan_service.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.gymai.plan_service.entity.Exercise;
import com.gymai.plan_service.entity.Food;
import com.gymai.plan_service.repository.ExerciseRepository;
import com.gymai.plan_service.repository.FoodRepository;

import jakarta.annotation.PostConstruct;

// Data Initialization Service
@Service
@Component
public class DataInitializationService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @PostConstruct
    public void initializeData() {
        if (foodRepository.count() == 0) {
            initializeFoodData();
        }

        if (exerciseRepository.count() == 0) {
            initializeExerciseData();
        }
    }

    private void initializeFoodData() {
        List<Food> foods = Arrays.asList(
                // Vegetarian Foods
                // Breakfast
                new Food("Oats", 389, 16.9, 66.3, 6.9, 10.6, "VEG", "BREAKFAST", "GRAINS"),
                new Food("Whole Wheat Bread", 247, 12.6, 41.0, 3.4, 7.4, "VEG", "BREAKFAST", "GRAINS"),
                new Food("Banana", 89, 1.1, 22.8, 0.3, 2.6, "VEG", "BREAKFAST", "FRUITS"),
                new Food("Greek Yogurt", 59, 10.3, 3.6, 0.4, 0, "VEG", "BREAKFAST", "DAIRY"),
                new Food("Almonds", 579, 21.2, 21.6, 49.9, 12.5, "VEG", "BREAKFAST", "NUTS"),
                new Food("Eggs", 155, 13.0, 1.1, 11.0, 0, "VEG", "BREAKFAST", "PROTEIN"),

                // Lunch/Dinner
                new Food("Brown Rice", 111, 2.6, 23.0, 0.9, 1.8, "VEG", "LUNCH", "GRAINS"),
                new Food("Quinoa", 368, 14.1, 64.2, 6.1, 7.0, "VEG", "LUNCH", "GRAINS"),
                new Food("Lentils", 116, 9.0, 20.1, 0.4, 7.9, "VEG", "LUNCH", "PROTEIN"),
                new Food("Chickpeas", 164, 8.9, 27.4, 2.6, 7.6, "VEG", "LUNCH", "PROTEIN"),
                new Food("Paneer", 265, 18.3, 1.2, 20.8, 0, "VEG", "LUNCH", "PROTEIN"),
                new Food("Spinach", 23, 2.9, 3.6, 0.4, 2.2, "VEG", "LUNCH", "VEGETABLES"),
                new Food("Broccoli", 34, 2.8, 6.6, 0.4, 2.6, "VEG", "LUNCH", "VEGETABLES"),
                new Food("Sweet Potato", 86, 1.6, 20.1, 0.1, 3.0, "VEG", "LUNCH", "VEGETABLES"),
                new Food("Avocado", 160, 2.0, 8.5, 14.7, 6.7, "VEG", "LUNCH", "FRUITS"),

                // Non-Vegetarian Foods
                new Food("Chicken Breast", 165, 31.0, 0, 3.6, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                new Food("Salmon", 208, 22.0, 0, 12.4, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                new Food("Tuna", 144, 23.3, 0, 4.9, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                new Food("Lean Beef", 250, 26.0, 0, 15.0, 0, "NON_VEG", "LUNCH", "PROTEIN"),
                new Food("Fish", 206, 22.0, 0, 12.0, 0, "NON_VEG", "LUNCH", "PROTEIN"),

                // Snacks
                new Food("Apple", 52, 0.3, 13.8, 0.2, 2.4, "VEG", "SNACK", "FRUITS"),
                new Food("Walnuts", 654, 15.2, 13.7, 65.2, 6.7, "VEG", "SNACK", "NUTS"),
                new Food("Carrot", 41, 0.9, 9.6, 0.2, 2.8, "VEG", "SNACK", "VEGETABLES"),
                new Food("Greek Yogurt Plain", 59, 10.3, 3.6, 0.4, 0, "VEG", "SNACK", "DAIRY"),
                new Food("Cottage Cheese", 98, 11.1, 3.4, 4.3, 0, "VEG", "SNACK", "DAIRY"),

                // Vegan Options
                new Food("Tofu", 76, 8.1, 1.9, 4.8, 0.3, "VEGAN", "LUNCH", "PROTEIN"),
                new Food("Tempeh", 193, 19.0, 9.4, 11.0, 9.0, "VEGAN", "LUNCH", "PROTEIN"),
                new Food("Chia Seeds", 486, 17.0, 42.0, 31.0, 34.4, "VEGAN", "BREAKFAST", "NUTS"),
                new Food("Almond Milk", 17, 0.6, 1.5, 1.2, 0.2, "VEGAN", "BREAKFAST", "DAIRY"),
                new Food("Coconut Oil", 862, 0, 0, 100.0, 0, "VEGAN", "LUNCH", "NUTS"),
                new Food("Black Beans", 132, 8.9, 23.7, 0.5, 8.7, "VEGAN", "LUNCH", "PROTEIN"));

        foodRepository.saveAll(foods);
    }

    private void initializeExerciseData() {
        List<Exercise> exercises = Arrays.asList(
                // Cardio Exercises
                new Exercise("Running", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 10.0,
                        "Basic running exercise", "Start slow, maintain steady pace, focus on breathing"),
                new Exercise("Walking", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 4.0,
                        "Brisk walking", "Maintain brisk pace, swing arms naturally"),
                new Exercise("Cycling", "CARDIO", "LEGS", "MACHINE", "BEGINNER", 8.0,
                        "Stationary or outdoor cycling", "Maintain steady cadence, adjust resistance as needed"),
                new Exercise("Jump Rope", "CARDIO", "FULL_BODY", "RESISTANCE_BAND", "INTERMEDIATE", 12.0,
                        "Skipping rope exercise", "Start with basic bounce, keep jumps low"),
                new Exercise("Swimming", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 11.0,
                        "Full body swimming", "Focus on form and breathing technique"),

                // Strength Training - Chest
                new Exercise("Push-ups", "STRENGTH", "CHEST", "NONE", "BEGINNER", 5.0,
                        "Classic push-up exercise", "Keep body straight, lower chest to ground, push up"),
                new Exercise("Bench Press", "STRENGTH", "CHEST", "BARBELL", "INTERMEDIATE", 6.0,
                        "Barbell bench press", "Control the weight, full range of motion"),
                new Exercise("Dumbbell Chest Press", "STRENGTH", "CHEST", "DUMBBELL", "BEGINNER", 5.5,
                        "Dumbbell chest press", "Press dumbbells up and together"),
                new Exercise("Chest Dips", "STRENGTH", "CHEST", "NONE", "INTERMEDIATE", 6.0,
                        "Parallel bar dips", "Lower body until chest stretch, press up"),

                // Strength Training - Back
                new Exercise("Pull-ups", "STRENGTH", "BACK", "NONE", "INTERMEDIATE", 6.0,
                        "Classic pull-up", "Hang from bar, pull body up until chin over bar"),
                new Exercise("Bent-over Row", "STRENGTH", "BACK", "BARBELL", "INTERMEDIATE", 5.5,
                        "Barbell bent-over row", "Bend at hips, row bar to lower chest"),
                new Exercise("Lat Pulldown", "STRENGTH", "BACK", "MACHINE", "BEGINNER", 5.0,
                        "Lat pulldown machine", "Pull bar down to upper chest, squeeze shoulder blades"),
                new Exercise("Deadlift", "STRENGTH", "BACK", "BARBELL", "ADVANCED", 7.0,
                        "Conventional deadlift", "Hip hinge movement, keep bar close to body"),

                // Strength Training - Legs
                new Exercise("Squats", "STRENGTH", "LEGS", "NONE", "BEGINNER", 5.0,
                        "Bodyweight squats", "Feet shoulder-width apart, lower hips back and down"),
                new Exercise("Lunges", "STRENGTH", "LEGS", "NONE", "BEGINNER", 4.5,
                        "Forward lunges", "Step forward, lower back knee toward ground"),
                new Exercise("Leg Press", "STRENGTH", "LEGS", "MACHINE", "BEGINNER", 5.5,
                        "Leg press machine", "Press weight with legs, full range of motion"),
                new Exercise("Calf Raises", "STRENGTH", "LEGS", "NONE", "BEGINNER", 3.0,
                        "Standing calf raises", "Rise up on toes, lower slowly"),

                // Strength Training - Arms
                new Exercise("Bicep Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
                        "Dumbbell bicep curls", "Curl weight up, squeeze bicep at top"),
                new Exercise("Tricep Dips", "STRENGTH", "ARMS", "NONE", "BEGINNER", 4.0,
                        "Chair or bench dips", "Lower body by bending arms, press back up"),
                new Exercise("Hammer Curls", "STRENGTH", "ARMS", "DUMBBELL", "BEGINNER", 3.5,
                        "Neutral grip curls", "Keep palms facing each other throughout movement"),
                new Exercise("Overhead Press", "STRENGTH", "SHOULDERS", "DUMBBELL", "INTERMEDIATE", 5.0,
                        "Standing shoulder press", "Press weights overhead, control the descent"),

                // Core Exercises
                new Exercise("Plank", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.0,
                        "Forearm plank hold", "Hold straight line from head to heels"),
                new Exercise("Crunches", "STRENGTH", "CORE", "NONE", "BEGINNER", 3.5,
                        "Basic abdominal crunches", "Lift shoulders off ground, squeeze abs"),
                new Exercise("Mountain Climbers", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 8.0,
                        "Dynamic mountain climbers", "Alternate bringing knees to chest rapidly"),
                new Exercise("Russian Twists", "STRENGTH", "CORE", "NONE", "INTERMEDIATE", 4.0,
                        "Seated twisting exercise", "Rotate torso side to side, keep core engaged"),

                // Flexibility/Yoga
                new Exercise("Yoga Flow", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 3.0,
                        "Basic yoga sequence", "Flow through poses with controlled breathing"),
                new Exercise("Static Stretching", "FLEXIBILITY", "FULL_BODY", "NONE", "BEGINNER", 2.0,
                        "Hold stretches for flexibility", "Hold each stretch for 15-30 seconds"),

                // HIIT Exercises
                new Exercise("Burpees", "CARDIO", "FULL_BODY", "NONE", "INTERMEDIATE", 10.0,
                        "Full body explosive exercise", "Jump down to plank, jump back up with arms overhead"),
                new Exercise("High Knees", "CARDIO", "LEGS", "NONE", "BEGINNER", 8.0,
                        "Running in place with high knees", "Bring knees up to hip level alternately"),
                new Exercise("Jumping Jacks", "CARDIO", "FULL_BODY", "NONE", "BEGINNER", 7.0,
                        "Classic jumping jacks", "Jump feet apart while raising arms overhead"));

        exerciseRepository.saveAll(exercises);
    }
}