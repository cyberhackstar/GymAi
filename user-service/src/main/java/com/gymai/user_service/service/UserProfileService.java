package com.gymai.user_service.service;

import com.gymai.user_service.dto.UserDto;
import com.gymai.user_service.entity.UserProfile;
import com.gymai.user_service.exception.ResourceNotFoundException;
import com.gymai.user_service.kafka.producer.UserEventProducer;
import com.gymai.user_service.mapper.UserMapper;
import com.gymai.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository repository;
    private final UserMapper mapper;
    private final UserEventProducer producer;

    public UserDto saveOrUpdate(Long userId, UserDto dto) {
        log.info("📝 Saving or updating user profile for userId: {}", userId);

        UserProfile profile = mapper.toEntity(dto);
        profile.setUserId(userId);

        UserProfile saved = repository.save(profile);
        log.info("✅ Profile saved: {}", saved);

        UserDto result = mapper.toDto(saved);
        producer.sendUserEvent(result);
        log.info("📤 User event sent to Kafka for userId: {}", userId);

        return result;
    }

    public UserDto getById(Long userId) {
        log.info("🔍 Fetching user profile for userId: {}", userId);

        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("❌ User not found with ID: {}", userId);
                    return new ResourceNotFoundException("User not found with ID: " + userId);
                });

        log.info("✅ Found user profile: {}", profile);
        return mapper.toDto(profile);
    }
}
