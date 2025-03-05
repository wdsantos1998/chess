package service.tests;

import data.access.DataAccess;
import data.access.DataAccessExceptionHTTP;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.AppService;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    private AppService appService;
    private DataAccess mockDataAccess;

    @BeforeEach
    public void setUp() {
        mockDataAccess = Mockito.mock(DataAccess.class);
        appService = new AppService(mockDataAccess);
    }

    @Test
    public void testRegister() throws DataAccessExceptionHTTP {
        User user = new User("username", "password", "email@example.com");
        authData result = appService.register(user);
        assertNotNull(result);
        assertEquals("authToken", result.getAuthToken());
        assertEquals("username", result.getUsername());
    }

    @Test
    public void testLogin() throws DataAccessExceptionHTTP {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User user = new User("username", "password", "email@example.com");

        authData result = appService.login(loginRequest);

        assertNotNull(result);
        assertEquals("authToken", result.getAuthToken());
        assertEquals("username", result.getUsername());
    }

}