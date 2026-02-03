package codes.matheus.util;

import codes.matheus.entity.User;
import codes.matheus.entity.Username;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class JsonTest {
    @Test
    void testSerialize() {
        @NotNull User user = new User(new Username("Matheus"), "mc2019750@gmail.com");
        @NotNull JsonObject object = Json.serialize(user);

        assertEquals(1, object.get("id").getAsInt());
        assertNotNull(object.get("name").getAsString());
        assertNotNull(object.get("email").getAsString());
    }

    @Test
    void testDeserialize() {
        @NotNull User user = new User(new Username("Matheus"), "mc2019750@gmail.com");
        @NotNull JsonObject object = Json.serialize(user);
        @NotNull User copy = Json.deserialize(object.toString());

        assertEquals(copy, user);
        assertEquals(1, copy.getId());
        assertEquals(object, Json.serialize(copy));
    }

}