import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private Set<Group> myGroups; // Colección para almacenar grupos a los que pertenece
    private Map<String, Stack<String>> messageStacks; // Almacena pilas de mensajes con otros clientes
    private Map<String, Stack<String>> groupMessageStacks; // Historial de mensajes para cada grupo

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.myGroups = new HashSet<>(); 
        this.messageStacks = new HashMap<>(); // Historial para mensajes de usuarios
        this.groupMessageStacks = new HashMap<>(); // Historial para mensajes de grupos
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
        // Mostrar mensajes anteriores con el cliente objetivo
        showPreviousMessages(targetClient);

        out.println("Escribe tu mensaje:");
        String message = in.readLine();
        if (message == null) {
            throw new IOException("Cliente desconectado");
        }

        // Guardar el mensaje en la pila del remitente
        saveMessage(targetClient, "Mensaje de " + name + ": " + message);
        // Enviar el mensaje al destinatario y también guardar en la pila del destinatario
        Server.sendMessage(targetClient, "Mensaje de " + name + ": " + message, name);
    } else {
        out.println("Cliente no válido o es tu propio nombre.");
    }
}

    
    // Método para mostrar mensajes anteriores
    private void showPreviousMessages(String targetClient) {
        Stack<String> stack = messageStacks.get(targetClient);
        if (stack != null && !stack.isEmpty()) {
            out.println("Mensajes anteriores con " + targetClient + ":");
            for (String message : stack) {
                out.println(message);
            }
        } else {
            out.println("No hay mensajes anteriores con " + targetClient + ".");
        }
    }
    
    // Método para guardar un mensaje en la pila
    private void saveMessage(String targetClient, String message) {
        messageStacks.putIfAbsent(targetClient, new Stack<>());
        messageStacks.get(targetClient).push(message);
    }
    

    // Método para enviar un mensaje al cliente
    public void sendMessage(String message, String sender) {
        //out.println(message);
    // Guardar el mensaje recibido en la pila del cliente receptor
        saveMessage(sender, message);
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
            // Mostrar mensajes anteriores antes de enviar el nuevo mensaje
            showPreviousGroupMessages(groupName);
    
            out.println("Escribe tu mensaje:");
            String message = in.readLine();
            
            // Guardar el mensaje en el historial del grupo
            saveGroupMessage(groupName, "Mensaje de " + name + ": " + message);
            
            // Enviar el mensaje a todos los miembros del grupo
            group.sendMessage(message, name);
        } else {
            out.println("No eres miembro de ese grupo o el grupo no existe.");
        }
    }
    
    private void showPreviousGroupMessages(String groupName) {
        Stack<String> stack = groupMessageStacks.get(groupName);
        if (stack != null && !stack.isEmpty()) {
            out.println("Mensajes anteriores en el grupo " + groupName + ":");
            for (String message : stack) {
                out.println(message);
            }
        } else {
            out.println("No hay mensajes anteriores en el grupo " + groupName + ".");
        }
    }
    
    private void saveGroupMessage(String groupName, String message) {
        groupMessageStacks.putIfAbsent(groupName, new Stack<>()); // Inicializa la pila si no existe
        groupMessageStacks.get(groupName).push(message); // Añade el mensaje a la pila
    }
    
    
    
    private String getMyGroups() {
        StringBuilder groupList = new StringBuilder();
        for (Group group : myGroups) {
            groupList.append(group.getName()).append(" ");
        }
        return groupList.toString().trim();
    }
    
}
