package data.access;
import data.access.DataAccessExceptionHTTP;
import model.Game;
import model.User;
import model.authData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTests {

    private MemoryDataAccess dataAccess;

    @BeforeEach
    void setUp() {
        dataAccess = new MemoryDataAccess();
    }

    @Test
    void testAddUser() throws DataAccessExceptionHTTP {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        assertTrue(dataAccess.addUser(user));
        assertEquals(user, dataAccess.getUser(user.getUsername()));
    }

    @Test
    void testGetUser() throws DataAccessExceptionHTTP {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        assertEquals(user, dataAccess.getUser(user.getUsername()));
    }

    @Test
    void testGetUserNotFound() {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        Executable executable = () -> dataAccess.getUser(user.getUsername());
        assertThrows(DataAccessExceptionHTTP.class, executable);
    }

    @Test
    void testCreateAuthData() throws DataAccessExceptionHTTP {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        authData token = dataAccess.createAuthData(user.getUsername());
        assertEquals(token,dataAccess.getAuthData(user.getUsername()));
    }

    @Test
    void testDeleteAuthToken() throws DataAccessExceptionHTTP {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        authData token = dataAccess.createAuthData(user.getUsername());
        assertTrue(dataAccess.deleteAuthToken(token.getAuthToken()));
        Executable executable = () -> dataAccess.getAuthData(user.getUsername());
        assertThrows(DataAccessExceptionHTTP.class, executable);
    }

    @Test
    void testUpdateGame() throws DataAccessExceptionHTTP {
        Game game = new Game("fakeWhite", "fakeBlack","fakeGame");
        dataAccess.createGame(game);
        game.setGameName("fakeGameUpdated");
        game.setWhiteUsername("fakeWhiteUpdated");
        game.setBlackUsername("fakeBlackUpdated");
        assertThrows(DataAccessExceptionHTTP.class, () -> dataAccess.updateGame(game));
        assertEquals(game, dataAccess.getGameData(game.getGameId()));
    }

    @Test
    void testCreateGame() throws DataAccessExceptionHTTP {
        Game game = new Game("fakeWhite", "fakeBlack", "fakeGame");
        Game gameCreated = dataAccess.createGame(game);
        assertEquals(gameCreated, dataAccess.listGames().get(0));
    }

    @Test
    void testListGames() throws DataAccessExceptionHTTP {
        Game game1 = new Game("fakeWhite", "fakeBlack", "fakeGame1");
        Game game2 = new Game("fakeWhite", "fakeBlack", "fakeGame2");
        dataAccess.createGame(game1);
        dataAccess.createGame(game2);
        List<Game> games = dataAccess.listGames();
        assertEquals(2, games.size());
        assertTrue(games.contains(game1));
        assertTrue(games.contains(game2));
    }

    @Test
    void testClear() throws DataAccessExceptionHTTP {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        dataAccess.createAuthData(user.getUsername());
        Game game = new Game("fakeWhite", "fakeBlack", "fakeGame1");
        dataAccess.createGame(game);
        assertTrue(dataAccess.clear());
        assertThrows(DataAccessExceptionHTTP.class, () -> dataAccess.getUser(user.getUsername()));
        assertThrows(DataAccessExceptionHTTP.class, () -> dataAccess.getAuthData(user.getUsername()));
        assertThrows(DataAccessExceptionHTTP.class,( )-> dataAccess.listGames().isEmpty());
    }
}