package com.gymai.plan_service.service;

import com.gymai.plan_service.dto.*;
import com.gymai.plan_service.entity.DietPlan;
import com.gymai.plan_service.entity.User;
import com.gymai.plan_service.entity.WorkoutPlan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CacheService {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  // Cache Keys
  private static final String USER_PROFILE_KEY = "user:profile:";
  private static final String DIET_PLAN_KEY = "diet:plan:";
  private static final String WORKOUT_PLAN_KEY = "workout:plan:";
  private static final String NUTRITION_ANALYSIS_KEY = "nutrition:analysis:";
  private static final String PLANS_RESPONSE_KEY = "plans:response:";
  private static final String FOODS_BY_PREFERENCE_KEY = "foods:preference:";
  private static final String EXERCISES_BY_FOCUS_KEY = "exercises:focus:";

  // TTL in seconds
  private static final long USER_PROFILE_TTL = 3600; // 1 hour
  private static final long DIET_PLAN_TTL = 86400; // 24 hours
  private static final long WORKOUT_PLAN_TTL = 86400; // 24 hours
  private static final long NUTRITION_ANALYSIS_TTL = 3600; // 1 hour
  private static final long PLANS_RESPONSE_TTL = 7200; // 2 hours
  private static final long FOODS_BY_PREFERENCE_TTL = 21600; // 6 hours
  private static final long EXERCISES_BY_FOCUS_TTL = 21600; // 6 hours

  // User Profile Caching
  public void cacheUserProfile(String email, UserProfileDTO userProfile) {
    try {
      String key = USER_PROFILE_KEY + email;
      redisTemplate.opsForValue().set(key, userProfile, USER_PROFILE_TTL, TimeUnit.SECONDS);
      log.debug("Cached user profile for email: {}", email);
    } catch (Exception e) {
      log.error("Error caching user profile for email: {}", email, e);
    }
  }

  public UserProfileDTO getCachedUserProfile(String email) {
    try {
      String key = USER_PROFILE_KEY + email;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached instanceof UserProfileDTO) {
        log.debug("Retrieved cached user profile for email: {}", email);
        return (UserProfileDTO) cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached user profile for email: {}", email, e);
    }
    return null;
  }

  // Diet Plan Caching
  public void cacheDietPlan(Long userId, SimpleDietPlanDTO dietPlan) {
    try {
      String key = DIET_PLAN_KEY + userId;
      redisTemplate.opsForValue().set(key, dietPlan, DIET_PLAN_TTL, TimeUnit.SECONDS);
      log.debug("Cached diet plan for userId: {}", userId);
    } catch (Exception e) {
      log.error("Error caching diet plan for userId: {}", userId, e);
    }
  }

  public SimpleDietPlanDTO getCachedDietPlan(Long userId) {
    try {
      String key = DIET_PLAN_KEY + userId;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached instanceof SimpleDietPlanDTO) {
        log.debug("Retrieved cached diet plan for userId: {}", userId);
        return (SimpleDietPlanDTO) cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached diet plan for userId: {}", userId, e);
    }
    return null;
  }

  // Workout Plan Caching
  public void cacheWorkoutPlan(Long userId, SimpleWorkoutPlanDTO workoutPlan) {
    try {
      String key = WORKOUT_PLAN_KEY + userId;
      redisTemplate.opsForValue().set(key, workoutPlan, WORKOUT_PLAN_TTL, TimeUnit.SECONDS);
      log.debug("Cached workout plan for userId: {}", userId);
    } catch (Exception e) {
      log.error("Error caching workout plan for userId: {}", userId, e);
    }
  }

  public SimpleWorkoutPlanDTO getCachedWorkoutPlan(Long userId) {
    try {
      String key = WORKOUT_PLAN_KEY + userId;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached instanceof SimpleWorkoutPlanDTO) {
        log.debug("Retrieved cached workout plan for userId: {}", userId);
        return (SimpleWorkoutPlanDTO) cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached workout plan for userId: {}", userId, e);
    }
    return null;
  }

  // Nutrition Analysis Caching
  public void cacheNutritionAnalysis(Long userId, NutritionAnalysis analysis) {
    try {
      String key = NUTRITION_ANALYSIS_KEY + userId;
      redisTemplate.opsForValue().set(key, analysis, NUTRITION_ANALYSIS_TTL, TimeUnit.SECONDS);
      log.debug("Cached nutrition analysis for userId: {}", userId);
    } catch (Exception e) {
      log.error("Error caching nutrition analysis for userId: {}", userId, e);
    }
  }

  public NutritionAnalysis getCachedNutritionAnalysis(Long userId) {
    try {
      String key = NUTRITION_ANALYSIS_KEY + userId;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached instanceof NutritionAnalysis) {
        log.debug("Retrieved cached nutrition analysis for userId: {}", userId);
        return (NutritionAnalysis) cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached nutrition analysis for userId: {}", userId, e);
    }
    return null;
  }

  // Complete Plans Response Caching
  public void cachePlansResponse(String email, OptimizedPlansResponseDTO response) {
    try {
      String key = PLANS_RESPONSE_KEY + email;
      redisTemplate.opsForValue().set(key, response, PLANS_RESPONSE_TTL, TimeUnit.SECONDS);
      log.debug("Cached complete plans response for email: {}", email);
    } catch (Exception e) {
      log.error("Error caching plans response for email: {}", email, e);
    }
  }

  public OptimizedPlansResponseDTO getCachedPlansResponse(String email) {
    try {
      String key = PLANS_RESPONSE_KEY + email;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached instanceof OptimizedPlansResponseDTO) {
        log.debug("Retrieved cached plans response for email: {}", email);
        return (OptimizedPlansResponseDTO) cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached plans response for email: {}", email, e);
    }
    return null;
  }

  // Foods by Preference Caching
  @SuppressWarnings("unchecked")
  public void cacheFoodsByPreference(String preference, Object foods) {
    try {
      String key = FOODS_BY_PREFERENCE_KEY + preference;
      redisTemplate.opsForValue().set(key, foods, FOODS_BY_PREFERENCE_TTL, TimeUnit.SECONDS);
      log.debug("Cached foods for preference: {}", preference);
    } catch (Exception e) {
      log.error("Error caching foods for preference: {}", preference, e);
    }
  }

  public Object getCachedFoodsByPreference(String preference) {
    try {
      String key = FOODS_BY_PREFERENCE_KEY + preference;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached != null) {
        log.debug("Retrieved cached foods for preference: {}", preference);
        return cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached foods for preference: {}", preference, e);
    }
    return null;
  }

  // Exercises by Focus Area Caching
  public void cacheExercisesByFocus(String focusArea, String difficulty, Object exercises) {
    try {
      String key = EXERCISES_BY_FOCUS_KEY + focusArea + ":" + difficulty;
      redisTemplate.opsForValue().set(key, exercises, EXERCISES_BY_FOCUS_TTL, TimeUnit.SECONDS);
      log.debug("Cached exercises for focusArea: {} and difficulty: {}", focusArea, difficulty);
    } catch (Exception e) {
      log.error("Error caching exercises for focusArea: {} and difficulty: {}", focusArea, difficulty, e);
    }
  }

  public Object getCachedExercisesByFocus(String focusArea, String difficulty) {
    try {
      String key = EXERCISES_BY_FOCUS_KEY + focusArea + ":" + difficulty;
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached != null) {
        log.debug("Retrieved cached exercises for focusArea: {} and difficulty: {}", focusArea, difficulty);
        return cached;
      }
    } catch (Exception e) {
      log.error("Error retrieving cached exercises for focusArea: {} and difficulty: {}", focusArea, difficulty, e);
    }
    return null;
  }

  // Cache Invalidation Methods
  public void invalidateUserCache(String email) {
    try {
      redisTemplate.delete(USER_PROFILE_KEY + email);
      redisTemplate.delete(PLANS_RESPONSE_KEY + email);
      log.debug("Invalidated user cache for email: {}", email);
    } catch (Exception e) {
      log.error("Error invalidating user cache for email: {}", email, e);
    }
  }

  public void invalidateUserPlansCache(Long userId) {
    try {
      redisTemplate.delete(DIET_PLAN_KEY + userId);
      redisTemplate.delete(WORKOUT_PLAN_KEY + userId);
      redisTemplate.delete(NUTRITION_ANALYSIS_KEY + userId);
      log.debug("Invalidated plans cache for userId: {}", userId);
    } catch (Exception e) {
      log.error("Error invalidating plans cache for userId: {}", userId, e);
    }
  }

  public void invalidateAllUserCache(String email, Long userId) {
    try {
      invalidateUserCache(email);
      invalidateUserPlansCache(userId);
      log.debug("Invalidated all cache for email: {} and userId: {}", email, userId);
    } catch (Exception e) {
      log.error("Error invalidating all cache for email: {} and userId: {}", email, userId, e);
    }
  }

  // Cache Statistics
  public boolean isCacheAvailable() {
    try {
      redisTemplate.opsForValue().set("health:check", "ok", 5, TimeUnit.SECONDS);
      return "ok".equals(redisTemplate.opsForValue().get("health:check"));
    } catch (Exception e) {
      log.error("Redis cache is not available", e);
      return false;
    }
  }

  public void clearAllCache() {
    try {
      redisTemplate.getConnectionFactory().getConnection().flushAll();
      log.info("Cleared all Redis cache");
    } catch (Exception e) {
      log.error("Error clearing all cache", e);
    }
  }
}