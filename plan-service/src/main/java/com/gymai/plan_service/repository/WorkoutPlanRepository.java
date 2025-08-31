package com.gymai.plan_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.WorkoutPlan;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    Optional<WorkoutPlan> findByUserId(Long userId);

    @Query("SELECT DISTINCT wp FROM WorkoutPlan wp LEFT JOIN FETCH wp.weeklyPlan WHERE wp.userId = :userId ORDER BY wp.createdDate DESC")
    Optional<WorkoutPlan> findLatestByUserIdWithDays(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);
}