package service.tests;

import data.access.DataAccessExceptionHTTP;
import data.access.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.*;
import service.AppService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    //The initial setup and init are basically a copy of the StandardAPITest.java file

    private static UserData existingUserData;
    private static UserData newUserData;
    private static AppService service;
    private String existingAuth;
    private String newAuth;
    private static GameData existingGameDataSession;


    @AfterAll
    static void clearServices() throws DataAccessExceptionHTTP {
        service.clearApplication();
    }

    @BeforeAll
    public static void init() {
        service = new AppService(new MemoryDataAccess());
        existingUserData = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUserData = new UserData("brandNewUser", "newUserPassword", "nu@mail.com");
    }

    @BeforeEach
    public void setup() throws DataAccessExceptionHTTP {
        service.clearApplication();
        AuthData regResult = service.register(existingUserData);
        AuthData regResult_2 = service.register(newUserData);
        existingAuth = regResult.authToken();
        newAuth = regResult_2.authToken();
        existingGameDataSession = service.createGame(new GameRequest("ExistingGame", existingAuth));

    }

    @Test
    @Order(1)
    @DisplayName("Testing register service")
    public void testRegister() throws DataAccessExceptionHTTP {
        UserData userData = new UserData("NewUser", "newUserPassword", "nu@mail.com");
        AuthData result = service.register(userData);
        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("NewUser", result.username());
    }

    @Test
    @Order(2)
    @DisplayName("Testing duplicate registration")
    public void testRegisterDuplicateUser() throws DataAccessExceptionHTTP {
        UserData userData = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");

        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.register(userData);
        });

        assertEquals(403, exception.getStatusCode());
        assertEquals("Error: already taken", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Testing login service")
    public void testLogin() throws DataAccessExceptionHTTP {
        LoginRequest loginRequest = new LoginRequest("ExistingUser", "existingUserPassword");

        AuthData result = service.login(loginRequest);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertEquals("ExistingUser", result.username());
    }

    @Test
    @Order(4)
    @DisplayName("Testing login with incorrect password")
    public void testLoginWithIncorrectPassword() {
        LoginRequest loginRequest = new LoginRequest("ExistingUser", "wrongPassword");

        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.login(loginRequest);
        });

        assertEquals(401, exception.getStatusCode());
        assertEquals("Error: unauthorized", exception.getMessage() );
    }

    @Test
    @Order(5)
    @DisplayName("Testing login with non-existent user")
    public void testLoginWithNonExistentUser() {
        LoginRequest invalidLogin = new LoginRequest("FakeUser", "fakePassword");

        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.login(invalidLogin);
        });

        assertEquals(401, exception.getStatusCode());
        assertEquals("Error: unauthorized", exception.getMessage() );
    }
    @Test
    @Order(6)
    @DisplayName("Testing logout")
    public void testingLogout() throws DataAccessExceptionHTTP {
        //This function returns void, so if we don't see anything, we assume it worked
        assertDoesNotThrow(() -> {
            service.logout(existingAuth);
        });
    }
    @Test
    @Order(7)
    @DisplayName("Testing duplicate logout")
    public void testDuplicateLogout() throws DataAccessExceptionHTTP {
        service.logout(existingAuth);

        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.logout(existingAuth);
        });
        assertEquals(401, exception.getStatusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }
    @Test
    @Order(8)
    @DisplayName("Testing list of games service")
    public void testingListOfGames() throws DataAccessExceptionHTTP {
        List<GameListData> singleGame = service.listGames(existingAuth);
        assertEquals(1, singleGame.size());
        assertEquals("ExistingGame", singleGame.getFirst().gameName());
        assertNull(singleGame.getFirst().whiteUsername());
        assertNull(singleGame.getFirst(). blackUsername());
    }
    @Test
    @Order(9)
    @DisplayName("Testing list of games with unregistered authToken")
    public void testListOfGamesWithUnauthorizedAuthToken() throws DataAccessExceptionHTTP {
        String fakeTokenString = "fakeToken#$1235123";
        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.listGames(fakeTokenString);
        });
        assertEquals(401, exception.getStatusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }
    @Test
    @Order(10)
    @DisplayName("Testing join game service")
    public void testingJoinExistingGame() throws DataAccessExceptionHTTP {
        JoinGameRequest joinAsWhiteToExistingGame = new JoinGameRequest(existingAuth, "WHITE", existingGameDataSession.gameID());
        service.joinGame(joinAsWhiteToExistingGame);
        List<GameListData> singleGame = service.listGames(existingAuth);
        assertEquals(existingUserData.username(), singleGame.getFirst().whiteUsername());
    }
    @Test
    @Order(11)
    @DisplayName("Testing join game: Trying to join a taken color")
    public void testingJoinGameOnTakenTeamColor() throws DataAccessExceptionHTTP {
        JoinGameRequest joinAsWhiteToExistingGame = new JoinGameRequest(existingAuth, "WHITE", existingGameDataSession.gameID());

        service.joinGame(joinAsWhiteToExistingGame);

        JoinGameRequest joinAgainAsWhiteToExistingGame = new JoinGameRequest(newAuth, "WHITE", existingGameDataSession.gameID());
        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.joinGame(joinAgainAsWhiteToExistingGame);
        });
        assertEquals(403, exception.getStatusCode());
        assertEquals("Error: already taken", exception.getMessage());

    }
    @Test
    @Order(12)
    @DisplayName("Testing create game service")
    public void testingCreateGame() throws DataAccessExceptionHTTP {
        GameRequest newGame = new GameRequest("NewGame",existingAuth);
        GameData newGameDataObject = service.createGame(newGame);
        assertEquals("NewGame", newGameDataObject.gameName());
        assertNull(newGameDataObject.whiteUsername());
        assertNull(newGameDataObject.blackUsername());
    }

    @Test
    @Order(13)
    @DisplayName("Testing create game with unauthorized token")
    public void testingCreateGameWithUnauthorizedToken() throws DataAccessExceptionHTTP {
        String fakeTokenString = "fakeToken#$1235123";
        GameRequest newGame = new GameRequest("NewGame",fakeTokenString);
        DataAccessExceptionHTTP exception = assertThrows(DataAccessExceptionHTTP.class, () -> {
            service.createGame(newGame);
        });
        assertEquals(401, exception.getStatusCode());
        assertEquals("Error: unauthorized", exception.getMessage());
    }
}
