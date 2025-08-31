// Fixed WorkoutPlan.java
package com.gymai.plan_service.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "workout_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<DayWorkoutPlan> weeklyPlan = new ArrayList<>();

    @Column(name = "plan_type")
    private String planType;

    @Column(name = "difficulty_level")
    private String difficultyLevel;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }

    // Helper method to add day workout plan
    public void addDayWorkoutPlan(DayWorkoutPlan dayWorkoutPlan) {
        weeklyPlan.add(dayWorkoutPlan);
        dayWorkoutPlan.setWorkoutPlan(this);
    }
}
