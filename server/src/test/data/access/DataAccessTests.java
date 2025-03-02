package data.access;
import data.access.DataAccessException;
import model.Game;
import model.User;
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
    void testAddUser() throws DataAccessException {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        assertTrue(dataAccess.addUser(user));
        assertEquals(user, dataAccess.getUser(user));
    }

    @Test
    void testGetUser() throws DataAccessException {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        assertEquals(user, dataAccess.getUser(user));
    }

    @Test
    void testGetUserNotFound() {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        Executable executable = () -> dataAccess.getUser(user);
        assertThrows(DataAccessException.class, executable);
    }

    @Test
    void testCreateAuthData() throws DataAccessException {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        assertTrue(dataAccess.createAuthData(user));
        assertNotNull(dataAccess.getAuthData(user));
    }

    @Test
    void testDeleteAuthToken() throws DataAccessException {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        dataAccess.createAuthData(user);
        assertTrue(dataAccess.deleteAuthToken(user));
        Executable executable = () -> dataAccess.getAuthData(user);
        assertThrows(DataAccessException.class, executable);
    }

    @Test
    void testUpdateGame() throws DataAccessException {
        Game game = new Game("fakeWhite", "fakeBlack","fakeGame");
        dataAccess.createGame(game);
        game.setGameName("fakeGameUpdated");
        game.setWhiteUsername("fakeWhiteUpdated");
        game.setBlackUsername("fakeBlackUpdated");
        assertThrows(DataAccessException.class, () -> dataAccess.updateGame(game));
        assertEquals(game, dataAccess.getGameData(game.getGameId()));
    }

    @Test
    void testCreateGame() throws DataAccessException {
        Game game = new Game("fakeWhite", "fakeBlack", "fakeGame");
        assertTrue(dataAccess.createGame(game));
        assertEquals(game, dataAccess.listGames().get(0));
    }

    @Test
    void testListGames() throws DataAccessException {
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
    void testClear() throws DataAccessException {
        User user = new User("fakeUser", "password", "fake@gmail.com");
        dataAccess.addUser(user);
        dataAccess.createAuthData(user);
        Game game = new Game("fakeWhite", "fakeBlack", "fakeGame1");
        dataAccess.createGame(game);
        assertTrue(dataAccess.clear());
        assertThrows(DataAccessException.class, () -> dataAccess.getUser(user));
        assertThrows(DataAccessException.class, () -> dataAccess.getAuthData(user));
        assertThrows(DataAccessException.class,( )-> dataAccess.listGames().isEmpty());
    }
}