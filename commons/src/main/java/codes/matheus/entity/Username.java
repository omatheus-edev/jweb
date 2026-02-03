package codes.matheus.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Username implements CharSequence {
    public static boolean validate(@NotNull String name) {
        if (name.isBlank()) {
            return false;
        } else if (name.length() < 3 || name.length() >= 16) {
            return false;
        } else if (!name.matches("^[a-zA-Z0-9_.-]+$")) {
            return false;
        } else if (name.matches("\\d+")) {
            return false;
        }
        return true;
    }

    public static @NotNull Username parse(@NotNull String name) {
        return new Username(name);
    }

    private final @NotNull String name;

    public Username(@NotNull String name) {
        if (!validate(name)) {
            throw new IllegalArgumentException("Username invalid");
        }
        this.name = name;
    }

    public @NotNull String getName() {
        return name;
    }

    @Override
    public int length() {
        return name.length();
    }

    @Override
    public char charAt(int i) {
        return name.charAt(i);
    }

    @Override
    public @NotNull CharSequence subSequence(int i, int i1) {
        return name.subSequence(i, i1);
    }

    @Override
    public @NotNull String toString() {
        return "name=" + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(name, username.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
