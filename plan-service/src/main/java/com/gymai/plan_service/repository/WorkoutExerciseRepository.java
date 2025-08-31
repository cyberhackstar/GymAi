// WorkoutExerciseRepository.java
package com.gymai.plan_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.WorkoutExercise;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {
    List<WorkoutExercise> findByDayWorkoutPlanId(Long dayWorkoutPlanId);
}