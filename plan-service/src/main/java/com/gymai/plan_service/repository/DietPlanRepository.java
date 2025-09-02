// package com.gymai.plan_service.repository;

// import java.util.Optional;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Transactional;
// import com.gymai.plan_service.entity.DietPlan;

// @Repository
// public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

//     Optional<DietPlan> findByUserId(Long userId);

//     // Fixed query - fetch only basic plan first, then load relationships separately
//     @Query("SELECT dp FROM DietPlan dp WHERE dp.userId = :userId ORDER BY dp.createdDate DESC")
//     Optional<DietPlan> findLatestByUserId(@Param("userId") Long userId);

//     // Separate query to fetch only daily plans
//     @Query("SELECT DISTINCT dp FROM DietPlan dp LEFT JOIN FETCH dp.dailyPlans WHERE dp.id = :planId")
//     Optional<DietPlan> findByIdWithDailyPlans(@Param("planId") Long planId);

//     // Separate query to fetch meals for daily plans
//     @Query("SELECT DISTINCT dmp FROM DayMealPlan dmp LEFT JOIN FETCH dmp.meals WHERE dmp.dietPlan.id = :planId")
//     java.util.List<com.gymai.plan_service.entity.DayMealPlan> findDayMealPlansWithMeals(@Param("planId") Long planId);

//     // Separate query to fetch food items for meals
//     @Query("SELECT DISTINCT m FROM Meal m LEFT JOIN FETCH m.foodItems fi LEFT JOIN FETCH fi.food WHERE m.dayMealPlan.dietPlan.id = :planId")
//     java.util.List<com.gymai.plan_service.entity.Meal> findMealsWithFoodItemsByPlanId(@Param("planId") Long planId);

//     default Optional<DietPlan> findLatestByUserIdWithDays(Long userId) {
//         Optional<DietPlan> planOpt = findLatestByUserId(userId);
//         if (!planOpt.isPresent()) {
//             return Optional.empty();
//         }

//         DietPlan plan = planOpt.get();

//         // Step 1: Load the diet plan with daily plans (no meals yet)
//         Optional<DietPlan> planWithDailyPlans = findByIdWithDailyPlans(plan.getId());
//         if (!planWithDailyPlans.isPresent()) {
//             return planOpt;
//         }

//         DietPlan fullPlan = planWithDailyPlans.get();

//         // Step 2: Load day meal plans with meals separately
//         findDayMealPlansWithMeals(fullPlan.getId());

//         // Step 3: Load food items separately (this will be associated automatically due
//         // to mappedBy)
//         findMealsWithFoodItemsByPlanId(fullPlan.getId());

//         return Optional.of(fullPlan);
//     }

//     @Modifying
//     @Transactional
//     @Query("DELETE FROM DietPlan dp WHERE dp.userId = :userId")
//     void deleteByUserId(@Param("userId") Long userId);

//     boolean existsByUserId(Long userId);
// }

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

    @Modifying
    @Transactional
    @Query("DELETE FROM DietPlan dp WHERE dp.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);
}