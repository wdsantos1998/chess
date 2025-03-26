package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.lang.classfile.instruction.ReturnInstruction;
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
            Map<String, Object> jsonMap = Gson.from(responseBody, Map.class);
            Double gameIdDouble = (Double) jsonMap.get("gameID");  // Gson parses numbers as Double
            int gameID = gameIdDouble.intValue();

        }
            else{
                throw new Exception("Error occurred: " + response.body());
            }
        }
    }
}
