package service.tests;

import data.access.DataAccess;
import data.access.DataAccessExceptionHTTP;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.AppService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTests {

    private AppService appService;
    private DataAccess mockDataAccess;

    @BeforeEach
    public void setUp() {
        mockDataAccess = Mockito.mock(DataAccess.class);
        appService = new AppService(mockDataAccess);
    }




}