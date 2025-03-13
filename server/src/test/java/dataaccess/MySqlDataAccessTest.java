package dataaccess;

import data.access.DataAccessExceptionHTTP;
import data.access.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.GameListData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

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
        newGame = new GameData(null, null, "FakeGame");
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

    @Test
    @Order(3)
    @DisplayName("Get user")
    public void getUser() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        UserData userInDatabase = database.getUser(newUserData.username());
        Assertions.assertNotNull(userInDatabase, "Database did not find a record.");
        Assertions.assertTrue(database.verifyPassword(newUserData.password(), userInDatabase.password()),
                "Passwords don't match");
    }

    @Test
    @Order(4)
    @DisplayName("Get user with typo in username")
    public void getUsernameWithTypo() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        UserData userInDatabase = database.getUser("brandNewUserError");
        Assertions.assertNull(userInDatabase, "Database did find a record.");
    }

    @Test
    @Order(5)
    @DisplayName("Verify password")
    public void verifyPasswordCorrect() {
        String randomPassword = "Password123";
        String hashedPassword = database.hashPassword(randomPassword);

        Assertions.assertTrue(database.verifyPassword(randomPassword, hashedPassword),
                "Passwords don't match.");
    }

    @Test
    @Order(6)
    @DisplayName("Verify password with incorrect credentials")
    public void verifyPasswordIncorrect() {
        String rawPassword = "Password123";
        String wrongPassword = "WrongPassword456";
        String hashedPassword = database.hashPassword(rawPassword);

        Assertions.assertFalse(database.verifyPassword(wrongPassword, hashedPassword),
                "Verification should fail for incorrect credentials.");
    }

    @Test
    @Order(7)
    @DisplayName("Create and return auth-token")
    public void createAndGetAuthToken() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        AuthData authData = database.createAuthData(newUserData.username());

        Assertions.assertNotNull(authData, "AuthData should not be null.");
        Assertions.assertNotNull(authData.authToken(), "Auth token should not be null.");

        AuthData retrievedAuthData = database.getAuthData(authData.authToken());
        Assertions.assertNotNull(retrievedAuthData, "AuthData is null.");
        Assertions.assertEquals(authData.authToken(), retrievedAuthData.authToken(),
                "Auth tokens don't match.");
        Assertions.assertEquals(authData.username(), retrievedAuthData.username(),
                "Usernames don't match.");
    }

    @Test
    @Order(8)
    @DisplayName("Trying to return non-existing auth-token")
    public void returnNonExistingAuthToken() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        AuthData authData = database.createAuthData(newUserData.username());

        Assertions.assertNotNull(authData, "AuthData should not be null.");
        Assertions.assertNotNull(authData.authToken(), "Auth token should not be null.");

        String fakeAuthToken = "ajsbfjabjadvfjavdjabgjhabjfd%1231231";
        AuthData retrievedAuthData = database.getAuthData(fakeAuthToken);
        Assertions.assertNull(retrievedAuthData, "AuthData should be null null.");
    }

    @Test
    @Order(9)
    @DisplayName("Validate auth token - Valid")
    public void validateAuthToken() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        AuthData authData = database.createAuthData(newUserData.username());

        Assertions.assertTrue(database.isValidAuthToken(authData.authToken()),
                "Auth token should be valid.");
        Assertions.assertFalse(database.isValidAuthToken("invalidToken"),
                "Invalid token should not be recognized.");
    }

    @Test
    @Order(10)
    @DisplayName("Validate auth token - Invalid")
    public void validateAuthTokenWithInvalidToken() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        database.createAuthData(newUserData.username());
        Assertions.assertFalse(database.isValidAuthToken("invalidToken"),
                "Invalid token should not be recognized.");
    }

    @Test
    @Order(11)
    @DisplayName("Delete auth token")
    public void deleteAuthToken() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        AuthData authData = database.createAuthData(newUserData.username());

        Assertions.assertTrue(database.isValidAuthToken(authData.authToken()), "Auth token must exist.");
        database.deleteAuthToken(authData.authToken());
        Assertions.assertFalse(database.isValidAuthToken(authData.authToken()), "Auth token shouldn't exist after deletion.");
    }

    @Test
    @Order(12)
    @DisplayName("Create and return game data")
    public void createAndReturnGameData() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        GameData createdGame = database.createGame(newGame);
        Assertions.assertNotNull(createdGame, "GameData should not be null.");
        GameData retrievedGame = database.getGameData(createdGame.gameID());
        Assertions.assertNotNull(retrievedGame, "game data is null.");
        Assertions.assertEquals(createdGame.gameID(), retrievedGame.gameID(),
                "GameIDs don't match.");
        Assertions.assertEquals(createdGame.gameName(), retrievedGame.gameName(),
                "Game names do not match.");
    }

    @Test
    @Order(13)
    @DisplayName("Update game data")
    public void updateGameData() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        GameData createdGame = database.createGame(newGame);

        Assertions.assertNotNull(createdGame, "GameData should not be null.");

        GameData updatedGame = new GameData(existingUserData.username(), newUserData.username(), "UpdatedGame", createdGame.gameID());
        database.updateGame(updatedGame);

        GameData retrievedGame = database.getGameData(createdGame.gameID());
        Assertions.assertNotNull(retrievedGame, "Game data not found.");
        Assertions.assertEquals(existingUserData.username(), retrievedGame.whiteUsername(),
                "White username don't match.");
        Assertions.assertEquals(newUserData.username(), retrievedGame.blackUsername(),
                "Black username don't match.");
    }

    @Test
    @Order(14)
    @DisplayName("Trying to update non-existing game")
    public void updateNonExistingGame() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        int fakeGameID = 56789;
        GameData createdGame = database.createGame(newGame);
        Assertions.assertNotNull(createdGame, "GameData should not be null.");
        GameData updatedGame = new GameData(existingUserData.username(), newUserData.username(), "UpdatedGame",fakeGameID,createdGame.game());
        database.updateGame(updatedGame);
        Assertions.assertNull(database.getGameData(fakeGameID), "GameData should be null.");
    }

    @Test
    @Order(15)
    @DisplayName("List all games")
    public void listGames() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        database.createGame(newGame);
        List<GameListData> gameList = database.listGames();
        Assertions.assertNotNull(gameList, "Game list shouldn't be null.");
        Assertions.assertFalse(gameList.isEmpty(), "Game list shouldn't be empty.");
    }
    @Test
    @Order(16)
    @DisplayName("Return empty list of games")
    public void returnEmptyList() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        database.createGame(newGame);
        List<GameListData> gameList = database.listGames();
        Assertions.assertNotNull(gameList, "Game list shouldn't be null.");
        Assertions.assertFalse(gameList.isEmpty(), "Game list shouldn't be empty.");
        database.clear();
        database.addUser(newUserData);
        List<GameListData> newGameList = database.listGames();
        Assertions.assertNotNull(newGameList, "Game list should be null.");
        Assertions.assertTrue(newGameList.isEmpty(), "Game list should be empty.");
    }

    @Test
    @Order(17)
    @DisplayName("Clear database")
    public void clearDatabaseTest() throws DataAccessExceptionHTTP {
        database.addUser(newUserData);
        database.createGame(newGame);

        database.clear();
        UserData user = database.getUser(newUserData.username());
        List<GameListData> games = database.listGames();

        Assertions.assertNull(user, "We shouldn't have any user after clearing database.");
        Assertions.assertTrue(games.isEmpty(), "We shouldn't have any game after clearing database.");
    }

}
