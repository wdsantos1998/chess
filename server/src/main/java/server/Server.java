package server;

import controller.AppHandler;
import data.access.MemoryDataAccess;
import server.websocket.WebSocketHandler;
import service.AppService;
import spark.*;

import javax.websocket.server.ServerContainer;

import static spark.Spark.exception;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        AppService appService = new AppService(new MemoryDataAccess());
        AppHandler userHandler = new AppHandler(appService);
        WebSocketHandler webSocketHandler = new WebSocketHandler();
        Spark.webSocket("/connect", webSocketHandler);

        userHandler.startRoutes();



        // Handle unhandled exceptions
        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body("Internal Server Error: " + e.getMessage());
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
