package client;

import data.access.DataAccessExceptionHTTP;
import data.access.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import service.AppService;
import ui.ExceptionResponse;
import ui.ServerFacade;

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
        existingGameDataSession = facade.createGame("ExistingGame", existingAuth);
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
}
