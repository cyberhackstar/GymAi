// // Updated DayWorkoutPlan.java - Now a JPA Entity
// package com.gymai.plan_service.entity;

// import java.util.ArrayList;
// import java.util.List;
// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;
// import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.annotation.JsonManagedReference;

// @Entity
// @Table(name = "day_workout_plans")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // avoid proxy issues in JSON
// public class DayWorkoutPlan {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(name = "day_number")
//     private int dayNumber;

//     @Column(name = "day_name")
//     private String dayName;

//     @Column(name = "focus_area")
//     private String focusArea;

//     @OneToMany(mappedBy = "dayWorkoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//     @JsonManagedReference
//     private List<WorkoutExercise> exercises = new ArrayList<>();

//     @Column(name = "estimated_duration_minutes")
//     private int estimatedDurationMinutes;

//     @Column(name = "total_calories_burned")
//     private double totalCaloriesBurned;

//     @Column(name = "is_rest_day")
//     private boolean restDay;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "workout_plan_id")
//     @JsonBackReference
//     private WorkoutPlan workoutPlan;

//     public DayWorkoutPlan(int dayNumber, String dayName, String focusArea) {
//         this.dayNumber = dayNumber;
//         this.dayName = dayName;
//         this.focusArea = focusArea;
//         this.exercises = new ArrayList<>();
//         this.restDay = "REST".equals(focusArea);
//     }

//     public void addExercise(WorkoutExercise exercise) {
//         exercise.setDayWorkoutPlan(this);
//         this.exercises.add(exercise);
//         updateTotals();
//     }

//     private void updateTotals() {
//         this.totalCaloriesBurned = exercises.stream().mapToDouble(WorkoutExercise::getCaloriesBurned).sum();
//         this.estimatedDurationMinutes = (int) exercises.stream()
//                 .mapToDouble(ex -> ex.getSets() * (ex.getReps() * 0.05 + ex.getRestSeconds() / 60.0)
//                         + ex.getDurationMinutes())
//                 .sum();
//     }
// }

package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "day_workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayWorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    @JsonBackReference
    private WorkoutPlan workoutPlan;

    @OneToMany(mappedBy = "dayWorkoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutExercise> exercises = new ArrayList<>();

    @Column(name = "day_number")
    private int dayNumber;

    @Column(name = "day_name")
    private String dayName;

    @Column(name = "focus_area")
    private String focusArea;

    @Column(name = "rest_day")
    private boolean restDay;

    @Column(name = "estimated_duration_minutes")
    private int estimatedDurationMinutes;

    @Column(name = "total_calories_burned")
    private double totalCaloriesBurned;

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public DayWorkoutPlan(int dayNumber, String dayName, String focusArea) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.focusArea = focusArea;
        this.restDay = false;
        this.exercises = new ArrayList<>();
    }

    public void addExercise(WorkoutExercise workoutExercise) {
        exercises.add(workoutExercise);
        workoutExercise.setDayWorkoutPlan(this);
        calculateTotals();
    }

    public void removeExercise(WorkoutExercise workoutExercise) {
        exercises.remove(workoutExercise);
        workoutExercise.setDayWorkoutPlan(null);
        calculateTotals();
    }

    @PrePersist
    @PreUpdate
    public void calculateTotals() {
        if (restDay) {
            this.estimatedDurationMinutes = 0;
            this.totalCaloriesBurned = 0.0;
            return;
        }

        // Calculate total duration
        int cardioMinutes = exercises.stream()
                .mapToInt(ex -> ex.getDurationMinutes() > 0 ? ex.getDurationMinutes() : 0)
                .sum();

        int strengthMinutes = (int) exercises.stream()
                .filter(ex -> ex.getDurationMinutes() == 0)
                .count() * 5; // Estimate 5 minutes per strength exercise (including rest)

        this.estimatedDurationMinutes = cardioMinutes + strengthMinutes;

        // Calculate total calories burned
        this.totalCaloriesBurned = roundTo1Decimal(
                exercises.stream().mapToDouble(WorkoutExercise::getCaloriesBurned).sum());
    }

    // Getters with rounding for safety
    public double getTotalCaloriesBurned() {
        calculateTotals(); // Ensure fresh calculation
        return roundTo1Decimal(totalCaloriesBurned);
    }

    public void setRestDay(boolean restDay) {
        this.restDay = restDay;
        if (restDay) {
            this.exercises.clear();
            this.estimatedDurationMinutes = 0;
            this.totalCaloriesBurned = 0.0;
        }
    }
}