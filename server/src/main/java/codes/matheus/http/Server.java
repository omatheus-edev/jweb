package codes.matheus.http;

import codes.matheus.entity.User;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public final class Server implements Runnable {
    private final @NotNull HttpServer server;
    private final @NotNull List<User> users = new ArrayList<>();

    public Server(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    @Override
    public void run() {
        server.createContext("/api/users", new UserHandler(users));
        server.start();

        System.out.println("Server running in http://localhost:" + server.getAddress().getPort());
    }
}
