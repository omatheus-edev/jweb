package codes.matheus.util;

import codes.matheus.entity.User;
import codes.matheus.entity.Username;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class JsonTest {
    @Test
    void testSerializeObject() {
        @NotNull User user = new User(new Username("Matheus"), "mc2019750@gmail.com");
        @NotNull JsonObject object = Json.serialize(user);

        assertEquals(1, object.get("id").getAsInt());
        assertNotNull(object.get("name").getAsString());
        assertNotNull(object.get("email").getAsString());
    }

    @Test
    void testSerializeArray() {
        @NotNull User user1 = new User(new Username("Carlos"), "sla@email.com");
        @NotNull User user2 = new User(new Username("junior"), "jr@email.com");
        @NotNull User user3 = new User(new Username("lucas"), "lc@email.com");
        @NotNull User user4 = new User(new Username("henrique"), "hrq@email.com");
        @NotNull List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        @NotNull JsonArray array = Json.serialize(users);
        @NotNull List<User> copy = Json.deserializeArray(array.toString());

        assertEquals(copy, users);
        System.out.println(users);
        System.out.println(copy);
    }

    @Test
    void testDeserialize() {
        @NotNull User user = new User(new Username("Matheus"), "mc2019750@gmail.com");
        @NotNull JsonObject object = Json.serialize(user);
        @NotNull User copy = Json.deserializeObject(object.toString());

        assertEquals(copy, user);
        assertEquals(1, copy.getId());
        assertEquals(object, Json.serialize(copy));
    }

}