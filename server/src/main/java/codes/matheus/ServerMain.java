package codes.matheus;

import codes.matheus.http.Server;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        @NotNull Server server = new Server(8080);
        new Thread(server).start();
    }
}