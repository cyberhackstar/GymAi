
// DietPlanRepository.java - Fixed for your entities
package com.gymai.plan_service.repository;

import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.gymai.plan_service.entity.DietPlan;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    // Optional<DietPlan> findByUserId(Long userId);
    List<DietPlan> findByUserId(Long userId);

    @Query("SELECT dp FROM DietPlan dp WHERE dp.userId = :userId ORDER BY dp.createdDate DESC")
    List<DietPlan> findByUserIdOrderByCreatedDateDesc(@Param("userId") Long userId);

    default Optional<DietPlan> findLatestByUserId(Long userId) {
        List<DietPlan> plans = findByUserIdOrderByCreatedDateDesc(userId);
        return plans.isEmpty() ? Optional.empty() : Optional.of(plans.get(0));
    }

    boolean existsByUserId(Long userId);
}