import java.io.*;
import java.net.*;

public class SMTPClientHandler implements Runnable {
    private final Socket clientSocket;
    private static final String SERVER_NAME = "Gabriel_smtp_server";

    public SMTPClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        //usamos o buffereader para ler textos 
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
            out.println("220 " + SERVER_NAME + " Simple Mail Transfer Service Ready");


            String senderEmail = null;
            String recipientEmail = null;
            // aqui recebemos os comando e enviamos eles para o servidor 
            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("HELO")) {
                    out.println("250 Hello, pleased to meet you");
                } else if (input.startsWith("MAIL FROM:")) {
                    senderEmail = extractEmail(input);
                    if (isValidEmail(senderEmail)) {
                        out.println("250 Sender " + senderEmail + " OK");
                    } else {
                        out.println("550 Invalid sender email");
                    }
                } else if (input.startsWith("RCPT TO:")) {
                    recipientEmail = extractEmail(input);
                    if (isValidEmail(recipientEmail)) {
                        out.println("250 Recipient " + recipientEmail + " OK");
                    } else {
                        out.println("550 Invalid recipient email");
                    }
                } else if (input.equals("DATA")) {
                    out.println("354 End data with <CR><LF>.<CR><LF>");
                    StringBuilder data = new StringBuilder();
                    String line;
                    while (!(line = in.readLine()).equals(".")) {
                        data.append(line).append("\n");
                    }
                    out.println("250 Message accepted for delivery");
                    System.out.println("E-mail recebido:");
                    System.out.println("De: " + senderEmail);
                    System.out.println("Para: " + recipientEmail);
                    System.out.println("Mensagem:\n" + data);
                } else if (input.equals("QUIT")) {
                    out.println("221 " + SERVER_NAME + " Service closing transmission channel");
                    break;
                } else {
                    out.println("500 Syntax error, command unrecognized");
                }
            }
        } catch (IOException e) {
            System.err.println("Erro no cliente ");
        }
    }

    // Extrai o e-mail de um comando
    private String extractEmail(String command) {
        int start = command.indexOf('<');
        int end = command.indexOf('>');
        if (start != -1 && end != -1 && start < end) {
            return command.substring(start + 1, end);
        }
        return null;
    }

    // Valida o formato de um e-mail
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}