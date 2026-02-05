package codes.matheus.http;

import codes.matheus.entity.User;
import codes.matheus.util.Json;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class UserHandler implements HttpHandler {
    private final @NotNull List<User> users;

    public UserHandler(@NotNull List<User> users) {
        this.users = users;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            get(exchange);
        } else if (exchange.getRequestMethod().equals("POST")) {
            post(exchange);
        } else if (exchange.getRequestMethod().equals("DELETE")) {
            delete(exchange);
        }
    }

    private void get(@NotNull HttpExchange exchange) throws IOException {
        @NotNull String path = exchange.getRequestURI().getPath();
        @NotNull String[] parts = path.split("/");
        @NotNull String response = "";
        int status = 200;

        if (parts.length == 3) {
            response = Json.serialize(users).toString();
        } else if (parts.length == 4 && parts[3].matches("\\d+")) {
            try {
                long id = Long.parseLong(parts[3]);
                @Nullable User found = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);

                if (found != null) {
                    response = Json.serialize(found).toString();
                } else {
                    @NotNull JsonObject error = new JsonObject();
                    error.addProperty("error", "user not found");
                    status = 404;
                }
            } catch (NumberFormatException e) {
                @NotNull JsonObject error = new JsonObject();
                error.addProperty("error", "invalid id");
                response = error.toString();
                status = 400;
            }
        } else {
            @NotNull JsonObject error = new JsonObject();
            error.addProperty("error", "path not found");
            response = error.toString();
            status = 400;
        }

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes(StandardCharsets.UTF_8).length);
        printResponse(exchange, response);
    }

    private void post(@NotNull HttpExchange exchange) throws IOException {
        @NotNull InputStream is = exchange.getRequestBody();
        @NotNull String body = new String(is.readAllBytes());

        @NotNull User user = Json.deserializeObject(body);
        users.add(user);

        @NotNull String response = Json.serialize(user).toString();
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, response.getBytes().length);
        printResponse(exchange, response);
    }

    private void delete(@NotNull HttpExchange exchange) throws IOException {
        @NotNull String path = exchange.getRequestURI().getPath();
        @NotNull String[] parts = path.split("/");

        if (parts.length != 4 || !parts[3].matches("\\d+")) {
            @NotNull JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "invalid id on URL");
            @NotNull String response = errorObj.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            printResponse(exchange, response);
        }

        long id;
        try {
            id = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            @NotNull JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "invalid id");
            @NotNull String response = errorObj.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            printResponse(exchange, response);
            return;
        }

        boolean removed = users.removeIf(user -> user.getId() == id);
        if (removed) {
            @NotNull JsonObject success = new JsonObject();
            success.addProperty("message", "user deleted with success");
            success.addProperty("id", id);

            @NotNull String response = success.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            printResponse(exchange, response);
        } else {
            @NotNull JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "user not found");
            @NotNull String response = errorObj.toString();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
            printResponse(exchange, response);
        }
    }

    private void printResponse(@NotNull HttpExchange exchange, @NotNull String response) throws IOException {
        @NotNull OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

}
