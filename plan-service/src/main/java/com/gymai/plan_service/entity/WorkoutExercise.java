package com.gymai.plan_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "workout_exercises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id", nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_workout_plan_id")
    @JsonBackReference
    private DayWorkoutPlan dayWorkoutPlan;

    public WorkoutExercise(Exercise exercise, int sets, int reps, int durationMinutes, double weight, int restSeconds) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.weight = weight;
        this.restSeconds = restSeconds;
        calculateCaloriesBurned();
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void calculateCaloriesBurned() {
        if (exercise != null) {
            if (durationMinutes > 0) {
                // For timed exercises (usually cardio)
                this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * durationMinutes;
            } else {
                // For rep-based exercises, estimate time
                double estimatedMinutes = sets * (reps * 0.05 + restSeconds / 60.0);
                this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * estimatedMinutes;
            }
        }
    }
}