import java.io.*;
import java.net.Socket;

public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = "Novo usuario se conectou: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);

            } while (!clientMessage.equals(".sair"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + "saiu.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Erro em UserThread: " + ex.getMessage());
        }
    }

    private void printUsers() {
        if (server.hasUsers()) {
            writer.println("Usuarios conectados: " + ChatServer.getUserNames());
        } else {
            writer.println("Nenhum usuario conectado");
        }
    }

    void sendMessage(String message) {
        int count = 1;
        this.writer.print(String.format("\033[%dA",count)); //Move cursor para linha de cima do terminal
        this.writer.print("\033[2K"); // Apaga a linha
        writer.println(message);
    }
}
