// Fixed DietPlanRepository.java
package com.gymai.plan_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.DietPlan;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    Optional<DietPlan> findByUserId(Long userId);

    @Query("SELECT DISTINCT dp FROM DietPlan dp LEFT JOIN FETCH dp.dailyPlans WHERE dp.userId = :userId ORDER BY dp.createdDate DESC")
    Optional<DietPlan> findLatestByUserIdWithDays(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    boolean existsByUserId(Long userId);
}