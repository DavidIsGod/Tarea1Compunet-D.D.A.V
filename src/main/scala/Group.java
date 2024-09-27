import java.io.File;
import java.util.*;

public class Group {
    private String name;
    private Set<ClientHandler> members;

    public Group(String name) {
        this.name = name;
        this.members = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void addMember(ClientHandler member) {
        members.add(member);
    }

    public Set<ClientHandler> getMembers() {
        return members;
    }

    public void sendMessage(String message, String sender) {
        for (ClientHandler member : members) {
            member.sendMessage(message, sender);
        }
    }
    // Método para enviar un archivo de audio a todos los miembros del grupo
    public void sendAudio(File audioFile, String sender) {
        for (ClientHandler member : members) {
            member.sendAudio(audioFile, sender);  
        }
    }
}
