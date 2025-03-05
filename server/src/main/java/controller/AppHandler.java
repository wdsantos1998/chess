package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import data.access.DataAccessExceptionHTTP;
import model.*;
import service.AppService;

import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class AppHandler {
    private static final Gson gson = new Gson();
    private final AppService appService;

    public AppHandler(AppService appService) {
        this.appService = appService;
    }

    public void startRoutes() {
        post("/user", (req, res) -> {
            try {
                UserData userData = gson.fromJson(req.body(), UserData.class);
                try {
                    AuthData response = appService.register(userData);
                    res.status(200);
                    return gson.toJson(response);
                } catch (DataAccessExceptionHTTP e) {
                    res.status(e.getStatusCode());
                    return gson.toJson(Map.of(
                            "message", e.getMessage()
                    ));
                }
            } catch (JsonSyntaxException e) {
                res.status(400);
                return gson.toJson(Map.of(
                        "error", "Invalid JSON",
                        "message", "Malformed request body"
                ));
            }
        });
        post("/session", (req, res) -> {
            LoginRequest loginRequestData = gson.fromJson(req.body(), LoginRequest.class);
            try {
                AuthData response = appService.login(loginRequestData);
                res.status(200);
                return gson.toJson(response);
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        post("/game", (req, res) -> {
            try {
            String gameName = gson.fromJson(req.body(), JsonObject.class).get("gameName").getAsString();
            String authToken = req.headers("authorization");

            if(authToken == null || gameName == null){
                throw new DataAccessExceptionHTTP(400,"Error: bad request.");
            }
                GameRequest gameRequest = new GameRequest(gameName, authToken);
                GameData response = appService.createGame(gameRequest);
                res.status(200);
                return gson.toJson(Map.of("gameID", response.gameID()));
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        put("/game", (req, res) -> {
            try {
                JsonObject requestJsonObject = gson.fromJson(req.body(), JsonObject.class);
                String authToken = req.headers("authorization");
                if (requestJsonObject == null || !requestJsonObject.has("gameID") || !requestJsonObject.has("playerColor") || authToken == null) {
                    throw new DataAccessExceptionHTTP(400, "Error: bad request");
                }
                int gameID = requestJsonObject.get("gameID").getAsInt();
                String playerColor = requestJsonObject.get("playerColor").getAsString();

                JoinGameRequest joinGameRequestData = new JoinGameRequest(authToken,playerColor, gameID );
                appService.joinGame(joinGameRequestData);
                res.status(200);
                res.type("application/json");
                return gson.toJson(new Object());
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        get("/game", (req, res) -> {
            try {
            String authToken = req.headers("authorization");
            if(authToken == null){
                throw new DataAccessExceptionHTTP(400,"Error: bad request" );
            }
                List<GameListData> response =  appService.listGames(authToken);
                res.status(200);
                return gson.toJson(Map.of("games", response));
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        delete("/session", (req, res) -> {
            try {
                String authToken = req.headers("authorization");
                if(authToken == null){
                    throw new DataAccessExceptionHTTP(400, "Error: bad request");
                }
                appService.logout(authToken);
                res.status(200);
                res.type("application/json");
                return gson.toJson(new Object());
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        delete("/db", (req, res) -> {
            try {
                try {
                    appService.clearApplication();
                    res.status(200);
                    res.type("application/json");
                    return gson.toJson(new Object());
                } catch (DataAccessExceptionHTTP e) {
                    res.status(e.getStatusCode());
                    return gson.toJson(e.getMessage());
                }
            }
            catch (Exception e){
                res.status(500);
                return gson.toJson(e.getMessage());
            }
        });
    }
}