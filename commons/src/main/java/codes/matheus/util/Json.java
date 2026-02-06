package codes.matheus.util;

import codes.matheus.entity.User;
import codes.matheus.entity.Username;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.bind.JsonTreeWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Json {
    private Json() {
        throw new UnsupportedOperationException("the class should not be instantiated");
    }

    public static @NotNull JsonObject serialize(@NotNull User user) {
        try (@NotNull JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name("id").value(user.getId());
            writer.name("name").value(user.getUsername().getName());
            writer.name("email").value(user.getEmail());
            writer.endObject();
            return writer.get().getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize: " + e.getMessage());
        }
    }

    public static @NotNull JsonArray serialize(@NotNull List<User> users) {
        try (@NotNull JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginArray();
            for (@NotNull User user : users) {
                writer.beginObject();
                writer.name("id").value(user.getId());
                writer.name("name").value(user.getUsername().getName());
                writer.name("email").value(user.getEmail());
                writer.endObject();
            }
            writer.endArray();
            return writer.get().getAsJsonArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize: " + e.getMessage());
        }
    }

    public static @NotNull String serialize(@NotNull String key, @NotNull String value) {
        try (@NotNull JsonTreeWriter writer = new JsonTreeWriter()) {
            writer.beginObject();
            writer.name(key).value(value);
            writer.endObject();
            return writer.get().toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize: " + e.getMessage());
        }
    }

    public static @NotNull User deserializeObject(@NotNull String json) {
        @NotNull JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        long id = object.get("id").getAsLong();
        @NotNull Username username = new Username(object.get("name").getAsString());
        @NotNull String email = object.get("email").getAsString();
        return new User(id, username, email);
    }

    public static @NotNull List<User> deserializeArray(@NotNull String json) {
        @NotNull JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        @NotNull List<User> users = new ArrayList<>();
        for (@NotNull JsonElement element : array) {
            users.add(deserializeObject(element.toString()));
        }
        return users;
    }
}
