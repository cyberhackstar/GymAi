package com.gymai.auth_service.security;

import com.gymai.auth_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);
        return userRepo.findByEmail(email)
                .map(user -> {
                    logger.info("User found: {}", user.getEmail());
                    return new UserPrinciple(user);
                })
                .orElseThrow(() -> {
                    logger.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found");
                });
    }
}
