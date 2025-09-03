package com.gymai.plan_service.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Use String serializer for keys
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());

    // Use JSON serializer for values
    template.setValueSerializer(jsonRedisSerializer());
    template.setHashValueSerializer(jsonRedisSerializer());

    template.setDefaultSerializer(jsonRedisSerializer());
    template.afterPropertiesSet();

    log.info("RedisTemplate configured successfully");
    return template;
  }

  @Bean
  public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    return new GenericJackson2JsonRedisSerializer(objectMapper);
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
            .fromSerializer(jsonRedisSerializer()))
        .entryTtl(Duration.ofHours(1)); // Default TTL

    // Configure different TTLs for different cache types
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    cacheConfigurations.put("user-profiles", defaultConfig.entryTtl(Duration.ofHours(1)));
    cacheConfigurations.put("diet-plans", defaultConfig.entryTtl(Duration.ofHours(24)));
    cacheConfigurations.put("workout-plans", defaultConfig.entryTtl(Duration.ofHours(24)));
    cacheConfigurations.put("nutrition-analysis", defaultConfig.entryTtl(Duration.ofHours(1)));
    cacheConfigurations.put("plans-response", defaultConfig.entryTtl(Duration.ofHours(2)));
    cacheConfigurations.put("foods-by-preference", defaultConfig.entryTtl(Duration.ofHours(6)));
    cacheConfigurations.put("exercises-by-focus", defaultConfig.entryTtl(Duration.ofHours(6)));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}