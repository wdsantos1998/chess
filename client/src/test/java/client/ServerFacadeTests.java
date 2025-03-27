package client;

import data.access.DataAccessExceptionHTTP;
import data.access.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.AppService;
import ui.ExceptionResponse;
import ui.ServerFacade;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static UserData existingUserData;
    private static UserData newUserData;
    private static AppService service;
    private String existingAuth;
    private String newAuth;
    private static GameData existingGameDataSession;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" +port);
        //Setting up users
        existingUserData = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUserData = new UserData("brandNewUser", "newUserPassword", "nu@mail.com");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setUp() throws Exception {
        facade.clearData();
        AuthData regResult = facade.register(existingUserData);
        existingAuth = regResult.authToken();
    }
    @Test
    @Order(1)
    @DisplayName("Testing clearDB")
    public void testClearDB() throws ExceptionResponse {
        AuthData newUserAuth = facade.register(newUserData);
        Assertions.assertNotNull(newUserAuth);
        Assertions.assertEquals(newUserData.username(), newUserAuth.username());
        Assertions.assertNotNull(newUserAuth.authToken());
        facade.clearData();
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.login(new LoginRequest(newUserData.username(), newUserData.password()));
        });
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: unauthorized"), exception.getMessage());
    }

    @Test
    @Order(2)
    @DisplayName("Testing register")
    public void testRegister() throws ExceptionResponse {
       AuthData newUserAuth = facade.register(newUserData);
       Assertions.assertNotNull(newUserAuth);
       Assertions.assertEquals(newUserData.username(), newUserAuth.username());
       Assertions.assertNotNull(newUserAuth.authToken());
    }
    @Test
    @Order(3)
    @DisplayName("Testing register with already existing user")
    public void testRegisterExistingUser() throws ExceptionResponse {
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.register(existingUserData);
        });
        Assertions.assertEquals(403, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: already taken"), exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("Testing login")
    public void testingLogin() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered
        AuthData result = facade.login(new LoginRequest(existingUserData.username(), existingUserData.password()));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingUserData.username(), result.username());
        Assertions.assertNotNull(result.authToken());
    }
    @Test
    @Order(5)
    @DisplayName("Testing login with incorrect password")
    public void loginWithIncorrectPassword() throws ExceptionResponse {
        String fakePassword = "123asdajbfabfjabskanlfas";
        String realUsername = existingUserData.username();

        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.login(new LoginRequest(realUsername, fakePassword));
        });
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: unauthorized"), exception.getMessage());
    }
    @Test
    @Order(6)
    @DisplayName("Testing logout")
    public void logoutUser() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered
        facade.logout(existingAuth);
        //No error should be thrown
    }
    @Test
    @Order(7)
    @DisplayName("Trying to logout with invalid token")
    public void logoutWithInvalidToken() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered
        //However, I will use a fake authToken
        String fakeAuthToken = "fakeAuthToken1234";
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.logout(fakeAuthToken);
        });
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: unauthorized"), exception.getMessage());
    }

    @Test
    @Order(8)
    @DisplayName("Creating a game")
    public void creatingGame() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        GameData gameData = facade.createGame("ExistingGame", existingAuth);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("ExistingGame", gameData.gameName());
        Assertions.assertTrue(gameData.gameID() > 0);
    }
    @Test
    @Order(9)
    @DisplayName("Trying to create a game with invalid authToken")
    public void creatingGameWithInvalidAuthToken() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        //However I will create a fake authToken
        String fakeAuthToken = "fakeAuthToken1234";
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.createGame("ExistingGame", fakeAuthToken);
        });
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: unauthorized"), exception.getMessage());
    }
    @Test
    @Order(10)
    @DisplayName("Joining game")
    public void joiningGame() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        GameData gameData = facade.createGame("ExistingGame", existingAuth);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("ExistingGame", gameData.gameName());
        Assertions.assertTrue(gameData.gameID() > 0);
        //Now I will try to join the game
        JoinGameRequest joinGameRequest = new JoinGameRequest(existingAuth, "WHITE", gameData.gameID());
        facade.joinGame(joinGameRequest);
    }

    @Test
    @Order(11)
    @DisplayName("Joining into an already taken color")
    public void joiningGameIntoAnAlreadyTakenColor() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        GameData gameData = facade.createGame("ExistingGame", existingAuth);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("ExistingGame", gameData.gameName());
        Assertions.assertTrue(gameData.gameID() > 0);
        JoinGameRequest joinGameRequest = new JoinGameRequest(existingAuth, "WHITE", gameData.gameID());
        facade.joinGame(joinGameRequest);
        //Now I will try to join the game with an invalid gameID
        AuthData newUserAuth = facade.register(newUserData);
        JoinGameRequest newJoinGameRequest = new JoinGameRequest(newUserAuth.authToken(), "WHITE", gameData.gameID());
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.joinGame(newJoinGameRequest);
        });
        Assertions.assertEquals(403, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: already taken"), exception.getMessage());
    }
    @Test
    @Order(12)
    @DisplayName("Listing games")
    public void listingGames() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        GameData gameData = facade.createGame("ExistingGame", existingAuth);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("ExistingGame", gameData.gameName());
        Assertions.assertTrue(gameData.gameID() > 0);
        JoinGameRequest joinGameRequest = new JoinGameRequest(existingAuth, "WHITE", gameData.gameID());
        facade.joinGame(joinGameRequest);
        //Now I will try to list the games
        List<GameListData> games = facade.listGames(existingAuth);
        Assertions.assertNotNull(games);
        System.out.println(games);
        Assertions.assertFalse(games.isEmpty());
        Assertions.assertEquals(1, games.size());
        Assertions.assertEquals("ExistingGame", games.getFirst().gameName());
        Assertions.assertEquals(gameData.gameID(), games.getFirst().gameID());
    }
    @Test
    @Order(13)
    @DisplayName("Listing games with invalid authToken")
    public void listingGamesWithInvalidToken() throws ExceptionResponse {
        //My test will consider the existingUserData as the user that is already registered and has an authToken
        GameData gameData = facade.createGame("ExistingGame", existingAuth);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("ExistingGame", gameData.gameName());
        Assertions.assertTrue(gameData.gameID() > 0);
        JoinGameRequest joinGameRequest = new JoinGameRequest(existingAuth, "WHITE", gameData.gameID());
        facade.joinGame(joinGameRequest);
        //Now I will try to list the games with an invalid token
        String fakeAuthToken = "fakeAuthToken1234";
        ExceptionResponse exception = assertThrows(ExceptionResponse.class, () -> {
            facade.listGames(fakeAuthToken);
        });
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertTrue(exception.getMessage().contains("Error: unauthorized"), exception.getMessage());
    }

}
