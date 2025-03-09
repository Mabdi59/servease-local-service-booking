package com.servease.controller;

import com.servease.dao.UserDao;
import com.servease.exception.DaoException;
import com.servease.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userDao.getUsers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable int userId) {
        User user = userDao.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable int userId, @Valid @RequestBody User user) {
        user.setUserId(userId);  // Fixed method call from setId() to setUserId()
        boolean updated = userDao.updateUser(user);
        return updated ? ResponseEntity.ok("User updated successfully.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable int userId) {
        boolean deleted = userDao.deleteUser(userId);
        return deleted ? ResponseEntity.ok("User deleted successfully.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    @PatchMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> activateUser(@PathVariable int userId) {
        boolean activated = userDao.activateUser(userId);
        return activated ? ResponseEntity.ok("User activated successfully.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }

    @PatchMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deactivateUser(@PathVariable int userId) {
        boolean deactivated = userDao.deactivateUser(userId);
        return deactivated ? ResponseEntity.ok("User deactivated successfully.") :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    }
}
