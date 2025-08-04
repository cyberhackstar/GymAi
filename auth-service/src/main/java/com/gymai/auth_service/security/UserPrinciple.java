package com.gymai.auth_service.security;

import com.gymai.auth_service.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserPrinciple implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(UserPrinciple.class);

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrinciple(User user) {
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Arrays.stream(user.getUserRole().split(","))
                .map(String::trim)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        logger.debug("Created UserPrinciple for user: {}", username);
        logger.debug("Authorities: {}", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", ")));
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
