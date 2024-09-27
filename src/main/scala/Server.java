import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    public static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    public static Map<String, Group> groups = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Servidor esperando conexiones...");

            // ThreadPool para manejar múltiples clientes
            ExecutorService pool = Executors.newFixedThreadPool(5);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");
                
                // Crear y asignar el nuevo cliente a un hilo
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enviar un mensaje a un cliente en específico
    public static void sendMessage(String receiverName, String message, String senderName) {
        ClientHandler receiver = clients.get(receiverName);
        if (receiver != null) {
            receiver.sendMessage(message, senderName);  // Ahora pasa el nombre del remitente
         } else {
            System.out.println("Cliente no encontrado: " + receiverName);
        }
    }  


    public static void addGroup(Group group) {
        groups.put(group.getName(), group);
    }

    public static void sendAudio(String receiverName, File audioFile, String senderName) {
        ClientHandler receiver = clients.get(receiverName);
        if (receiver != null) {
            receiver.receiveAudio(audioFile, senderName);
        } else {
            System.out.println("Cliente no encontrado: " + receiverName);
        }
    }
    
    
}
