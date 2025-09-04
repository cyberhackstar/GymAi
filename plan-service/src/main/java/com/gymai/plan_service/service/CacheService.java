package com.gymai.plan_service.service;

import com.gymai.plan_service.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CacheService {

  @Autowired(required = false)
  private RedisTemplate<String, Object> redisTemplate;

  @Value("${cache.redis.enabled:false}")
  private boolean cacheEnabled;

  // Cache Keys
  private static final String USER_PROFILE_KEY = "user:profile:";
  private static final String DIET_PLAN_KEY = "diet:plan:";
  private static final String WORKOUT_PLAN_KEY = "workout:plan:";
  private static final String NUTRITION_ANALYSIS_KEY = "nutrition:analysis:";
  private static final String PLANS_RESPONSE_KEY = "plans:response:";
  private static final String FOODS_BY_PREFERENCE_KEY = "foods:preference:";
  private static final String EXERCISES_BY_FOCUS_KEY = "exercises:focus:";

  // TTL in seconds
  private static final long USER_PROFILE_TTL = 3600;
  private static final long DIET_PLAN_TTL = 86400;
  private static final long WORKOUT_PLAN_TTL = 86400;
  private static final long NUTRITION_ANALYSIS_TTL = 3600;
  private static final long PLANS_RESPONSE_TTL = 7200;
  private static final long FOODS_BY_PREFERENCE_TTL = 21600;
  private static final long EXERCISES_BY_FOCUS_TTL = 21600;

  private boolean isCacheAvailableInternal() {
    return cacheEnabled && redisTemplate != null;
  }

  // Safe cache operations with ClassCastException handling
  private <T> T safeGet(String key, Class<T> expectedClass) {
    if (!isCacheAvailableInternal()) {
      return null;
    }

    try {
      Object cached = redisTemplate.opsForValue().get(key);
      if (cached != null && expectedClass.isInstance(cached)) {
        return expectedClass.cast(cached);
      }
    } catch (ClassCastException e) {
      log.warn("ClassCastException for key: {} - clearing cache entry. Error: {}", key, e.getMessage());
      // Clear the problematic cache entry
      try {
        redisTemplate.delete(key);
      } catch (Exception deleteEx) {
        log.warn("Failed to delete problematic cache entry: {}", key);
      }
    } catch (Exception e) {
      log.warn("Error retrieving from cache for key: {} - {}", key, e.getMessage());
    }
    return null;
  }

  private void safeSet(String key, Object value, long ttl) {
    if (!isCacheAvailableInternal()) {
      return;
    }

    try {
      redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    } catch (Exception e) {
      log.warn("Failed to cache for key: {} - {}", key, e.getMessage());
    }
  }

  // User Profile Caching
  public void cacheUserProfile(String email, UserProfileDTO userProfile) {
    if (!isCacheAvailableInternal()) {
      log.debug("Cache disabled - skipping user profile caching for email: {}", email);
      return;
    }

    String key = USER_PROFILE_KEY + email;
    safeSet(key, userProfile, USER_PROFILE_TTL);
    log.debug("Cached user profile for email: {}", email);
  }

  public UserProfileDTO getCachedUserProfile(String email) {
    String key = USER_PROFILE_KEY + email;
    UserProfileDTO cached = safeGet(key, UserProfileDTO.class);
    if (cached != null) {
      log.debug("Retrieved cached user profile for email: {}", email);
    }
    return cached;
  }

  // Diet Plan Caching
  public void cacheDietPlan(Long userId, SimpleDietPlanDTO dietPlan) {
    if (!isCacheAvailableInternal()) {
      log.debug("Cache disabled - skipping diet plan caching for userId: {}", userId);
      return;
    }

    String key = DIET_PLAN_KEY + userId;
    safeSet(key, dietPlan, DIET_PLAN_TTL);
    log.debug("Cached diet plan for userId: {}", userId);
  }

  public SimpleDietPlanDTO getCachedDietPlan(Long userId) {
    String key = DIET_PLAN_KEY + userId;
    SimpleDietPlanDTO cached = safeGet(key, SimpleDietPlanDTO.class);
    if (cached != null) {
      log.debug("Retrieved cached diet plan for userId: {}", userId);
    }
    return cached;
  }

  // Workout Plan Caching
  public void cacheWorkoutPlan(Long userId, SimpleWorkoutPlanDTO workoutPlan) {
    if (!isCacheAvailableInternal()) {
      log.debug("Cache disabled - skipping workout plan caching for userId: {}", userId);
      return;
    }

    String key = WORKOUT_PLAN_KEY + userId;
    safeSet(key, workoutPlan, WORKOUT_PLAN_TTL);
    log.debug("Cached workout plan for userId: {}", userId);
  }

  public SimpleWorkoutPlanDTO getCachedWorkoutPlan(Long userId) {
    String key = WORKOUT_PLAN_KEY + userId;
    SimpleWorkoutPlanDTO cached = safeGet(key, SimpleWorkoutPlanDTO.class);
    if (cached != null) {
      log.debug("Retrieved cached workout plan for userId: {}", userId);
    }
    return cached;
  }

  // Nutrition Analysis Caching
  public void cacheNutritionAnalysis(Long userId, NutritionAnalysis analysis) {
    String key = NUTRITION_ANALYSIS_KEY + userId;
    safeSet(key, analysis, NUTRITION_ANALYSIS_TTL);
    log.debug("Cached nutrition analysis for userId: {}", userId);
  }

  public NutritionAnalysis getCachedNutritionAnalysis(Long userId) {
    String key = NUTRITION_ANALYSIS_KEY + userId;
    NutritionAnalysis cached = safeGet(key, NutritionAnalysis.class);
    if (cached != null) {
      log.debug("Retrieved cached nutrition analysis for userId: {}", userId);
    }
    return cached;
  }

  // Complete Plans Response Caching
  public void cachePlansResponse(String email, OptimizedPlansResponseDTO response) {
    String key = PLANS_RESPONSE_KEY + email;
    safeSet(key, response, PLANS_RESPONSE_TTL);
    log.debug("Cached complete plans response for email: {}", email);
  }

  public OptimizedPlansResponseDTO getCachedPlansResponse(String email) {
    String key = PLANS_RESPONSE_KEY + email;
    OptimizedPlansResponseDTO cached = safeGet(key, OptimizedPlansResponseDTO.class);
    if (cached != null) {
      log.debug("Retrieved cached plans response for email: {}", email);
    }
    return cached;
  }

  // Foods by Preference Caching - SKIP CACHING OF ENTITY OBJECTS
  public void cacheFoodsByPreference(String preference, Object foods) {
    // Disable caching of entity objects to avoid ClassCastException
    log.debug("Skipping foods cache for preference: {} to avoid ClassCastException", preference);
    // Don't cache Food entities - they cause classloader issues
  }

  public Object getCachedFoodsByPreference(String preference) {
    // Always return null to force database lookup
    log.debug("Skipping foods cache lookup for preference: {} to avoid ClassCastException", preference);
    return null;
  }

  // Exercises by Focus Area Caching - SKIP CACHING OF ENTITY OBJECTS
  public void cacheExercisesByFocus(String focusArea, String difficulty, Object exercises) {
    // Disable caching of entity objects to avoid ClassCastException
    log.debug("Skipping exercises cache for focusArea: {} and difficulty: {} to avoid ClassCastException",
        focusArea, difficulty);
    // Don't cache Exercise entities - they cause classloader issues
  }

  public Object getCachedExercisesByFocus(String focusArea, String difficulty) {
    // Always return null to force database lookup
    log.debug("Skipping exercises cache lookup for focusArea: {} and difficulty: {} to avoid ClassCastException",
        focusArea, difficulty);
    return null;
  }

  // Cache Invalidation Methods
  public void invalidateUserCache(String email) {
    if (!isCacheAvailableInternal()) {
      return;
    }

    try {
      redisTemplate.delete(USER_PROFILE_KEY + email);
      redisTemplate.delete(PLANS_RESPONSE_KEY + email);
      log.debug("Invalidated user cache for email: {}", email);
    } catch (Exception e) {
      log.warn("Failed to invalidate user cache for email: {} - {}", email, e.getMessage());
    }
  }

  public void invalidateUserPlansCache(Long userId) {
    if (!isCacheAvailableInternal()) {
      return;
    }

    try {
      redisTemplate.delete(DIET_PLAN_KEY + userId);
      redisTemplate.delete(WORKOUT_PLAN_KEY + userId);
      redisTemplate.delete(NUTRITION_ANALYSIS_KEY + userId);
      log.debug("Invalidated plans cache for userId: {}", userId);
    } catch (Exception e) {
      log.warn("Failed to invalidate plans cache for userId: {} - {}", userId, e.getMessage());
    }
  }

  public void invalidateAllUserCache(String email, Long userId) {
    if (!isCacheAvailableInternal()) {
      return;
    }

    try {
      invalidateUserCache(email);
      invalidateUserPlansCache(userId);
      // Also clear any food/exercise caches that might cause issues
      clearFoodAndExerciseCaches();
      log.debug("Invalidated all cache for email: {} and userId: {}", email, userId);
    } catch (Exception e) {
      log.warn("Failed to invalidate all cache for email: {} and userId: {} - {}", email, userId, e.getMessage());
    }
  }

  private void clearFoodAndExerciseCaches() {
    if (!isCacheAvailableInternal()) {
      return;
    }

    try {
      // Clear all food and exercise caches that might have classloader issues
      redisTemplate.delete(redisTemplate.keys(FOODS_BY_PREFERENCE_KEY + "*"));
      redisTemplate.delete(redisTemplate.keys(EXERCISES_BY_FOCUS_KEY + "*"));
      log.debug("Cleared food and exercise caches");
    } catch (Exception e) {
      log.warn("Failed to clear food and exercise caches: {}", e.getMessage());
    }
  }

  // Cache Health Check
  public boolean isCacheAvailable() {
    if (!isCacheAvailableInternal()) {
      return false;
    }

    try {
      String testKey = "health:check:" + System.currentTimeMillis();
      redisTemplate.opsForValue().set(testKey, "ok", 5, TimeUnit.SECONDS);
      boolean result = "ok".equals(redisTemplate.opsForValue().get(testKey));
      redisTemplate.delete(testKey);
      return result;
    } catch (Exception e) {
      log.warn("Redis cache health check failed: {}", e.getMessage());
      return false;
    }
  }

  public void clearAllCache() {
    if (!isCacheAvailableInternal()) {
      log.warn("Cannot clear cache - Redis not available");
      return;
    }

    try {
      redisTemplate.getConnectionFactory().getConnection().flushAll();
      log.info("Cleared all Redis cache");
    } catch (Exception e) {
      log.error("Error clearing all cache: {}", e.getMessage());
    }
  }
}