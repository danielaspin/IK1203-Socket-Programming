package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    private static int BUFFERSIZE = 1024;
    
    public TCPClient() {
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        Socket socket = new Socket(hostname, port);

        socket.getOutputStream().write(toServerBytes);
        socket.getOutputStream().flush();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFERSIZE];
        int totalBytesRead;

        while (true) {
            totalBytesRead = socket.getInputStream().read(buffer);
            // If the end of the input stream is reached, -1 will return 
            if (totalBytesRead == -1) {
                break;
            }
            byteArray.write(buffer, 0, totalBytesRead);
        }
        byte[] serverMessage = new byte[byteArray.size()];
        serverMessage = byteArray.toByteArray();

        socket.close();

        return serverMessage;
    }

    public static byte[] askServer(String hostname, int port) throws IOException {

        Socket socket = new Socket(hostname, port);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFERSIZE];
        int totalBytesRead;
        
        while (true) {
            totalBytesRead = socket.getInputStream().read(buffer);
            // If the end of the input stream is reached, -1 will return 
            if (totalBytesRead == -1) {
                break;
            }
            byteArray.write(buffer, 0, totalBytesRead);
        }
        byte[] serverMessage = new byte[byteArray.size()];
        serverMessage = byteArray.toByteArray();

        socket.close();
        
        return serverMessage;
    }
}
