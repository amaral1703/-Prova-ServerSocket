import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class main {
    private static final int PORT = 2525;
   

    public static void main(String[] args) {
        System.out.println("Servidor SMTP iniciado na porta " + PORT);
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new SMTPClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor");
        }
    }

    
}
