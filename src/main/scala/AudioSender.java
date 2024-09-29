import javax.sound.sampled.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AudioSender implements Runnable {
    private DatagramSocket socket;
    private InetAddress targetAddress;
    private int targetPort;

    public AudioSender(DatagramSocket socket, InetAddress targetAddress, int targetPort) {
        this.socket = socket;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
    }

    @Override
    public void run() {
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            TargetDataLine line = AudioSystem.getTargetDataLine(format);
            line.open(format);
            line.start();

            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = line.read(buffer, 0, buffer.length); // Captura audio del micrófono
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, targetAddress, targetPort);
                socket.send(packet); // Envía el paquete de audio
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
