import javax.sound.sampled.*;
import java.net.*;
import java.util.Arrays;

public class CallHandler {
    private DatagramSocket udpSocket;
    private boolean isCalling;
    private InetAddress targetAddress;
    private int targetPort;

    public CallHandler() {
        try {
            this.udpSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // Inicia la transmisión de audio a través de UDP
    public void startCall(InetAddress targetAddress, int targetPort) {
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
        isCalling = true;

        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                byte[] buffer = new byte[512];
                System.out.println("Iniciando transmisión de audio...");

                while (isCalling) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);

                    if (bytesRead > 0) {
                        DatagramPacket packet = new DatagramPacket(buffer, bytesRead, targetAddress, targetPort);
                        udpSocket.send(packet);
                    }
                }

                microphone.close();
                System.out.println("Llamada finalizada.");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Inicia la escucha para recibir datos de audio a través de UDP
    public void startListening() {
        new Thread(() -> {
            try {
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
                speakers.open(format);
                speakers.start();

                byte[] buffer = new byte[512];

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);

                    speakers.write(packet.getData(), 0, packet.getLength());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Detener la llamada
    public void stopCall() {
        isCalling = false;
    }

    public boolean isCalling() {
        return isCalling;
    }

    public int getLocalPort() {
        return udpSocket.getLocalPort();
    }
}
