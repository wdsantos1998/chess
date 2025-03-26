package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class ServerFacade {
    private String ServerUrl;
    private static HttpClient HttpClient;
    private Gson Gson;

    public ServerFacade(String URLrequest ){
        this.ServerUrl = URLrequest;
        this.HttpClient = HttpClient.newHttpClient();
        this.Gson = new Gson();
    }

    public AuthData register(UserData newUser) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(Gson.toJson(newUser)))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = HttpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return Gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Error occurred: " + response.body());
        }
    }

    public AuthData login(UserData user) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerUrl + "/session"))
                .POST(HttpRequest.BodyPublishers.ofString(Gson.toJson(user)))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = HttpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return Gson.fromJson(response.body(), AuthData.class);
        } else {
            throw new Exception("Error occurred: " + response.body());
        }
    }

    public GameData createGame(String gameName, String authToken) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerUrl + "/game"))
                .POST(HttpRequest.BodyPublishers.ofString(Gson.toJson(Map.of("gameName", gameName))))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .build();

        HttpResponse<String> response = HttpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            Map<String, Object> jsonMap = Gson.fromJson(responseBody, Map.class);
            Double gameIdDouble = (Double) jsonMap.get("gameID");
            int gameID = gameIdDouble.intValue();
            return new GameData(null, null, gameName, gameID, new ChessGame());
        }
            else{
                throw new Exception("Error occurred: " + response.body());
            }
    }

//    public String joinGame(String playerColor, int gameID, String authToken) throws Exception {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(ServerUrl + "/game"))
//                .PUT(HttpRequest.BodyPublishers.ofString(Gson.toJson(Map.of("gameID", gameID, "playerColor", playerColor))))
//                .header("Content-Type", "application/json")
//                .header("Authorization", authToken)
//                .build();
//
//        HttpResponse<String> response = HttpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 200) {
//           return response.body();
//        }
//        else{
//            throw new Exception("Error occurred: " + response.body());
//        }
//    }

    public void clearData(AuthData authData) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ServerUrl + "/session"))
                .DELETE()
                .header("Content-Type", "application/json").header("Authorization", authData.authToken())
                .build();

        HttpResponse<String> response = HttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Error occurred: " + response.body());
        }
    }

}
