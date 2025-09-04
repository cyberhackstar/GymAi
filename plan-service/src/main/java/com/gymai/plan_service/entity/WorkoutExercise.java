// package com.gymai.plan_service.entity;

// import jakarta.persistence.*;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;
// import com.fasterxml.jackson.annotation.JsonBackReference;

// @Entity
// @Table(name = "workout_exercises")
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public class WorkoutExercise {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @ManyToOne(fetch = FetchType.EAGER)
//     @JoinColumn(name = "exercise_id", nullable = false)
//     private Exercise exercise;

//     @Column(name = "sets")
//     private int sets;

//     @Column(name = "reps")
//     private int reps;

//     @Column(name = "duration_minutes")
//     private int durationMinutes;

//     @Column(name = "weight")
//     private double weight;

//     @Column(name = "rest_seconds")
//     private int restSeconds;

//     @Column(name = "calories_burned")
//     private double caloriesBurned;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "day_workout_plan_id")
//     @JsonBackReference
//     private DayWorkoutPlan dayWorkoutPlan;

//     public WorkoutExercise(Exercise exercise, int sets, int reps, int durationMinutes, double weight, int restSeconds) {
//         this.exercise = exercise;
//         this.sets = sets;
//         this.reps = reps;
//         this.durationMinutes = durationMinutes;
//         this.weight = weight;
//         this.restSeconds = restSeconds;
//         calculateCaloriesBurned();
//     }

//     @PostLoad
//     @PostPersist
//     @PostUpdate
//     private void calculateCaloriesBurned() {
//         if (exercise != null) {
//             if (durationMinutes > 0) {
//                 // For timed exercises (usually cardio)
//                 this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * durationMinutes;
//             } else {
//                 // For rep-based exercises, estimate time
//                 double estimatedMinutes = sets * (reps * 0.05 + restSeconds / 60.0);
//                 this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * estimatedMinutes;
//             }
//         }
//     }
// }

package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "workout_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_workout_plan_id")
    @JsonBackReference
    private DayWorkoutPlan dayWorkoutPlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "sets")
    private int sets;

    @Column(name = "reps")
    private int reps;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "weight")
    private double weight;

    @Column(name = "rest_seconds")
    private int restSeconds;

    @Column(name = "calories_burned")
    private double caloriesBurned;

    // Helper method to round to 1 decimal place
    private double roundTo1Decimal(double value) {
        return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public WorkoutExercise(Exercise exercise, int sets, int reps, int durationMinutes, double weight, int restSeconds) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.weight = roundTo1Decimal(weight);
        this.restSeconds = restSeconds;
        calculateCaloriesBurned();
    }

    @PrePersist
    @PreUpdate
    public void calculateCaloriesBurned() {
        if (exercise != null) {
            double burned = 0.0;

            if (durationMinutes > 0) {
                // For cardio exercises with duration
                burned = exercise.getCaloriesBurnedPerMinute() * durationMinutes;
            } else {
                // For strength exercises with sets and reps
                // Estimate based on sets, reps, and exercise intensity
                double baseCaloriesPerSet = exercise.getCaloriesBurnedPerMinute() * 0.5; // Assume 30 seconds per set on
                                                                                         // average
                burned = baseCaloriesPerSet * sets;
            }

            this.caloriesBurned = roundTo1Decimal(burned);
        }
    }

    // Setters that trigger recalculation
    public void setSets(int sets) {
        this.sets = sets;
        calculateCaloriesBurned();
    }

    public void setReps(int reps) {
        this.reps = reps;
        calculateCaloriesBurned();
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        calculateCaloriesBurned();
    }

    public void setWeight(double weight) {
        this.weight = roundTo1Decimal(weight);
        calculateCaloriesBurned();
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        calculateCaloriesBurned();
    }

    // Getters with rounding for safety
    public double getWeight() {
        return roundTo1Decimal(weight);
    }

    public double getCaloriesBurned() {
        return roundTo1Decimal(caloriesBurned);
    }
}