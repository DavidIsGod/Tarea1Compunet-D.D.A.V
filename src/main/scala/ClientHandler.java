import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private ChatServer server;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(ChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username:");
            username = in.readLine();
            server.registerUser(username, this);

            out.println("Menu: 1. Create group, 2. Join group, 3. Private message");
            String option = in.readLine();

            switch (option) {
                case "1":
                    out.println("Enter group name:");
                    String groupName = in.readLine();
                    server.createGroup(groupName, username);
                    break;
                case "2":
                    out.println(server.getGroupsList());
                    out.println("Enter group name to join:");
                    String joinGroupName = in.readLine();
                    server.joinGroup(joinGroupName, username);
                    break;
                case "3":
                    out.println("Enter recipient username:");
                    String recipient = in.readLine();
                    out.println("Enter your message:");
                    String privateMessage = in.readLine();
                    server.sendMessageToUser(username, recipient, privateMessage);
                    break;
                default:
                    out.println("Invalid option.");
                    break;
            }

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("/msg")) {
                    String[] tokens = input.split(" ", 3);
                    String recipient = tokens[1];
                    String message = tokens[2];
                    server.sendMessageToUser(username, recipient, message);
                } else if (input.startsWith("/groupmsg")) {
                    String[] tokens = input.split(" ", 3);
                    String group = tokens[1];
                    String message = tokens[2];
                    server.sendMessageToGroup(group, username, message);
                } else {
                    out.println("Unknown command.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
