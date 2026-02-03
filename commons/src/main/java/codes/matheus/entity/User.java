package codes.matheus.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class User {
    private final long id;
    private final @NotNull Username username;
    private @NotNull String email;

    public User(@NotNull Username username, @NotNull String email) {
        this.id = GeneratedID.incrementId();
        this.username = username;
        this.email = email;
    }

    public User(long id, @NotNull Username username, @NotNull String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public @NotNull Username getUsername() {
        return username;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                ", name=" + username +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private static final class GeneratedID {
        private static final @NotNull AtomicLong id = new AtomicLong(0);

        @Range(from = 0, to = Long.MAX_VALUE)
        private static long incrementId() {
            return id.incrementAndGet();
        }
    }
}
