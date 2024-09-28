import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            // Conectarse al servidor en la dirección local y puerto 1234
            Socket socket = new Socket("192.168.20.98", 1234);

            // Obtener los flujos de entrada y salida
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Crear un hilo para escuchar mensajes del servidor de forma independiente
            Thread messageListener = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.startsWith("AUDIO_FILE:")) {
                            String fileName = fromServer.substring("AUDIO_FILE:".length());
                            fromServer = in.readLine(); // Read file size
                            long fileSize = Long.parseLong(fromServer.substring("FILE_SIZE:".length()));
                            
                            System.out.println("Recibiendo archivo de audio: " + fileName);
                            receiveAudioFile(socket, fileName, fileSize);
                        } else {
                            System.out.println(fromServer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageListener.start(); // Iniciar el hilo de recepción de mensajes

            // Leer mensajes del usuario y enviarlos al servidor
            String fromUser;

            // La entrada principal se usa para enviar datos al servidor, como el nombre y las opciones
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

    private static void receiveAudioFile(Socket socket, String fileName, long fileSize) throws IOException {
        File outputFile = new File("audios_recibidos/" + fileName);
        outputFile.getParentFile().mkdirs();

        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             InputStream inputStream = socket.getInputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytesRead = 0;

            while (totalBytesRead < fileSize && (bytesRead = inputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            System.out.println("Audio recibido y guardado en: " + outputFile.getAbsolutePath());
        }
    }
}