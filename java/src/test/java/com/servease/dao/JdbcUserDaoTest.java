package com.servease.dao;

import com.servease.exception.DaoException;
import com.servease.model.RegisterUserDto;
import com.servease.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcUserDaoTest extends BaseDaoTest {

    private JdbcUserDao sut;

    @BeforeEach
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void getUserByUsername_given_null_throws_exception() {
        assertThrows(IllegalArgumentException.class, () -> sut.getUserByUsername(null));
    }

    @Test
    public void getUserByUsername_given_invalid_username_returns_null() {
        assertNull(sut.getUserByUsername("invalid_user"));
    }

    @Test
    public void getUserById_given_invalid_user_id_returns_null() {
        assertNull(sut.getUserById(-1));
    }

    @Test
    public void createUser_with_null_username_throws_exception() {
        RegisterUserDto user = new RegisterUserDto();
        user.setUsername(null);
        user.setPassword("password");
        user.setRole("ROLE_USER");

        assertThrows(DaoException.class, () -> sut.createUser(user));
    }

    @Test
    public void createUser_with_existing_username_throws_exception() {
        RegisterUserDto user = new RegisterUserDto();
        user.setUsername("user1");
        user.setPassword("password");
        user.setRole("ROLE_USER");

        assertThrows(DaoException.class, () -> sut.createUser(user));
    }

    @Test
    public void createUser_creates_user_successfully() {
        RegisterUserDto user = new RegisterUserDto();
        user.setUsername("new_user");
        user.setPassword("securePass123");
        user.setRole("ROLE_USER");

        User createdUser = sut.createUser(user);

        assertNotNull(createdUser);
        assertEquals("new_user", createdUser.getUsername());
        assertNotNull(sut.getUserByUsername("new_user"));
    }
}
