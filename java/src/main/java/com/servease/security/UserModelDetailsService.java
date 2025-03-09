package com.servease.security;

import com.servease.model.Authority;
import com.servease.model.User;
import com.servease.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserModelDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserModelDetailsService.class);

    private final UserDao userDao;

    public UserModelDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating user '{}'", login);

        String lowercaseLogin = login.toLowerCase();
        User user = userDao.getUserByUsername(lowercaseLogin);

        if (user == null) {
            log.error("Authentication failed: User '{}' not found.", lowercaseLogin);
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found.");
        }

        if (!user.isActivated()) {
            log.error("Authentication failed: User '{}' is not activated.", lowercaseLogin);
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not activated.");
        }

        return createSpringSecurityUser(user);
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(User user) {
        Set<Authority> userAuthorities = user.getAuthorities();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if (userAuthorities != null) {
            grantedAuthorities = userAuthorities.stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toSet());
        } else {
            log.warn("User '{}' has no assigned roles.", user.getUsername());
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),  // Changed from getPassword() to getPasswordHash()
                grantedAuthorities.isEmpty() ? Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")) : grantedAuthorities
        );
    }
}
