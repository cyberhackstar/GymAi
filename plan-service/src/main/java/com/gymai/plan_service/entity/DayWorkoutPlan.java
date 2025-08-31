// Updated DayWorkoutPlan.java - Now a JPA Entity
package com.gymai.plan_service.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "day_workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // avoid proxy issues in JSON
public class DayWorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_number")
    private int dayNumber;

    @Column(name = "day_name")
    private String dayName;

    @Column(name = "focus_area")
    private String focusArea;

    @OneToMany(mappedBy = "dayWorkoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<WorkoutExercise> exercises = new ArrayList<>();

    @Column(name = "estimated_duration_minutes")
    private int estimatedDurationMinutes;

    @Column(name = "total_calories_burned")
    private double totalCaloriesBurned;

    @Column(name = "is_rest_day")
    private boolean restDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id")
    @JsonBackReference
    private WorkoutPlan workoutPlan;

    public DayWorkoutPlan(int dayNumber, String dayName, String focusArea) {
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.focusArea = focusArea;
        this.exercises = new ArrayList<>();
        this.restDay = "REST".equals(focusArea);
    }

    public void addExercise(WorkoutExercise exercise) {
        exercise.setDayWorkoutPlan(this);
        this.exercises.add(exercise);
        updateTotals();
    }

    private void updateTotals() {
        this.totalCaloriesBurned = exercises.stream().mapToDouble(WorkoutExercise::getCaloriesBurned).sum();
        this.estimatedDurationMinutes = (int) exercises.stream()
                .mapToDouble(ex -> ex.getSets() * (ex.getReps() * 0.05 + ex.getRestSeconds() / 60.0)
                        + ex.getDurationMinutes())
                .sum();
    }
}