package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.User;
import model.authData;
import service.AppService;

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
                AppHandler handler = new AppHandler(appService);
                User userData = gson.fromJson(String.valueOf(req.body()), User.class);
                authData response =  handler.appService.register(userData);
                res.status(200);
                return gson.toJson(response);
            }
            catch (Exception e){
                res.status(500);
                return gson.toJson(e.getMessage());
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