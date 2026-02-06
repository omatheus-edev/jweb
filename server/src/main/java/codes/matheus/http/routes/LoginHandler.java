package codes.matheus.http.routes;

import codes.matheus.http.HttpStatus;
import codes.matheus.http.Response;
import codes.matheus.util.Json;
import codes.matheus.util.JwtUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class LoginHandler implements HttpHandler {
    private static final @NotNull String VALIDATE_USERNAME = "admin";
    private static final @NotNull String VALIDATE_PASSWORD = "javaislife";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("POST")) {
            login(exchange);
        } else {
            Response.builder(exchange)
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .header("Allow", "POST")
                    .error("method not allowed")
                    .build().send();
        }
    }

    private void login(@NotNull HttpExchange exchange) throws IOException {
        @NotNull Response.Builder response = Response.builder(exchange);
        @NotNull String body = "";

        try (InputStream is = exchange.getRequestBody()) {
            body = new String(is.readAllBytes());
        } catch (Exception e) {
            response.status(HttpStatus.INTERNAL_SERVER_ERROR).error("failed on read body");
        }

        if (body.isBlank()) {
            response.status(HttpStatus.BAD_REQUEST).error("requisition body invalid").build().send();
            return;
        }

        @NotNull JsonObject credentials;
        try {
            credentials = JsonParser.parseString(body).getAsJsonObject();
        } catch (Exception e) {
            response.status(HttpStatus.BAD_REQUEST).error("json invalid").build().send();
            return;
        }

        @Nullable JsonElement usernameElem = credentials.get("username");
        @Nullable JsonElement passwordElem = credentials.get("password");

        @Nullable String username = (usernameElem != null && usernameElem.isJsonPrimitive())
                ? usernameElem.getAsString()
                : null;

        @Nullable String password = (passwordElem != null && passwordElem.isJsonPrimitive())
                ? passwordElem.getAsString()
                : null;


        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            response.status(HttpStatus.BAD_REQUEST).error("username and password are required").build().send();
            return;
        }

        if (VALIDATE_USERNAME.equals(username) && VALIDATE_PASSWORD.equals(password)) {
            @NotNull String token = JwtUtil.generateToken(username, 60 * 60 * 1000);
            response.status(HttpStatus.OK).body(Json.serialize("token", token));
        } else {
            response.status(HttpStatus.BAD_REQUEST).error("credentials invalids");
        }

        response.build().send();
    }
}
