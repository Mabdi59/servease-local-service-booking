package com.servease.dao;

import com.servease.model.RegisterUserDto;
import com.servease.model.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int userId);

    User getUserByUsername(String username);

    User createUser(RegisterUserDto user);

    boolean updateUser(User user);

    boolean deleteUser(int userId);

    boolean activateUser(int userId);

    boolean deactivateUser(int userId);
}
