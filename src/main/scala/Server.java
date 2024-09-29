import java.io.*;
import java.net.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class Server {
    public static Map<String, ClientHandler> clients = new HashMap<>();
    public static Map<String, Group> groups = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGroup(Group group) {
        groups.put(group.getName(), group);
    }

    public static void sendMessage(String targetClient, String message, String sender) {
        if (clients.containsKey(targetClient)) {
            clients.get(targetClient).sendMessage(message, sender);
        }
    }

    public static void sendAudio(String targetClient, File audioFile) {
        ClientHandler targetHandler = clients.get(targetClient);
        if (targetHandler != null) {
            targetHandler.sendAudio(audioFile, "Audio recibido");
            // Reproducir el audio después de enviarlo
            targetHandler.playAudio(audioFile);
        }
    }

    // Método para reproducir audio
    public static void playAudio(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            System.out.println("Reproduciendo audio...");

            // Esperar a que termine la reproducción
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            System.out.println("Error al reproducir el audio: " + e.getMessage());
        }
    }
}
