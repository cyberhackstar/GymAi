package com.gymai.user_service.service;

import com.gymai.user_service.dto.UserDto;
import com.gymai.user_service.entity.UserProfile;
import com.gymai.user_service.exception.ResourceNotFoundException;
import com.gymai.user_service.mapper.UserMapper;
import com.gymai.user_service.producer.UserEventProducer;
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
        log.info("Saving or updating user profile for userId: {}", userId);

        UserProfile profile = mapper.toEntity(dto);
        profile.setUserId(userId);

        UserProfile saved = repository.save(profile);
        log.info("Profile saved: {}", saved);

        UserDto result = mapper.toDto(saved);
        producer.publishUserEvent(result);
        log.info("User event published to RabbitMQ for userId: {}", userId);

        return result;
    }

    public UserDto getById(Long userId) {
        log.debug("Fetching user profile for userId: {}", userId);

        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return mapper.toDto(profile);
    }

    public boolean existsById(Long userId) {
        return repository.existsById(userId);
    }
}