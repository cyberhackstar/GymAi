
// MealRepository.java
package com.gymai.plan_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gymai.plan_service.entity.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByDayMealPlanId(Long dayMealPlanId);
}