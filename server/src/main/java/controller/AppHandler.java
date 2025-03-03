package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.LoginRequest;
import model.User;
import model.authData;
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
        delete("/db", (req, res) -> {
            try {
                AppHandler handler = new AppHandler(appService);
                handler.appService.clearApplication();
                res.status(200);
                res.type("application/json");
                return gson.toJson(new Object());
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(e.getMessage());
            }
        });
    }
}