package codes.matheus;

import codes.matheus.entity.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class ClientServiceTest {
    private final @NotNull ClientService service = new ClientService();

    @Test
    void testGet() throws IOException, InterruptedException {
        service.create(1, "shaulin", "russo@gmail.com");

        @NotNull List<User> users = service.get();
        assertEquals(1, users.size());
        System.out.println(users);
    }

    @Test
    void testPost() throws IOException, InterruptedException {
        @NotNull User user = service.create(2, "skar", "ribelga@gmail.com");
        
        assertNotNull(user);
        assertEquals(2, user.getId());
        assertEquals("skar", user.getUsername().getName());
        assertEquals("ribelga@gmail.com", user.getEmail());
        assertTrue(service.get(2).isPresent());
        assertEquals(service.get(2).get(), user);
    }

    @Test
    void testDelete() throws IOException, InterruptedException {
        service.create(3, "g4mer", "trol4dor");

        assertEquals(3, service.get().size());
        boolean removed = service.delete(3);

        assertTrue(removed);
        assertEquals(2, service.get().size());
        assertNull(service.get(3).orElse(null));
    }
}