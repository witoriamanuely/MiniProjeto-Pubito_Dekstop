import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = this.socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Erro recebendo Output Stream: " + ex.getMessage());
        }
    }

    public void run() {

        Console console = System.console();
        String userName;

        while (true) {
            userName = console.readLine("\nDigite seu nome: ");
            if (ChatServer.getUserNames().contains(userName)) {
                this.writer.println("Escolha outra nome");
            } else {
                break;
            }
        }

        this.client.setUserName(userName);
        this.writer.println(userName);

        String text;

        do {
            text = console.readLine("[" + userName + "]: ");
            this.writer.println(text);
        } while (!text.equals(".sair"));

        try {
            socket.close();
        } catch (IOException ex) {
            System.out.println("Erro escrevendo para Servidor: " + ex.getMessage());
        }
    }
}
