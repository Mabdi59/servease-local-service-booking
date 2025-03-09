package com.servease.dao;

import com.servease.exception.DaoException;
import com.servease.model.RegisterUserDto;
import com.servease.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, email, phone, password_hash, role, activated FROM users";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                users.add(mapRowToUser(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return users;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, full_name, email, phone, password_hash, role, activated FROM users WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                return mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, full_name, email, phone, password_hash, role, activated FROM users WHERE LOWER(username) = LOWER(TRIM(?))";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
            if (results.next()) {
                return mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
        return null;
    }

    @Override
    public User createUser(RegisterUserDto user) {
        String sql = "INSERT INTO users (username, full_name, email, phone, password_hash, role, activated) " +
                "VALUES (?, ?, ?, ?, ?, ?, TRUE) RETURNING user_id";
        String passwordHash = new BCryptPasswordEncoder().encode(user.getPassword());

        try {
            int newUserId = jdbcTemplate.queryForObject(sql, int.class,
                    user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), passwordHash, user.getRole());

            return getUserById(newUserId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("User creation failed: Duplicate entry or missing required field.", e);
        }
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, phone = ?, password_hash = ?, role = ?, activated = ? WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, user.getUsername(), user.getFullName(), user.getEmail(),
                    user.getPhone(), new BCryptPasswordEncoder().encode(user.getPasswordHash()), user.getRole(),
                    user.isActivated(), user.getUserId());
            return rowsAffected > 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("User update failed: Duplicate entry or constraint violation.", e);
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId);
            return rowsAffected > 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("User deletion failed: User may have dependencies in other tables.", e);
        }
    }

    @Override
    public boolean deactivateUser(int userId) {
        String sql = "UPDATE users SET activated = FALSE WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId);
            return rowsAffected > 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
    }

    @Override
    public boolean activateUser(int userId) {
        String sql = "UPDATE users SET activated = TRUE WHERE user_id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, userId);
            return rowsAffected > 0;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database", e);
        }
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setAuthorities(Objects.requireNonNull(rs.getString("role")));
        user.setActivated(rs.getBoolean("activated"));
        return user;
    }
}
