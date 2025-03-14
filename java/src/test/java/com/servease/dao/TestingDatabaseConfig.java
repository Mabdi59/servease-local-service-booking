package com.servease.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

@Configuration
public class TestingDatabaseConfig {

    private static final Logger logger = Logger.getLogger(TestingDatabaseConfig.class.getName());

    private static final String DB_HOST = Objects.requireNonNullElse(System.getenv("DB_HOST"), "localhost");
    private static final String DB_PORT = Objects.requireNonNullElse(System.getenv("DB_PORT"), "5432");
    private static final String DB_NAME = Objects.requireNonNullElse(System.getenv("DB_NAME"), "final_capstone_test");
    private static final String DB_USERNAME = Objects.requireNonNullElse(System.getenv("DB_USERNAME"), "postgres");
    private static final String DB_PASSWORD = Objects.requireNonNullElse(System.getenv("DB_PASSWORD"), "postgres1");

    private SingleConnectionDataSource adminDataSource;
    private JdbcTemplate adminJdbcTemplate;
    private DataSource testDataSource;

    @PostConstruct
    public void setup() {
        if (System.getenv("DB_HOST") == null) {
            logger.info("Setting up temporary test database.");
            adminDataSource = new SingleConnectionDataSource();
            adminDataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
            adminDataSource.setUsername("postgres");
            adminDataSource.setPassword("postgres1");
            adminJdbcTemplate = new JdbcTemplate(adminDataSource);
            adminJdbcTemplate.update("DROP DATABASE IF EXISTS \"" + DB_NAME + "\";");
            adminJdbcTemplate.update("CREATE DATABASE \"" + DB_NAME + "\";");
        }
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        if (testDataSource != null) return testDataSource;

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(String.format("jdbc:postgresql://%s:%s/%s", DB_HOST, DB_PORT, DB_NAME));
        dataSource.setUsername(DB_USERNAME);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setAutoCommit(false);

        try {
            logger.info("Executing schema.sql for test database...");
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new FileSystemResource("database/schema.sql"));
            logger.info("Executing test-data.sql...");
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));
        } catch (SQLException e) {
            logger.severe("Error setting up test database: " + e.getMessage());
            throw e;
        }

        testDataSource = dataSource;
        return testDataSource;
    }

    @PreDestroy
    public void cleanup() throws SQLException {
        if (adminDataSource != null) {
            logger.info("Dropping test database: " + DB_NAME);
            adminJdbcTemplate.update("DROP DATABASE \"" + DB_NAME + "\";");
            adminDataSource.getConnection().close();
            adminDataSource.destroy();
        }
    }
}
