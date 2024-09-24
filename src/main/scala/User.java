public class User {
    private String username;
    private ClientHandler clientHandler;

    public User(String username, ClientHandler clientHandler) {
        this.username = username;
        this.clientHandler = clientHandler;
    }

    public String getUsername() {
        return username;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }
}

