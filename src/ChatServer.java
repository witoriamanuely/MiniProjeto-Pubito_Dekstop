import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private int port;
    private static Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();

    private ChatServer(int port) {
        this.port = port;
    }

    private void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server funcionando na porta " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Novo usuario conectado");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Erro no servidor: " + ex.getMessage());
        }
    }

    void broadcast(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
            if (user != excludeUser) {
                user.sendMessage(message);
            }
        }
    }

    void addUserName(String userName) {
        userNames.add(userName);
    }

    void removeUser(String userName, UserThread userThread) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(userThread);
            System.out.println("O usuario " + userName + " saiu");
        }
    }

    static Set<String> getUserNames() {
        return userNames;
    }

    boolean hasUsers() {
        return !userNames.isEmpty();
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(27017);
        server.execute();
    }
}
