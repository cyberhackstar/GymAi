// DayWorkoutPlanRepository.java
package com.gymai.plan_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.DayWorkoutPlan;

@Repository
public interface DayWorkoutPlanRepository extends JpaRepository<DayWorkoutPlan, Long> {
    List<DayWorkoutPlan> findByWorkoutPlanId(Long workoutPlanId);
}