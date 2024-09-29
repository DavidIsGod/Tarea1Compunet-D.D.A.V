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
            AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open(format);
            targetLine.start();

            byte[] buffer = new byte[4096];
            while (true) {
                int bytesRead = targetLine.read(buffer, 0, buffer.length);
                DatagramPacket packet = new DatagramPacket(buffer, bytesRead, targetAddress, targetPort);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
