import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            // Conectarse al servidor en la dirección local y puerto 1234
            Socket socket = new Socket("localhost", 1234);

            // Obtener los flujos de entrada y salida
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Crear un hilo para escuchar mensajes del servidor de forma independiente
            Thread messageListener = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        // Mostrar cualquier mensaje recibido del servidor
                        System.out.println(fromServer);

                        // Manejar la notificación de llamada
                        if (fromServer.contains("te está llamando")) {
                            System.out.println("¿Quieres aceptar la llamada? (sí/no)");
                            String response = stdIn.readLine(); // Leer respuesta del usuario
                            out.println(response); // Enviar respuesta al servidor
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageListener.start(); // Iniciar el hilo de recepción de mensajes

            // Leer mensajes del usuario y enviarlos al servidor
            String fromUser;
            while (true) {
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    out.println(fromUser); // Enviar datos al servidor
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
