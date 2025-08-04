package com.gymai.auth_service.repository;

import com.gymai.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom queries on the 'users' table.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     *
     * @param email the user's email
     * @return an Optional containing the User if found
     */
    Optional<User> findByEmail(String email);

    // You can add more queries like existsByEmail, findByRole, etc.
}
