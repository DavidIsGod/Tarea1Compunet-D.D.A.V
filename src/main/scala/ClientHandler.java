import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private Set<Group> myGroups; // Colección para almacenar grupos a los que pertenece

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.myGroups = new HashSet<>(); // Inicializar conjunto de grupos
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Pedir nombre al cliente
            out.println("Introduce tu nombre: \n-");
            name = in.readLine();

            // Añadir cliente al mapa
            Server.clients.put(name, this);
            System.out.println(name + " se ha conectado.");

            boolean running = true;
        while (running) {
            out.println("\n--- Menú ---");
            out.println("1. Enviar mensaje a un usuario");
            out.println("2. Crear grupo");
            out.println("3. Unirse a un grupo");
            out.println("4. Mis grupos");
            out.println("5. Enviar mensaje a un grupo"); // Nueva opción
            out.println("6. Salir");
            out.println("Elige una opción:");

            String option = in.readLine();
            if (option == null) {
                throw new IOException("Cliente desconectado");
            }

            switch (option) {
                case "1":
                    sendMessageToAnotherClient();
                    break;
                case "2":
                    createGroup();
                    break;
                case "3":
                    joinGroup();
                    break;
                case "4":
                    showMyGroups();
                    break;
                case "5": // Llama a sendMessageToGroup aquí
                    sendMessageToGroup();
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    out.println("Opción no válida.");
                    break;
            }
        }

        } catch (IOException e) {
            System.out.println("Error de I/O: " + e.getMessage());
        } finally {
            try {
                Server.clients.remove(name);
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    private void createGroup() throws IOException {
        out.println("Introduce el nombre del nuevo grupo:");
        String groupName = in.readLine();
        Group group = new Group(groupName);
        Server.addGroup(group);
        group.addMember(this); // Añadir al creador al grupo
        myGroups.add(group);
        out.println("Grupo " + groupName + " creado y te has unido a él.");
    }

    private void joinGroup() throws IOException {
        out.println("Grupos disponibles: " + getAvailableGroups());
        out.println("Escribe el nombre del grupo al que deseas unirte:");
        String groupName = in.readLine();
        
        Group group = Server.groups.get(groupName);
        if (group != null && !myGroups.contains(group)) {
            group.addMember(this);
            myGroups.add(group);
            out.println("Te has unido al grupo " + groupName + ".");
        } else {
            out.println("Grupo no válido o ya eres miembro.");
        }
    }

    private void showMyGroups() {
        if (myGroups.isEmpty()) {
            out.println("No estás en ningún grupo.");
            return;
        }
        out.println("Mis grupos:");
        for (Group group : myGroups) {
            out.println(group.getName());
        }
    }



    private String getAvailableGroups() {
        StringBuilder groupList = new StringBuilder();
        for (String groupName : Server.groups.keySet()) {
            if (!myGroups.contains(Server.groups.get(groupName))) {
                groupList.append(groupName).append(" ");
            }
        }
        return groupList.toString().trim();
    }

    // Método para enviar un mensaje a otro cliente
    private void sendMessageToAnotherClient() throws IOException {
        out.println("Clientes conectados: " + getConnectedClients());
        out.println("Escribe el nombre del cliente al que deseas enviar un mensaje:");

        String targetClient = in.readLine();
        if (targetClient == null) {
            throw new IOException("Cliente desconectado");
        }

        if (Server.clients.containsKey(targetClient) && !targetClient.equals(name)) {
            out.println("Escribe tu mensaje:");
            String message = in.readLine();
            if (message == null) {
                throw new IOException("Cliente desconectado");
            }
            Server.sendMessage(targetClient, "Mensaje de " + name + ": " + message);
        } else {
            out.println("Cliente no válido o es tu propio nombre.");
        }
    }

    // Método para enviar un mensaje al cliente
    public void sendMessage(String message) {
        out.println(message);
    }

    // Obtener una lista de los clientes conectados (excluyendo al propio cliente)
    private String getConnectedClients() {
        StringBuilder clientList = new StringBuilder();
        for (String clientName : Server.clients.keySet()) {
            if (!clientName.equals(name)) {
                clientList.append(clientName).append(" ");
            }
        }
        return clientList.toString().trim();
    }

    private void sendMessageToGroup() throws IOException {
        out.println("Mis grupos: " + getMyGroups());
        out.println("Escribe el nombre del grupo al que deseas enviar un mensaje:");
        String groupName = in.readLine();
        
        Group group = Server.groups.get(groupName);
        if (group != null && myGroups.contains(group)) {
            out.println("Escribe tu mensaje:");
            String message = in.readLine();
            group.sendMessage("Mensaje de " + name + ": " + message);
        } else {
            out.println("No eres miembro de ese grupo o el grupo no existe.");
        }
    }
    
    private String getMyGroups() {
        StringBuilder groupList = new StringBuilder();
        for (Group group : myGroups) {
            groupList.append(group.getName()).append(" ");
        }
        return groupList.toString().trim();
    }
    
}
