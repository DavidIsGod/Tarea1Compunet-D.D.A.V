public class Main {
    public static void main(String[] args) {
        int port = 12345;  // Puedes cambiar el puerto si lo deseas
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
