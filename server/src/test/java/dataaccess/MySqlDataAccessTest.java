package dataaccess;

import data.access.DataAccessExceptionHTTP;
import data.access.MySqlDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

public class MySqlDataAccessTest {
    private static MySqlDataAccess database;
    private static UserData existingUserData;
    private static UserData newUserData;
    private static GameData newGame;


    @AfterAll
    static void clearDatabase() throws DataAccessExceptionHTTP {
        database.clear();
    }

    @BeforeAll
    public static void init() {
        existingUserData = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUserData = new UserData("brandNewUser", "newUserPassword", "nu@mail.com");
        newGame = new GameData(null, null, "FakeGame", 12345);
        database = new MySqlDataAccess();
    }

    @BeforeEach
    public void setup() throws DataAccessExceptionHTTP {
        clearDatabase();
    }

    @Test
    @Order(1)
    @DisplayName("Adding user")
    public void addUser() throws DataAccessExceptionHTTP {
        database.addUser(existingUserData);
        UserData userInDatabase = database.getUser(existingUserData.username());
        Assertions.assertNotNull(userInDatabase, "Database returned an empty record");
        Assertions.assertEquals(existingUserData.username(), userInDatabase.username(),
                "Username do not match");
        Assertions.assertEquals(existingUserData.email(), userInDatabase.email(),
                "Email do not match");
        Assertions.assertTrue(database.verifyPassword(existingUserData.password(), userInDatabase.password()),
                "Password do not match");
    }

    @Test
    @Order(2)
    @DisplayName("Adding user and comparing with uppercase user version.")
    public void addUserAndCompare() throws DataAccessExceptionHTTP {
        database.addUser(existingUserData);
        String upperCaseUsername = existingUserData.username().toUpperCase();
        UserData userInDatabase = database.getUser(upperCaseUsername);
        Assertions.assertNotNull(userInDatabase, "Database did not find a record. It means that your username comparison is case sensitive");
        Assertions.assertFalse(database.verifyPassword(existingUserData.password().toUpperCase(), userInDatabase.password()),
                "Passwords shouldn't match");
    }
}
