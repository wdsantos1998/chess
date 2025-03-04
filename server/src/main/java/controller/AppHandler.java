package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.*;
import service.AppService;

import java.util.Map;

import static spark.Spark.delete;
import static spark.Spark.post;

public class AppHandler {
    private static final Gson gson = new Gson();
    private final AppService appService;

    public AppHandler(AppService appService) {
        this.appService = appService;
    }

    public void startRoutes() {
        post("/user", (req, res) -> {
            try {
                User userData = gson.fromJson(req.body(), User.class);
                try {
                    authData response = appService.register(userData);
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
                authData response = appService.login(loginRequestData);
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
            String gameName = gson.fromJson(req.body(), JsonObject.class).get("gameName").getAsString();
            String authToken = req.headers("authorization");
            GameRequest gameRequest = new GameRequest(gameName, authToken);
            try {
                Game response = appService.createGame(gameRequest);
                res.status(200);
                System.out.println("Game id in create game "+response.getGameId());
                return gson.toJson(Map.of("gameID", response.getGameId()));
            } catch (DataAccessExceptionHTTP e) {
                res.status(e.getStatusCode());
                return gson.toJson(Map.of(
                        "message", e.getMessage()
                ));
            }
        });
        delete("/session", (req, res) -> {
            System.out.println("Calling logout API");
            try {
                String authToken = req.headers("authorization");
                if(authToken == null){
                    throw new DataAccessExceptionHTTP(400, "Error: bad request");
                }
                appService.logout(req.headers("authorization"));
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