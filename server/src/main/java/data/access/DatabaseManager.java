package data.access;

import data.access.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    private static final File SQL_FOLDER;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
                String relativePath = "data/database_files";
                SQL_FOLDER = new File(relativePath).getAbsoluteFile();
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessExceptionHTTP {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            //Create remaining tables
            createDataBaseTables();
        } catch (SQLException e) {
            throw new DataAccessExceptionHTTP(500,e.getMessage());
        }
    }

    /**
     * Creates the database tables.
     * This function assumes that an initial connection with the database was already established. Therefore, it must be executed after createDatabase.
     */
    static void createDataBaseTables() throws DataAccessExceptionHTTP {
        File[] files = SQL_FOLDER.listFiles((dir, name) -> name.toLowerCase().endsWith(".sql"));

        if (files == null || files.length == 0) {
            System.out.println("No SQL files found in folder: " + SQL_FOLDER);
            return;
        }

        try (Connection conn = getConnection()) {
            System.out.println("Connected to database: " + DATABASE_NAME);

            for (File file : files) {
                System.out.println("Executing: " + file.getName());
                String sqlScript = new String(Files.readAllBytes(file.toPath()));
                try (var preparedStatement = conn.prepareStatement(sqlScript)) {
                    preparedStatement.executeUpdate();
                }
            }

            System.out.println("All SQL scripts executed successfully!");

        } catch (SQLException | IOException e) {
            throw new DataAccessExceptionHTTP(500,"Error executing SQL files: " + e.getMessage());
        }
    }


    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessExceptionHTTP {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessExceptionHTTP(500,e.getMessage());
        }
    }
}
