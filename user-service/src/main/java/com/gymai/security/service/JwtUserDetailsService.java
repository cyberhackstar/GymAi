package com.gymai.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.trim().isEmpty()) {
            log.warn("Attempt to load user with null or empty email.");
            throw new UsernameNotFoundException("Email is missing in token");
        }

        log.info("Loading UserDetails for email: {}", email);

        // Return dummy user since we're just validating JWT subject
        return User.withUsername(email)
                .password("") // no password needed since we're not authenticating via form
                .authorities("ROLE_USER") // or set roles based on claim in token
                .build();
    }
}
