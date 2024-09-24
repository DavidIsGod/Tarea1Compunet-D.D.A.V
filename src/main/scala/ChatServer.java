import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private ServerSocket serverSocket;
    private Map<String, User> connectedUsers; // Usuarios conectados
    private Map<String, Group> groups;        // Grupos de chat
    private ExecutorService threadPool;       // ThreadPool para manejar múltiples clientes

    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            connectedUsers = new HashMap<>();
            groups = new HashMap<>();
            threadPool = Executors.newFixedThreadPool(10); // Pool con 10 hilos
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        System.out.println("Server started, waiting for clients...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                // Manejar al cliente con el ThreadPool
                threadPool.execute(new ClientHandler(this, clientSocket));
            } catch (IOException e) {
                System.out.println("Error accepting client: " + e.getMessage());
            }
        }
    }

    public synchronized void registerUser(String username, ClientHandler clientHandler) {
        if (!connectedUsers.containsKey(username)) {
            User newUser = new User(username, clientHandler);
            connectedUsers.put(username, newUser);
            System.out.println(username + " registered.");
        } else {
            System.out.println("Username already taken.");
        }
    }

    public synchronized void createGroup(String groupName, String creator) {
        if (!groups.containsKey(groupName)) {
            User user = connectedUsers.get(creator);
            Group newGroup = new Group(groupName, user);
            groups.put(groupName, newGroup);
            System.out.println("Group " + groupName + " created.");
        } else {
            System.out.println("Group name already exists.");
        }
    }

    public synchronized String getGroupsList() {
        StringBuilder groupList = new StringBuilder("Groups:\n");
        for (Group group : groups.values()) {
            groupList.append(group.getGroupName()).append(" - ").append(group.getMemberCount()).append(" members\n");
        }
        return groupList.toString();
    }

    public synchronized void joinGroup(String groupName, String username) {
        if (groups.containsKey(groupName)) {
            Group group = groups.get(groupName);
            User user = connectedUsers.get(username);
            group.addMember(user);
            System.out.println(username + " joined group " + groupName);
        } else {
            System.out.println("Group not found: " + groupName);
        }
    }

    public synchronized void sendMessageToGroup(String groupName, String sender, String message) {
        if (groups.containsKey(groupName)) {
            Group group = groups.get(groupName);
            for (User member : group.getMembers()) {
                if (!member.getUsername().equals(sender)) {
                    member.getClientHandler().sendMessage("Group " + groupName + " | " + sender + ": " + message);
                }
            }
        } else {
            System.out.println("Group not found: " + groupName);
        }
    }

    // Nuevo método para enviar un mensaje privado de un usuario a otro
    public synchronized void sendMessageToUser(String sender, String recipient, String message) {
        if (connectedUsers.containsKey(recipient)) {
            User recipientUser = connectedUsers.get(recipient);
            recipientUser.getClientHandler().sendMessage(sender + ": " + message);
        } else {
            System.out.println("User not found: " + recipient);
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        ChatServer server = new ChatServer(port);
        server.start();
    }
}


