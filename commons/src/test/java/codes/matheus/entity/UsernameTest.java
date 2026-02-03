package codes.matheus.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class UsernameTest {
    @Test
    void testValidate() {
        assertFalse(Username.validate("a"));
        assertFalse(Username.validate("1245464"));
        assertTrue(Username.validate("shaulin"));
        assertTrue(Username.validate("skar"));
    }
}