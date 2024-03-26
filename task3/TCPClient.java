import java.net.*;
import java.io.*;

public class TCPClient {

    private static int BUFFERSIZE = 1024;

    private boolean shutdown;
    private Integer timeout;
    private Integer limit;
    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {

        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        Socket socket = new Socket(hostname, port);
        
        int timeoutValue = 0;
        if (timeout != null) {
            timeoutValue = timeout;
        }
        socket.setSoTimeout(timeoutValue);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(toServerBytes);
        outputStream.flush();

        if (shutdown == true) {
            socket.shutdownOutput();
            System.out.println("Shutdown occured");
        }

        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFERSIZE];
        int totalBytesRead = 0;
        int bytesRead = 0;

        while (true) {
            try {
                bytesRead = inputStream.read(buffer);
                // If the end of the input stream is reached, -1 will return 
                if (bytesRead == -1) {
                    break;
                }
                if (limit != null && totalBytesRead + bytesRead > limit) {
                    byteArray.write(buffer, 0, limit - totalBytesRead);
                    totalBytesRead = limit;
                    break;
                }
                totalBytesRead += bytesRead;    
                byteArray.write(buffer, 0, bytesRead);
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout reached: " + timeout + " ms");
                break;
            }
        }
        byte[] serverMessage = new byte[byteArray.size()];
        serverMessage = byteArray.toByteArray();

        socket.close();
        System.out.println("Received " + totalBytesRead + " bytes");

        return serverMessage;
    }
}