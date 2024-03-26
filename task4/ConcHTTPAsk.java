import java.net.*;
import java.io.*;

public class ConcHTTPAsk {
    public static void main(String[] args) throws IOException {
        // Define the port to listen on
        int port = Integer.parseInt(args[0]);
        //int port = 8888;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Listening on port: " + port);

            // Loop forever to accept client socket connections
            while (true) {
                try {
                    // Accept a new client socket connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("A client connected from " + clientSocket.getInetAddress());
    
                    // Create a new thread for each client connection
                    new Thread(new MyRunnable(clientSocket)).start();

                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }
}