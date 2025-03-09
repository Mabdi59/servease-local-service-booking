package com.servease.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestingDatabaseConfig.class)
public abstract class BaseDaoTest {

    private static final Logger logger = Logger.getLogger(BaseDaoTest.class.getName());

    @Autowired
    protected DataSource dataSource;

    @AfterEach
    public void rollback() {
        try (Connection conn = dataSource.getConnection()) {
            conn.rollback();
            logger.info("Database rollback successful.");
        } catch (SQLException e) {
            logger.severe("Error during rollback: " + e.getMessage());
        }
    }
}
