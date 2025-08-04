package com.gymai.user_service.repository;

import com.gymai.user_service.entity.UserProfile;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByEmail(String email);
}
