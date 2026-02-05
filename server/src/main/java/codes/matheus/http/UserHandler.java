package codes.matheus.http;

import codes.matheus.entity.User;
import codes.matheus.util.Json;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
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
        } else {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .headers("Allow", "GET, POST, DELETE")
                    .body("method not allowed")
                    .build().send();
        }
    }

    private void get(@NotNull HttpExchange exchange) throws IOException {
        @NotNull String path = exchange.getRequestURI().getPath();
        @NotNull String[] parts = path.split("/");
        @NotNull Response.Builder response = Response.builder(exchange);

        if (parts.length == 3) {
            response.body(Json.serialize(users).toString());
        } else if (parts.length == 4 && parts[3].matches("\\d+")) {
            long id;
            try {
                id = Long.parseLong(parts[3]);
            } catch (NumberFormatException e) {
                response.status(HttpStatus.BAD_REQUEST).body("invalid id").build().send();
                return;
            }

            @Nullable User found = users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
            if (found != null) {
                response.body(Json.serialize(found).toString());
            } else {
                response.status(HttpStatus.NOT_FOUND).body("user not found");
            }
        } else {
            response.status(HttpStatus.BAD_REQUEST).body("url invalid");
        }

        response.build().send();
    }

    private void post(@NotNull HttpExchange exchange) throws IOException {
        @NotNull Response.Builder response = Response.builder(exchange);

        if (exchange.getRequestURI().getPath().split("/").length != 3) {
            response.status(HttpStatus.BAD_REQUEST).body("url invalid").build().send();
            return;
        }

        try (InputStream input = exchange.getRequestBody()) {
            @NotNull String body = new String(input.readAllBytes());

            if (body.isBlank()) {
                response.status(HttpStatus.BAD_REQUEST).body("body request is blank").build().send();
                return;
            }

            @NotNull User user = Json.deserializeObject(body);
            users.add(user);

            response.status(HttpStatus.CREATED).body(Json.serialize(user).toString());
        } catch (Exception e) {
            response.status(HttpStatus.BAD_REQUEST).body("failed to process json. " + e.getMessage());
        }
        response.build().send();
    }

    private void delete(@NotNull HttpExchange exchange) throws IOException {
        @NotNull String path = exchange.getRequestURI().getPath();
        @NotNull String[] parts = path.split("/");
        @NotNull Response.Builder response = Response.builder(exchange);

        if (parts.length != 4 || !parts[3].matches("\\d+")) {
            response.status(HttpStatus.BAD_REQUEST).body("invalid id or url").build().send();
            return;
        }

        long id;
        try {
            id = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            response.status(HttpStatus.BAD_REQUEST).body("invalid id").build().send();
            return;
        }

        boolean removed = users.removeIf(user -> user.getId() == id);
        if (removed) {
            response.body("user deleted with success")
                    .headers("Deleted-ID", String.valueOf(id));
        } else {
            response.status(HttpStatus.NOT_FOUND).body("user not found");
        }
        response.build().send();
    }
}
