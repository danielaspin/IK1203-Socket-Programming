import java.net.*;
import java.io.*;

public class HTTPAsk {
   private static String characterEncoding = "UTF-8";
    public static void main(String[] args) throws IOException {
        // Define the port to listen on
        int port = Integer.parseInt(args[0]);
        //int port = 8888;

        // Create a new server socket and listen for connections on the specified port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port + "...");
            while (true) {
                System.out.println("Waiting for client to connect...");

                // Accept a connection from a client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected!");

                // Create input and output streams for the client socket
                BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream outStream = clientSocket.getOutputStream();

                // Read the client's request from the input stream
                String inputLine = inStream.readLine();
                System.out.println("Received request: " + inputLine);
                String[] resources = inputLine.split("[ ?&]");

                // Check if the request is an "ask" request and extract the parameters
                if (resources.length >= 2 && resources[1].equals("/ask")) {
                    // Define all parameters
                    String hostname = null;
                    Integer portNumber = 0;
                    byte[] dataBytes = null;
                    boolean shutdown = false;
                    Integer timeout = 0;
                    Integer limit = 0;

                    boolean validRequest = true;

                    // Start looping after /ask and loop until last parameter
                    for (int i = 2; i < resources.length-1; i++) {
                        if (resources[i].startsWith("hostname=")) {
                            hostname = resources[i].substring(9);
                        } else if (resources[i].startsWith("port=")) {
                            portNumber = Integer.parseInt(resources[i].substring(5));
                        } else if (resources[i].startsWith("string=")) {
                            dataBytes = resources[i].substring(7).getBytes(characterEncoding);
                        } else if (resources[i].startsWith("shutdown=")) {
                            shutdown = Boolean.parseBoolean(resources[i].substring(9));
                        } else if (resources[i].startsWith("timeout=")) {
                            timeout = Integer.parseInt(resources[i].substring(8));
                        } else if (resources[i].startsWith("limit=")) {
                            limit = Integer.parseInt(resources[i].substring(6));
                        } else {
                            // If an invalid parameter is detected, set validRequest to false
                            validRequest = false;
                        }
                    }

                    // Check if portNumber is equal to 0, set to null if true
                    if (portNumber == 0) {
                        portNumber = null;
                    }

                    // If any of the required parameters are missing or invalid, return a 400 Bad Request response
                    if (hostname == null || portNumber == null || !validRequest || !resources[0].equals("GET") || !resources[resources.length-1].equals("HTTP/1.1")) {
                        String httpResponse = "HTTP/1.1 400 Bad Request\r\n\r\n";
                        outStream.write(httpResponse.getBytes(characterEncoding));
                        System.out.println("Sent response: " + httpResponse);
                    }
                    else if (resources[0].equals("GET") && resources[resources.length-1].equals("HTTP/1.1")) {
                        // If everything else is OK then return 200 OK response
                        
                        // Check if timeout is equal to 0, set to null if true
                        if (timeout == 0) {
                            timeout = null;
                        }
                        // Check if limit is equal to 0, set to null if true
                        if (limit == 0) {
                            limit = null;
                        }

                        // Call the TCPClient to ask the server for a response
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] responseBytes;
                        byte[] nullArray = new byte[0];
                        if (dataBytes != null) {
                            dataBytes = (new String(dataBytes) + "\n").getBytes(characterEncoding);
                            responseBytes = tcpClient.askServer(hostname, portNumber, dataBytes);
                        } else {
                            responseBytes = tcpClient.askServer(hostname, portNumber, nullArray);
                        }

                        // Write the HTTP response to the output stream and return a 200 OK Request response
                        String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + new String(responseBytes, characterEncoding);
                        outStream.write(httpResponse.getBytes(characterEncoding));
                        System.out.println("Sent response: " + httpResponse);
                    }
                } else {
                    // If the request is not an "ask" request, return a 404 Not Found response
                    String httpResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
                    outStream.write(httpResponse.getBytes(characterEncoding));
                    System.out.println("Sent response: " + httpResponse);
                }

                // Close the input and output streams and the client socket
                outStream.close();
                inStream.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            }
        }
    }
}