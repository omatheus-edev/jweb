package codes.matheus.authorization;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.http.HttpRequest;

public final class Authorization {
    private @Nullable String token;

    public Authorization() {
        this.token = null;
    }

    public @Nullable String getToken() {
        return token;
    }

    public void setToken(@Nullable String token) {
        this.token = token;
    }

    public void clearToken() {
        this.token = null;
    }

    public @NotNull HttpRequest.Builder addAuth(@NotNull HttpRequest.Builder builder) {
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return builder;
    }
}
