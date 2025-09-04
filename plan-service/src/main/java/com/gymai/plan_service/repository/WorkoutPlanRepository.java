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

    boolean existsByUserId(Long userId);

}
