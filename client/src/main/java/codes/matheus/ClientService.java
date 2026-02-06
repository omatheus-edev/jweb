package codes.matheus;

import codes.matheus.authorization.Authorization;
import codes.matheus.entity.User;
import codes.matheus.entity.Username;
import codes.matheus.util.Json;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public final class ClientService {
    private final @NotNull String url = "http://localhost:8080/api/users";
    private final @NotNull String loginUrl = "http://localhost:8080/api/login";
    private final @NotNull HttpClient client = HttpClient.newHttpClient();
    private final @NotNull Authorization auth = new Authorization();

    public ClientService() {
    }

    public @NotNull Authorization getAuth() {
        return auth;
    }

    public boolean login(@NotNull Username username, @NotNull String password) throws IOException, InterruptedException {
        @NotNull JsonObject body = new JsonObject();
        body.addProperty("username", username.getName());
        body.addProperty("password", password);

        @NotNull HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(loginUrl))
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .header("Content-Type", "application/json")
                .header("Accept" , "application/json")
                .build();


        @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            System.out.println("login failed: " + response.statusCode() + " - " + response.body());
            return false;
        }

        @NotNull JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("token")) {
            System.out.println("response without token");
            return false;
        }

        auth.setToken(json.get("token").getAsString());
        System.out.println("Login realized with success");
        return true;
    }

    public @NotNull User create(long id, @NotNull String name, @NotNull String email) throws IOException, InterruptedException {
        @NotNull String body = Json.serialize(new User(id, new Username(name), email)).toString();
        @NotNull HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            throw new IOException("Failed on create user: " + response.statusCode() + " - " + response.body());
        }


        return Json.deserializeObject(response.body());
    }

    public @NotNull List<User> get() throws IOException, InterruptedException {
        @NotNull HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error on list users: " + response.statusCode());
        }

        return Json.deserializeArray(response.body());
    }

    public @NotNull Optional<User> get(long id) throws IOException, InterruptedException {
        @NotNull HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + id))
                .GET()
                .header("Accept", "application/json")
                .build();

        @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            return Optional.empty();
        }

        if (response.statusCode() != 200) {
            throw new IOException("Error on search user: " + response.statusCode());
        }

        return Optional.of(Json.deserializeObject(response.body()));
    }

    public boolean delete(long id) throws IOException, InterruptedException {
        @NotNull HttpRequest request = auth.addAuth(HttpRequest.newBuilder()
                .uri(URI.create(url + "/" + id))
                .DELETE()).build();

        @NotNull HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
}
