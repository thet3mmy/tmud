package page.rightshift;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static ExecutorService threadPool = Executors.newFixedThreadPool(10);
    public static ServerSocket serverSocket;
    public static HashMap<String, Location> world;

    public static void main(String[] args) throws IOException, ParseException {
        new WorldLoader().load(false);
        Item.initSystem();

        try {
            serverSocket = new ServerSocket(1000);
            System.out.println("Started on port " + serverSocket.getLocalPort());
            do {
                Socket newSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(newSocket));
                System.out.println(newSocket.getInetAddress() + " connected");
            } while(true);
        } catch (IOException ignored){}
    }
}