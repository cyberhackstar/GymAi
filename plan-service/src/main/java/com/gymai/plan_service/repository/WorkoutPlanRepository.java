// package com.gymai.plan_service.repository;

// import java.util.List;
// import java.util.Optional;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Transactional;

// import com.gymai.plan_service.entity.DayWorkoutPlan;
// import com.gymai.plan_service.entity.WorkoutPlan;

// @Repository
// public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

//     Optional<WorkoutPlan> findByUserId(Long userId);

//     // Fixed query - avoid multiple bag fetch by using separate queries
//     @Query("SELECT DISTINCT wp FROM WorkoutPlan wp WHERE wp.userId = :userId ORDER BY wp.createdDate DESC")
//     List<WorkoutPlan> findByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId);

//     // Separate query to fetch weekly plans
//     @Query("SELECT DISTINCT wp FROM WorkoutPlan wp LEFT JOIN FETCH wp.weeklyPlan WHERE wp.id = :planId")
//     Optional<WorkoutPlan> findByIdWithWeeklyPlan(@Param("planId") Long planId);

//     // Separate query to fetch exercises for day plans
//     @Query("SELECT DISTINCT dwp FROM DayWorkoutPlan dwp LEFT JOIN FETCH dwp.exercises we LEFT JOIN FETCH we.exercise WHERE dwp.workoutPlan.id = :planId")
//     List<DayWorkoutPlan> findDayPlansWithExercisesByPlanId(@Param("planId") Long planId);

//     default Optional<WorkoutPlan> findLatestByUserIdWithDays(Long userId) {
//         List<WorkoutPlan> plans = findByUserIdOrderByCreatedDateDesc(userId);
//         if (plans.isEmpty()) {
//             return Optional.empty();
//         }

//         WorkoutPlan latestPlan = plans.get(0);

//         // Fetch the weekly plan separately
//         Optional<WorkoutPlan> planWithWeeklyPlan = findByIdWithWeeklyPlan(latestPlan.getId());
//         if (planWithWeeklyPlan.isPresent()) {
//             WorkoutPlan fullPlan = planWithWeeklyPlan.get();
//             // Fetch exercises for each day plan
//             List<DayWorkoutPlan> dayPlansWithExercises = findDayPlansWithExercisesByPlanId(fullPlan.getId());
//             // The exercises will be automatically associated due to the mappedBy
//             // relationship
//             return Optional.of(fullPlan);
//         }

//         return Optional.of(latestPlan);
//     }

//     @Modifying
//     @Transactional
//     @Query("DELETE FROM WorkoutPlan wp WHERE wp.userId = :userId")
//     void deleteByUserId(@Param("userId") Long userId);

//     boolean existsByUserId(Long userId);
// }

// WorkoutPlanRepository.java - Fixed for your entities  
package com.gymai.plan_service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.WorkoutPlan;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserId(Long userId);

    // Optional<WorkoutPlan> findByUserId(Long userId);

    @Query("SELECT wp FROM WorkoutPlan wp WHERE wp.userId = :userId ORDER BY wp.createdDate DESC")
    List<WorkoutPlan> findByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId);

    default Optional<WorkoutPlan> findLatestByUserId(Long userId) {
        List<WorkoutPlan> plans = findByUserIdOrderByCreatedDateDesc(userId);
        return plans.isEmpty() ? Optional.empty() : Optional.of(plans.get(0));
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM WorkoutPlan wp WHERE wp.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);

}
