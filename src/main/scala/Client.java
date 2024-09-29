import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            socket.setSoTimeout(30000); // 30 segundos de timeout
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            Thread messageListener = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        if (fromServer.startsWith("AUDIO_FILE:")) {
                            String fileName = fromServer.substring("AUDIO_FILE:".length());
                            fromServer = in.readLine(); // Read file size
                            long fileSize = Long.parseLong(fromServer.substring("FILE_SIZE:".length()));
                            
                            System.out.println("Recibiendo archivo de audio: " + fileName);
                            boolean success = receiveAudioFile(socket, fileName, fileSize);
                            if (success) {
                                out.println("FILE_RECEIVED_OK");
                                System.out.println("Recepción de audio completada y confirmada.");
                            } else {
                                out.println("FILE_RECEIVED_ERROR");
                                System.out.println("Error en la recepción del audio.");
                            }
                        } else {
                            System.out.println(fromServer);
                        }

                        if (fromServer.startsWith("GRUPO_AUDIO_FILE:")) {
                            String fileName = fromServer.substring("GRUPO_AUDIO_FILE:".length());
                            fromServer = in.readLine(); // Read file size
                            long fileSize = Long.parseLong(fromServer.substring("GRUPO_FILE_SIZE:".length()));
                            fromServer = in.readLine(); // Read sender name
                            String sender = fromServer.substring("GRUPO_SENDER:".length());
                            fromServer = in.readLine(); // Read group name
                            String groupName = fromServer.substring("GRUPO_NAME:".length());
                            
                            System.out.println("Recibiendo archivo de audio de grupo de " + sender + " en el grupo " + groupName + ": " + fileName);
                            boolean success = receiveGroupAudioFile(socket, fileName, fileSize, sender, groupName);
                            if (success) {
                                out.println("GRUPO_FILE_RECEIVED_OK");
                                System.out.println("Recepción de audio de grupo completada y confirmada.");
                            } else {
                                out.println("GRUPO_FILE_RECEIVED_ERROR");
                                System.out.println("Error en la recepción del audio de grupo.");
                            }
                        } else if (fromServer.startsWith("GRUPO_PROGRESS:")) {
                            int progress = Integer.parseInt(fromServer.substring("GRUPO_PROGRESS:".length()));
                            System.out.println("Progreso de recepción de audio de grupo: " + progress + "%");
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error en la recepción de mensajes: " + e.getMessage());
                }
            });
            messageListener.start();

            String fromUser;
            while ((fromUser = stdIn.readLine()) != null) {
                out.println(fromUser);
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    private static boolean receiveAudioFile(Socket socket, String fileName, long fileSize) throws IOException {
        File outputFile = new File("audios_recibidos/" + fileName);
        outputFile.getParentFile().mkdirs();

        try (InputStream inputStream = new BufferedInputStream(socket.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            long lastProgressUpdate = 0;

            while (totalBytesRead < fileSize) {
                bytesRead = inputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead));
                if (bytesRead == -1) break;
                
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Actualizar el progreso cada 1%
                if (totalBytesRead - lastProgressUpdate > fileSize / 100) {
                    int progress = (int)((totalBytesRead * 100) / fileSize);
                    System.out.println("Progreso: " + progress + "%");
                    lastProgressUpdate = totalBytesRead;
                }
            }

            if (totalBytesRead == fileSize) {
                System.out.println("Audio recibido y guardado en: " + outputFile.getAbsolutePath());
                return true;
            } else {
                System.out.println("Error: No se recibió el archivo completo. Bytes recibidos: " + totalBytesRead + " de " + fileSize);
                return false;
            }
        }
    }

    private static boolean receiveGroupAudioFile(Socket socket, String fileName, long fileSize, String sender, String groupName) throws IOException {
        File outputFile = new File("audios_grupo_recibidos/" + groupName + "/" + fileName);
        outputFile.getParentFile().mkdirs();
    
        try (InputStream inputStream = new BufferedInputStream(socket.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
    
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            long lastProgressUpdate = 0;
    
            while (totalBytesRead < fileSize) {
                bytesRead = inputStream.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalBytesRead));
                if (bytesRead == -1) break;
                
                fileOutputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
    
                // Actualizar el progreso cada 1%
                if (totalBytesRead - lastProgressUpdate > fileSize / 100) {
                    int progress = (int)((totalBytesRead * 100) / fileSize);
                    System.out.println("Progreso de audio de grupo: " + progress + "%");
                    lastProgressUpdate = totalBytesRead;
                }
            }
    
            if (totalBytesRead == fileSize) {
                System.out.println("Audio de grupo recibido de " + sender + " y guardado en: " + outputFile.getAbsolutePath());
                return true;
            } else {
                System.out.println("Error: No se recibió el archivo de grupo completo. Bytes recibidos: " + totalBytesRead + " de " + fileSize);
                return false;
            }
        }
    }
}