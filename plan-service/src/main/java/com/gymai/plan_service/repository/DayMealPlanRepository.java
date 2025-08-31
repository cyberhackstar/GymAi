// DayMealPlanRepository.java
package com.gymai.plan_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.DayMealPlan;

@Repository
public interface DayMealPlanRepository extends JpaRepository<DayMealPlan, Long> {
    List<DayMealPlan> findByDietPlanId(Long dietPlanId);
}