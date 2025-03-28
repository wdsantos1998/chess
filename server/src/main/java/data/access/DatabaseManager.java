package data.access;
import java.io.File;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

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
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

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

    static void createDataBaseTables() throws DataAccessExceptionHTTP {
        String userTable = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, email VARCHAR(100));";
        String authTokenTable = "CREATE TABLE IF NOT EXISTS auth_token (id INT AUTO_INCREMENT PRIMARY KEY, authToken VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci, username VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci);";
        String gameDataTable = "CREATE TABLE IF NOT EXISTS game_data (id INT AUTO_INCREMENT PRIMARY KEY, gameID INT UNIQUE, whiteUsername VARCHAR(100) NULL, blackUsername VARCHAR(100) NULL, gameName VARCHAR(100), game JSON);";

        String[] sqlTables = {userTable,authTokenTable,gameDataTable};


        try (Connection conn = getConnection()) {
            System.out.println("Connected to database: " + DATABASE_NAME);
            for (String file : sqlTables) {
                try (var preparedStatement = conn.prepareStatement(file)) {
                    preparedStatement.executeUpdate();
                }
                catch (SQLException e){
                    File fileData = new File(file);
                    throw new DataAccessExceptionHTTP(500,"Error " + fileData.getName()+": "+e.getMessage());
                }
            }

            System.out.println("All SQL scripts executed successfully!");

        } catch (SQLException e) {
            throw new DataAccessExceptionHTTP(500,"Error executing SQL files: " + e.getMessage());
        }
    }


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
